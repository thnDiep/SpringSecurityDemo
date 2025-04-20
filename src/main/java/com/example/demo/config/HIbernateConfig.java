package com.example.demo.config;

import com.example.demo.config.tenancy.MultiTenantConnectionProviderImpl;
import com.example.demo.config.tenancy.TenantDataSourceProperties;
import com.example.demo.config.tenancy.TenantIdentifierResolver;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TenantDataSourceProperties.class)
public class HIbernateConfig {

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider(TenantDataSourceProperties props) {
        return new MultiTenantConnectionProviderImpl(props);
    }

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver(){
        return new TenantIdentifierResolver();
    }
}
