package com.example.demo.config.tenancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {
    private final DataSource defaultDataSource;

    @Override
    public Connection getConnection(String tenantId) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            connection.createStatement().execute("USE " + tenantId);
        } catch (SQLException e) {
            throw new HibernateException("Could not switch to schema: " + tenantId, e);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantId, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return defaultDataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }

    @Override
    public boolean isUnwrappableAs(Class aClass) {
        return false;
    }
}
