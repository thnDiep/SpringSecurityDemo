package com.example.demo.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.datasource.AbstractDataSource;

import com.example.demo.config.tenancy.TenantContext;

@EnableConfigurationProperties
public class DataSourceConfig extends AbstractDataSource {
    private final DataSource delegate;

    public DataSourceConfig(DataSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = delegate.getConnection();
        try (Statement statement = conn.createStatement()) {
            statement.execute("USE " + TenantContext.getCurrentTenant());
        }
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = delegate.getConnection(username, password);
        try (Statement statement = conn.createStatement()) {
            statement.execute("USE " + TenantContext.getCurrentTenant());
        }
        return conn;
    }
}
