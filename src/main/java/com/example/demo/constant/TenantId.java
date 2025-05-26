package com.example.demo.constant;

import java.util.List;

public final class TenantId {
    public static final String DEFAULT_SCHEMA = "default_schema";
    public static final String JWT_SCHEMA = "jwt_schema";
    public static final String DIGEST_SCHEMA = "digest_schema";

    private TenantId() {}

    public static List<String> getAllTenantId() {
        return List.of(DEFAULT_SCHEMA, JWT_SCHEMA, DIGEST_SCHEMA);
    }
}
