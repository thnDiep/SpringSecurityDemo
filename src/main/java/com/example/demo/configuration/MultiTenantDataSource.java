package com.example.demo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class MultiTenantDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        log.info("Current tenant: {}", TenantContext.getCurrentTenant());
        return TenantContext.getCurrentTenant();
    }
}
