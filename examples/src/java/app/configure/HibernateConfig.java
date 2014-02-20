package app.configure;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
import org.reflections.Reflections;

import javax.persistence.Entity;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hibernate.cfg.AvailableSettings.*;

public class HibernateConfig {

    public static void configure(org.hibernate.cfg.Configuration configuration) {
        configuration.setProperty(Environment.CONNECTION_PROVIDER, AppConnectionProvider.class.getName());
        configuration.setProperty(CURRENT_SESSION_CONTEXT_CLASS, "thread");
        configuration.setProperty(DIALECT, H2Dialect.class.getName());
        configuration.setProperty(HBM2DDL_AUTO, "create-drop");
        configuration.setProperty(USE_SECOND_LEVEL_CACHE, Boolean.FALSE.toString());
        configuration.setProperty(USE_QUERY_CACHE, Boolean.FALSE.toString());
        configuration.setProperty(SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(FORMAT_SQL, Boolean.TRUE.toString());
        addAnnotatedClasses(configuration);
    }

    protected static Configuration addAnnotatedClasses(Configuration configuration) {
        for (Class<?> aClass : new Reflections("app.models").getTypesAnnotatedWith(Entity.class)) {
            // don't load classes that have generic type parameters as hibernate can not deal with them unless used as superclasses
            if (aClass.getTypeParameters().length == 0) {
                configuration.addAnnotatedClass(aClass);
            }
        }
        return configuration;
    }

    public static class AppConnectionProvider implements ConnectionProvider {

        DataSource getDataSource() {
            JdbcDataSource jdbcDataSource = new JdbcDataSource();
            jdbcDataSource.setURL("jdbc:h2:mem");
            return jdbcDataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return getDataSource().getConnection();
        }

        @Override
        public void closeConnection(Connection conn) throws SQLException {
            conn.close();
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return false;
        }

        @Override
        public boolean isUnwrappableAs(Class unwrapType) {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            return null;
        }
    }
}
