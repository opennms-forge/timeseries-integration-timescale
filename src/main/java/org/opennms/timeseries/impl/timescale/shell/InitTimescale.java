package org.opennms.timeseries.impl.timescale.shell;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.opennms.timeseries.impl.timescale.util.DBUtils;

@Command(scope = "timescale", name = "init", description = "Initializes the database tables for the Timeseries Integration Timescale Plugin.")
@Service
public class InitTimescale implements Action {

    @Reference
    private DataSource dataSource;

    @Option(name = "-p", aliases = "--print-only", description = "Only print sql, not actually executing.")
    private boolean printOnly;

    @Override
    public Object execute() {

        DBUtils db = new DBUtils();
        try {
            System.out.println("Checking preconditions");
            Connection conn = dataSource.getConnection();
            db.watch(conn);
            Statement stmt = conn.createStatement();
            db.watch(stmt);

            ResultSet result = stmt.executeQuery("select count(*) from pg_extension where extname = 'timescaledb';");
            db.watch(result);
            result.next();
            if (result.getInt(1) < 1) {
                System.out.println("It looks like timescale plugin is not installed. Please install: https://docs.timescale.com/latest/getting-started/installation. Aborting.");
                return null;
            }
            System.out.println("Installing Timescale tables");
            executeQuery(stmt, "CREATE TABLE timescale_time_series(key TEXT NOT NULL, time TIMESTAMPTZ NOT NULL, value DOUBLE PRECISION NULL)");
            executeQuery(stmt, "SELECT create_hypertable('timescale_time_series', 'time');");
            // double check:
            stmt.execute("select * from timescale_time_series;"); // will throw exception if table doesn't exist
            executeQuery(stmt, "CREATE TABLE timescale_metric(key TEXT NOT NULL PRIMARY KEY)");
            executeQuery(stmt, "CREATE TABLE timescale_tag(fk_timescale_metric TEXT NOT NULL, key TEXT, value TEXT NOT NULL, type TEXT NOT NULL, UNIQUE (fk_timescale_metric, key, value, type))");
            System.out.println("Done. Enjoy!");
        } catch (SQLException e) {
            System.out.println("An SQLException occured:");
            e.printStackTrace(System.out);
        } finally {
            db.cleanUp();
        }
        return null;
    }

    private void executeQuery(Statement stmt, final String sql) throws SQLException {
        if(printOnly) {
            System.out.println(sql);
        } else {
            stmt.execute(sql);
        }
    }
}
