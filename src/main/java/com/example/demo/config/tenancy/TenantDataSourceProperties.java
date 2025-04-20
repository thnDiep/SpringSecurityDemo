package com.example.demo.config.tenancy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource")
public class TenantDataSourceProperties {
    private Map<String, DataSourceConfig> datasources = new HashMap<>();

    public Map<String, DataSourceConfig> getDatasources() {
        return datasources;
    }

    @Setter
    @Getter
    public static class DataSourceConfig {
        private String url;
        private String username;
        private String password;
        // getter, setter
    }
}