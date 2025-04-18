package com.example.demo.configuration;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.demo.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class DataSourceConfig {
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties defaultProps() {
        return new DataSourceProperties();
    }

    @Bean(name = "tenant1DataSource")
    @ConfigurationProperties(prefix = "tenant1.datasource")
    public DataSourceProperties tenant1Props() {
        return new DataSourceProperties();
    }

    @Bean(name = "tenant2DataSource")
    @ConfigurationProperties(prefix = "tenant2.datasource")
    public DataSourceProperties tenant2Props() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("defaultDataSource") DataSourceProperties defaultProps,
                                 @Qualifier("tenant1DataSource") DataSourceProperties tenant1Props,
                                 @Qualifier("tenant2DataSource") DataSourceProperties tenant2Props) {
        MultiTenantDataSource dataSource = new MultiTenantDataSource();
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        resolvedDataSources.put(TenantContext.DEFAULT_TENANT, buildDataSource(defaultProps));
        resolvedDataSources.put("tenant1", buildDataSource(tenant1Props));
        resolvedDataSources.put("tenant2", buildDataSource(tenant2Props));

        dataSource.setTargetDataSources(resolvedDataSources);
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(TenantContext.DEFAULT_TENANT));
        dataSource.afterPropertiesSet();
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.demo.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private DataSource buildDataSource(DataSourceProperties props) {
        return DataSourceBuilder.create()
                .url(props.getUrl())
                .username(props.getUsername())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .build();
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
//        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
        properties.put(org.hibernate.cfg.Environment.FORMAT_SQL, true);
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update");
        return properties;
    }


    @Bean
    @Primary
    public Map<String, DataSource> multiTenantDataSourceProvider(
            @Qualifier("defaultDataSource") DataSourceProperties defaultProps,
            @Qualifier("tenant1DataSource") DataSourceProperties tenant1Props,
            @Qualifier("tenant2DataSource") DataSourceProperties tenant2Props) {

        return Map.of(
                "default", buildDataSource(defaultProps),
                "tenant1", buildDataSource(tenant1Props),
                "tenant2", buildDataSource(tenant2Props)
        );
    }


}




