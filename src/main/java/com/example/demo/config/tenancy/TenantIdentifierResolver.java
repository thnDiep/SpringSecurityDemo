package com.example.demo.config.tenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    @Override
    public Object resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();

        return (tenantId != null) ? tenantId : TenantContext.DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
