/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.timeseries.impl.timescale;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.opennms.integration.api.v1.timeseries.AbstractStorageIntegrationTest;
import org.opennms.integration.api.v1.timeseries.TimeSeriesStorage;
import org.opennms.timeseries.impl.timescale.util.DBUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimescaleStorageTest extends AbstractStorageIntegrationTest {


    public static GenericContainer<?> container;

    private DataSource dataSource;
    private TimescaleStorage timescale;

    @BeforeClass
    public static void setUpContainer() {
        container = new GenericContainer<>("timescale/timescaledb:latest-pg12")
                .withExposedPorts(5432)
                .withEnv("POSTGRES_PASSWORD", "password")
                .withEnv("TIMESCALEDB_TELEMETRY", "off")
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer(new Slf4jLogConsumer(log));
        container.start();
    }

    @AfterClass
    public static void tearDownContainer() {
        container.stop();
    }

    private static DataSource createDatasource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://localhost:%s/", container.getFirstMappedPort()));
        config.setUsername("postgres");
        config.setPassword("password");
        return new HikariDataSource(config);
    }

    @Before
    public void setUp() throws Exception {
        dataSource = createDatasource();
        timescale = new TimescaleStorage(dataSource);
        timescale.init();
        super.setUp();
    }

    @After
    public void tearDown() throws SQLException {
        dropTables(dataSource);
    }

    @Override
    protected TimeSeriesStorage createStorage() {
        return timescale;
    }

    private void dropTables(final DataSource dataSource) throws SQLException {
        DBUtils db = new DBUtils();
        try {
            Connection conn = dataSource.getConnection();
            db.watch(conn);
            Statement stmt = conn.createStatement();
            db.watch(stmt);
            stmt.execute("DROP TABLE timescale_tag;");
            stmt.execute("DROP TABLE timescale_metric;");
            stmt.execute("DROP TABLE timescale_time_series;");
        } finally {
            db.cleanUp();
        }
    }
}
