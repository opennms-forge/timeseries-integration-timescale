package org.opennms.timeseries.impl.timescale.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The timescale plugin uses the opennms database. But it needs extra tables.
 * This class offers helper methods to check for and create the tables.
 */
@AllArgsConstructor
@Slf4j
public class TimescaleDatabaseInitializer {

    private final DataSource dataSource;

    boolean isTimescaleExtensionInstalled() throws SQLException {
        DBUtils db = new DBUtils();
        try {
            Connection conn = dataSource.getConnection();
            db.watch(conn);
            Statement stmt = conn.createStatement();
            db.watch(stmt);

            ResultSet result = stmt.executeQuery("select count(*) from pg_extension where extname = 'timescaledb';");
            db.watch(result);
            result.next();
            return result.getInt(1) > 0;
        } finally {
            db.cleanUp();
        }
    }

    boolean isTimescaleTableExisting(String tableName) throws SQLException {
        DBUtils db = new DBUtils();
        try {
            Connection conn = dataSource.getConnection();
            db.watch(conn);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName, null);
            db.watch(tables);
            return tables.next();
        } finally {
            db.cleanUp();
        }
    }

    boolean isTimescaleTablesExisting() throws SQLException {
        return isTimescaleTableExisting(TableNames.TIMESCALE_TIME_SERIES)
                && isTimescaleTableExisting(TableNames.TIMESCALE_METRIC)
                && isTimescaleTableExisting(TableNames.TIMESCALE_TAG);
    }

    void createTables() throws SQLException {
        DBUtils db = new DBUtils();
        try {
            Connection conn = dataSource.getConnection();
            db.watch(conn);
            Statement stmt = conn.createStatement();
            db.watch(stmt);
            executeQuery(stmt, "CREATE TABLE timescale_time_series(key TEXT NOT NULL, time TIMESTAMPTZ NOT NULL, value DOUBLE PRECISION NULL)");
            executeQuery(stmt, "SELECT create_hypertable('timescale_time_series', 'time');");
            executeQuery(stmt, "CREATE TABLE timescale_metric(key TEXT NOT NULL PRIMARY KEY)");
            executeQuery(stmt, "CREATE TABLE timescale_tag(fk_timescale_metric TEXT NOT NULL, key TEXT, value TEXT NOT NULL, type TEXT NOT NULL, UNIQUE (fk_timescale_metric, key, value, type))");
        } finally {
            db.cleanUp();
        }
    }

    private void executeQuery(Statement stmt, final String sql) throws SQLException {
        log.info(sql);
        stmt.execute(sql);
    }

    public void initializeIfNeeded() throws SQLException {

        // Check Plugin
        if (!isTimescaleExtensionInstalled()) {
            log.error("It looks like timescale plugin is not installed. Please install: https://docs.timescale.com/latest/getting-started/installation. Aborting.");
            return;
        }

        // Check and create tables
        if (isTimescaleTablesExisting()) {
            log.info("Timescale tables exist. We are good to go.");
        } else {
            log.info("Timescale tables are missing. Will create them now.");
            createTables();
            log.info("Timescale tables created.");
        }
    }
}
