package com.example.demo.config.tenancy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {
    private final String[] PUBLIC_ENDPOINTS = {"/users", "/auth/token", "/auth/introspect"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (Arrays.stream(PUBLIC_ENDPOINTS).anyMatch(path::startsWith) && request.getMethod().equalsIgnoreCase("POST")) {
            log.info("Path: {}", path);
            String tenantID = request.getHeader("X-Tenant-Id");
            tenantID = tenantID != null ? tenantID : TenantContext.DEFAULT_TENANT;
            TenantContext.setCurrentTenant(tenantID);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        TenantContext.clear();
    }
}
