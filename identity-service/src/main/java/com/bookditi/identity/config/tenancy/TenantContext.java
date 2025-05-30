package com.bookditi.identity.config.tenancy;

import com.bookditi.identity.constant.TenantId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get() != null ? CURRENT_TENANT.get() : TenantId.DEFAULT_SCHEMA;
    }

    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
