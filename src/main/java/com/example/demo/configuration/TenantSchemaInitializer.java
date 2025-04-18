package com.example.demo.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
@DependsOn("entityManagerFactory")
@RequiredArgsConstructor
public class TenantSchemaInitializer implements InitializingBean {

    private final Map<String, DataSource> multiTenantDataSourceProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<String, DataSource> entry : multiTenantDataSourceProvider.entrySet()) {
            String tenantId = entry.getKey();
            DataSource ds = entry.getValue();

            log.info("Creating schema for tenant: {}", tenantId);
            log.info("JDBC URL: {}", ds.getConnection().getMetaData().getURL());

            LocalContainerEntityManagerFactoryBean emf = buildEntityManagerFactory(ds, tenantId);
            emf.afterPropertiesSet();
        }
    }

    private LocalContainerEntityManagerFactoryBean buildEntityManagerFactory(DataSource ds, String tenantId) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setPackagesToScan("com.example.demo.entity");
        em.setPersistenceUnitName(tenantId);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "update");
        em.setJpaProperties(props);

        return em;
    }
}

