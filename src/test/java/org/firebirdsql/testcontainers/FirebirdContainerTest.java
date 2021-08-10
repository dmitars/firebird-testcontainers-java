package org.firebirdsql.testcontainers;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.firebirdsql.gds.impl.GDSServerVersion;
import org.firebirdsql.jdbc.FirebirdConnection;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.firebirdsql.testcontainers.FirebirdTestImages.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rnorth.visibleassertions.VisibleAssertions.assertFalse;

public class FirebirdContainerTest {

    private ServerRuntime initServerRuntimeFrom(FirebirdContainer<?> firebirdContainer){
        return ServerRuntime.builder()
                .url(firebirdContainer.getJdbcUrl())
                .user(firebirdContainer.getUsername())
                .password(firebirdContainer.getPassword())
                .jdbcDriver(firebirdContainer.getDriverClassName())
                .addConfig("cayenne-project.xml")
                .build();
    }

    @Test
    public void testWithSysdbaPassword() throws SQLException {
        FirebirdContainer<?> firebirdContainer = new FirebirdContainer<>("jacobalberty/firebird");
        final String sysdbaPassword = "sysdbapassword";
        firebirdContainer.withUsername("sysdba");
        firebirdContainer.withPassword(sysdbaPassword);
        firebirdContainer.start();
        ServerRuntime cayenneRuntime = initServerRuntimeFrom(firebirdContainer);
        ObjectContext context = cayenneRuntime.newContext();
        context.commitChanges();
    }

    @Test
    public void testWithEnableLegacyClientAuth() throws SQLException {
        try (FirebirdContainer<?> container = new FirebirdContainer<>(FIREBIRD_TEST_IMAGE)
                .withEnableLegacyClientAuth()) {
            container.start();

            ServerRuntime cayenneRuntime = initServerRuntimeFrom(container);
            ObjectContext context = cayenneRuntime.newContext();

            context.commitChanges();
        }
    }

    @Test
    public void testWithEnableLegacyClientAuth_jdbcUrlIncludeAuthPlugins_default() {
        try (FirebirdContainer<?> container = new FirebirdContainer<>(FIREBIRD_TEST_IMAGE)
                .withEnableLegacyClientAuth()) {
            container.start();

            String jdbcUrl = container.getJdbcUrl();
            assertThat(jdbcUrl, allOf(
                    containsString("?"),
                    containsString("authPlugins=Srp256,Srp,Legacy_Auth")));
        }
    }

    @Test
    public void testWithEnableLegacyClientAuth_jdbcUrlIncludeAuthPlugins_explicitlySet() {
        try (FirebirdContainer<?> container = new FirebirdContainer<>(FIREBIRD_TEST_IMAGE)
                .withEnableLegacyClientAuth()
                .withUrlParam("authPlugins", "Legacy_Auth")) {
            container.start();

            String jdbcUrl = container.getJdbcUrl();
            assertThat(jdbcUrl, allOf(
                    containsString("?"),
                    containsString("authPlugins=Legacy_Auth")));
        }
    }

    @Test
    public void testWithEnableWireCrypt() throws SQLException {
        try (FirebirdContainer<?> container = new FirebirdContainer<>(FIREBIRD_TEST_IMAGE).withEnableWireCrypt()) {
            container.start();

            ServerRuntime cayenneRuntime = initServerRuntimeFrom(container);

            if (FirebirdContainer.isWireEncryptionSupported()) {
                // Check connecting with wire crypt
                try (Connection connection = cayenneRuntime.getDataSource().getConnection()) {
                    GDSServerVersion serverVersion = connection.unwrap(FirebirdConnection.class).getFbDatabase().getServerVersion();
                    assertTrue("Expected encryption in use", serverVersion.isWireEncryptionUsed());
                }
            }

            try (Connection connection = container.createConnection("?wireCrypt=disabled")) {
                GDSServerVersion serverVersion = connection.unwrap(FirebirdConnection.class).getFbDatabase().getServerVersion();
                assertFalse("Expected encryption not in use", serverVersion.isWireEncryptionUsed());
            }
        }
    }

    /**
     * The 2.5 images of jacobalberty/firebird handle FIREBIRD_DATABASE and need an absolute path to access the database
     */
    @Test
    public void test259_scImage() throws Exception {
        try (FirebirdContainer<?> container = new FirebirdContainer<>(FIREBIRD_259_SC_IMAGE).withDatabaseName("test")) {
            assertEquals("Expect original database name before start",
                    "test", container.getDatabaseName());

            container.start();

            assertEquals("Expect modified database name after start",
                    "/firebird/data/test", container.getDatabaseName());
            ServerRuntime cayenneRuntime = initServerRuntimeFrom(container);
            try (Connection connection = cayenneRuntime.getDataSource().getConnection()) {
                assertTrue(connection.isValid(1000));
            }
        }
    }

    /**
     * The 2.5 images of jacobalberty/firebird handle FIREBIRD_DATABASE and need an absolute path to access the database
     */
    @Test
    public void test259_ssImage() throws Exception {
        try (FirebirdContainer<?> container = new FirebirdContainer<>(FIREBIRD_259_SS_IMAGE).withDatabaseName("test")) {
            assertEquals("Expect original database name before start",
                    "test", container.getDatabaseName());

            container.start();

            assertEquals("Expect modified database name after start",
                    "/firebird/data/test", container.getDatabaseName());
            ServerRuntime cayenneRuntime = initServerRuntimeFrom(container);
            try (Connection connection = cayenneRuntime.getDataSource().getConnection()) {
                assertTrue(connection.isValid(1000));
            }
        }
    }

    @Test
    public void testWithAdditionalUrlParamInJdbcUrl() {
        try (FirebirdContainer<?> firebird = new FirebirdContainer<>(FIREBIRD_TEST_IMAGE)
                .withUrlParam("charSet", "utf-8")
                .withUrlParam("blobBufferSize", "2048")) {

            firebird.start();
            String jdbcUrl = firebird.getJdbcUrl();
            assertThat(jdbcUrl, allOf(
                    containsString("?"),
                    containsString("&"),
                    containsString("blobBufferSize=2048"),
                    containsString("charSet=utf-8")));
        }
    }
}
