package com.example.demo.configuration;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceConfig {
    private final Environment env;

    public DataSourceConfig (Environment environment) {
        this.env = environment;
    }

    @Bean(name = "defaultDataSourceProperties")
    @ConfigurationProperties(prefix = "default.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "tenant1DataSourceProperties")
    @ConfigurationProperties(prefix = "tenant1.datasource")
    public DataSourceProperties tenant1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "tenant2DataSourceProperties")
    @ConfigurationProperties(prefix = "tenant2.datasource")
    public DataSourceProperties tenant2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("defaultDataSourceProperties") DataSourceProperties defaultProps,
            @Qualifier("tenant1DataSourceProperties") DataSourceProperties tenant1Props,
            @Qualifier("tenant2DataSourceProperties") DataSourceProperties tenant2Props) {
        MultiTenantDataSource dataSource = new MultiTenantDataSource();
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        log.debug("--------URL {}", tenant1Props.getUrl());


        // Default datasource
        resolvedDataSources.put(TenantContext.DEFAULT_TENANT, DataSourceBuilder.create()
                .url(defaultProps.getUrl())
                .username(defaultProps.getUsername())
                .password(defaultProps.getPassword())
                .driverClassName(defaultProps.getDriverClassName())
                .build());

        // Tenant 1
        resolvedDataSources.put("tenant1", DataSourceBuilder.create()
                .url(tenant1Props.getUrl())
                .username(tenant1Props.getUsername())
                .password(tenant1Props.getPassword())
                .driverClassName(tenant1Props.getDriverClassName())
                .build());

        // Tenant 2
        resolvedDataSources.put("tenant2", DataSourceBuilder.create()
                .url(tenant2Props.getUrl())
                .username(tenant2Props.getUsername())
                .password(tenant2Props.getPassword())
                .driverClassName(tenant2Props.getDriverClassName())
                .build());

        dataSource.setTargetDataSources(resolvedDataSources);
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(TenantContext.DEFAULT_TENANT));
        dataSource.afterPropertiesSet();
        return dataSource;
    }

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.example.demo.entity");
//        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        return em;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
}
