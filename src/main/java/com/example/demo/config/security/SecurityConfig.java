package com.example.demo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.constant.TenantId;
import com.example.demo.service.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] JWT_PUBLIC_ENDPOINTS = {"/users", "/auth/token", "/auth/introspect"};
    private final String[] UI_PUBLIC_ENDPOINTS = {
        "/admin-dashboard.html", "/chat.html", "/css/**", "/js/**", "/supports/**"
    };

    CustomJwtDecoder customJwtDecoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Bean
    @Order(1)
    SecurityFilterChain digestChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher(request -> TenantId.DIGEST_SCHEMA.equals(TenantContext.getCurrentTenant()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, "/users")
                        .permitAll()
                        .requestMatchers(UI_PUBLIC_ENDPOINTS)
                        .permitAll() // WebSocket
                        .requestMatchers("/auth/**")
                        .denyAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(digestEntryPoint()))
                .addFilterBefore(digestAuthenticationFilter(), BasicAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain jwtChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, JWT_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .requestMatchers(UI_PUBLIC_ENDPOINTS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                //  token -> (JwtDecoder) -> Jwt -> (JwtAuthenticationConverter) -> Authentication
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(customJwtDecoder).jwtAuthenticationConverter(jwtConverter()))
                        .authenticationEntryPoint(new JwtEntryPoint()));
        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    DigestAuthenticationEntryPoint digestEntryPoint() {
        DigestAuthenticationEntryPoint result = new DigestAuthenticationEntryPoint();
        result.setRealmName("realm-name");
        result.setKey("3028472b-da34-4501-bfd8-a355c42bdf92");
        return result;
    }

    @Bean
    DigestAuthenticationFilter digestAuthenticationFilter() {
        DigestAuthenticationFilter result = new DigestAuthenticationFilter();
        result.setUserDetailsService(userDetailsService);
        result.setCreateAuthenticatedToken(true);
        result.setAuthenticationEntryPoint(digestEntryPoint());
        return result;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new TenantAwarePasswordEncoder();
    }
}
