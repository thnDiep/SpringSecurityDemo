package com.example.demo.config.tenancy;

import com.example.demo.config.DataSourceConfig;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateConfig  {
    @Bean(name="baseDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource baseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="tenantDataSource")
    @Primary
    public DataSource dataSource() {
        return new DataSourceConfig(baseDataSource());
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            MultiTenantConnectionProvider<String> tenantConnectionProviderImpl,
            CurrentTenantIdentifierResolver<String> tenantIdentifierResolver) {

        Map<String, Object> hibernateProps = new HashMap<>();
        hibernateProps.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, tenantConnectionProviderImpl);
        hibernateProps.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        hibernateProps.put(Environment.HBM2DDL_AUTO, "update");

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan("com.example.demo.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(hibernateProps);
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
