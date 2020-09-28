package org.opennms.timeseries.impl.timescale.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.Duration;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseInitializerTest {
    public static GenericContainer<?> container;

    private TimescaleDatabaseInitializer initializer;

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
    public static void tearDown() {
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
    public void setUp() {
        this.initializer = new TimescaleDatabaseInitializer(createDatasource());
    }

    @Test
    public void shouldCreateTables() throws SQLException {
        assertTrue(initializer.isTimescaleExtensionInstalled());

        assertFalse(initializer.isTimescaleTableExisting(TableNames.TIMESCALE_TIME_SERIES));
        assertFalse(initializer.isTimescaleTableExisting(TableNames.TIMESCALE_METRIC));
        assertFalse(initializer.isTimescaleTableExisting(TableNames.TIMESCALE_TAG));

        initializer.createTables();

        assertTrue(initializer.isTimescaleTableExisting(TableNames.TIMESCALE_TIME_SERIES));
        assertTrue(initializer.isTimescaleTableExisting(TableNames.TIMESCALE_METRIC));
        assertTrue(initializer.isTimescaleTableExisting(TableNames.TIMESCALE_TAG));
    }
}
