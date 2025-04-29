package com.example.demo.config.tenancy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    public static final String DEFAULT_TENANT = "jwt_schema";

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get() != null ? CURRENT_TENANT.get() : DEFAULT_TENANT;
    }

    public static void setCurrentTenant(String tenant) {
        log.info("Tenant set to: {}", TenantContext.getCurrentTenant());
        CURRENT_TENANT.set(tenant != null ? tenant : DEFAULT_TENANT);
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
