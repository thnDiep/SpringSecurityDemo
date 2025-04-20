//package com.example.demo.config;
//
//import com.example.demo.config.tenancy.TenantContext;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//@Slf4j
//public class MultiTenantDataSource extends AbstractRoutingDataSource {
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return TenantContext.getCurrentTenant();
//    }
//
//}
