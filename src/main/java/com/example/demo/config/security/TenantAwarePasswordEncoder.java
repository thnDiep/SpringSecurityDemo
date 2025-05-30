package com.example.demo.config.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.constant.TenantId;

public class TenantAwarePasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return getDelegate().encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return getDelegate().matches(rawPassword, encodedPassword);
    }

    private PasswordEncoder getDelegate() {
        String tenant = TenantContext.getCurrentTenant();
        if (TenantId.DIGEST_SCHEMA.equals(tenant)) {
            return NoOpPasswordEncoder.getInstance();
        } else {
            return new BCryptPasswordEncoder(10);
        }
    }
}
