package com.example.demo.config.digest;

import com.example.demo.filter.TenantFilter;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

@Configuration
@EnableAutoConfiguration
@EnableMethodSecurity
public class DigestSecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {"/users"};
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(digestAuthenticationEntryPoint()))
                .addFilterBefore(new TenantFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(digestAuthenticationFilter(), BasicAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    DigestAuthenticationEntryPoint digestAuthenticationEntryPoint() {
        DigestAuthenticationEntryPoint result = new DigestAuthenticationEntryPoint();
        result.setRealmName("realm-name");
        result.setNonceValiditySeconds(300);
        result.setKey("3028472b-da34-4501-bfd8-a355c42bdf92");
        return result;
    }

    @Bean
    DigestAuthenticationFilter digestAuthenticationFilter() {
        DigestAuthenticationFilter result = new DigestAuthenticationFilter();
        result.setUserDetailsService(userDetailsService);
        result.setCreateAuthenticatedToken(true);
        result.setAuthenticationEntryPoint(digestAuthenticationEntryPoint());
        return result;
    }
}
