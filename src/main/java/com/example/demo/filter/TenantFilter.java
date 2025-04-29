package com.example.demo.filter;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.GlobalExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tenantId = request.getHeader("X-Tenant-Id");

        try {
            if (tenantId == null) {
                sendErrorResponse(response, ErrorCode.INVALID_TENANT_ID);
                return;
            }

            if (request.getRequestURI().startsWith("/auth") && !tenantId.equals("jwt_schema")) {
                sendErrorResponse(response, ErrorCode.UNAUTHENTICATED);
                return;
            }

            TenantContext.setCurrentTenant(tenantId);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(GlobalExceptionHandler.apiResponseToString(errorCode));
    }
}
