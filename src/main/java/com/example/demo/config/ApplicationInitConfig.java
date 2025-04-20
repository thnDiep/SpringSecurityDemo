package com.example.demo.config;

import com.example.demo.service.UserSeedService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Configuration
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    @Value("#{'${tenants}'.split(',')}")
    private List<String> tenantList;

    @Bean
    ApplicationRunner applicationRunner(UserSeedService seedService) {
        return args -> {
            for (String tenantId : tenantList) {
                seedService.createAdminIfNotExists(tenantId);
            }
        };
    }
}
