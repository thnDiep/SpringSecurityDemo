package com.bookditi.identity.config.websocket;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import com.bookditi.identity.config.security.CustomJwtDecoder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StompJwtAuthenticator {
    CustomJwtDecoder decoder;
    JwtAuthenticationConverter converter;

    public Authentication authenticate(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }

        String jwtToken = token.substring(7); // remove "Bearer "
        Jwt jwt = decoder.decode(jwtToken);
        return converter.convert(jwt);
    }
}
