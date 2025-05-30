package com.bookditi.identity.config.tenancy;

import static org.hibernate.cfg.MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER;
import static org.hibernate.cfg.MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER;
import static org.hibernate.cfg.SchemaToolingSettings.HBM2DDL_AUTO;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.bookditi.identity.config.DataSourceConfig;

@Configuration
public class HibernateConfig {
    @Bean(name = "baseDataSource")
    @ConfigurationProperties("spring.datasource")
    @Profile("!test")
    public DataSource baseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "tenantDataSource")
    @Primary
    @Profile("!test")
    public DataSource dataSource() {
        return new DataSourceConfig(baseDataSource());
    }

    @Bean
    @Profile("!test")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            MultiTenantConnectionProvider<String> tenantConnectionProviderImpl,
            CurrentTenantIdentifierResolver<String> tenantIdentifierResolver) {

        Map<String, Object> hibernateProps = new HashMap<>();
        hibernateProps.put(MULTI_TENANT_CONNECTION_PROVIDER, tenantConnectionProviderImpl);
        hibernateProps.put(MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        hibernateProps.put(HBM2DDL_AUTO, "update");

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan("com.bookditi.identity.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(hibernateProps);
        return em;
    }

    @Bean
    @Profile("!test")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
