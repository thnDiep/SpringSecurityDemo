package com.example.demo.config.tenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.DatabaseConnectionInfo;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
    private final Map<String, DataSource> dataSources = new HashMap<>();

    public MultiTenantConnectionProviderImpl(TenantDataSourceProperties tenantProps) {
        tenantProps.getDatasources().forEach((tenantId, config) -> {
            dataSources.put(tenantId, buildDataSource(config));
        });
    }

    private DataSource buildDataSource(TenantDataSourceProperties.DataSourceConfig config) {
        return DataSourceBuilder.create()
                .url(config.getUrl())
                .username(config.getUsername())
                .password(config.getPassword())
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    private DataSource selectDataSource(String tenantId) {
        return dataSources.get(tenantId);
    }

    @Override
    public Connection getConnection(Object tenantId) throws SQLException {
        String tenant = String.valueOf(tenantId);
        return selectDataSource(tenant).getConnection();
    }

    @Override
    public void releaseConnection(Object o, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return selectDataSource(TenantContext.DEFAULT_TENANT).getConnection();
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
