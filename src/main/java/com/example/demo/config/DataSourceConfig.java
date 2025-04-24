package com.example.demo.config;

import com.example.demo.config.tenancy.TenantContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@EnableConfigurationProperties
public class DataSourceConfig extends AbstractDataSource {
    private final DataSource delegate;

    public DataSourceConfig(DataSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = delegate.getConnection();
        conn.createStatement().execute("USE " + TenantContext.getCurrentTenant());
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = delegate.getConnection(username, password);
        conn.createStatement().execute("USE " + TenantContext.getCurrentTenant());
        return conn;
    }
}
