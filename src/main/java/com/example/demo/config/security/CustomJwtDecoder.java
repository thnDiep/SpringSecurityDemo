package com.example.demo.config.security;

import java.text.ParseException;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    private final AuthenticationService authenticationService;

    public CustomJwtDecoder(@Lazy AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        SecretKey secretKey = new SecretKeySpec(signerKey.getBytes(), "HS512");
        Jwt jwt = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build()
                .decode(token);

        // Need to check later
        List<String> audience = jwt.getAudience();
        String tenantId = !audience.isEmpty() ? audience.get(0) : null;
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        }

        try {
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build(), false);

            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (ParseException | JOSEException e) {
            throw new JwtException(e.getMessage());
        }
        return jwt;
    }
}
