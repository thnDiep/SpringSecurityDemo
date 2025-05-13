package com.example.demo.config;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.constant.PredefinedRole;
import com.example.demo.constant.TenantId;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Configuration
public class ApplicationInitConfig {
    DataSource dataSource;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;
    List<String> tenantList = TenantId.getAllTenantId();

    @NonFinal
    @Value("${app.env:default}")
    String env;

    @PostConstruct
    public void init() {
        System.out.println("ENV = " + env);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            for(String tenantId : tenantList) {
                TenantContext.setCurrentTenant(tenantId);
                if(!isBatchSchemaInitialized(tenantId)) {
                    initializeBatchSchema();
                    log.info("Batch schema is initialized for " + tenantId);
                }
                if(!isDefaultSchemaInitialized(tenantId)) {
                    initializeDefaultSchema();
                    log.info("Default schema is initialized for " + tenantId);
                }
                initAdminAccount();
            }
        };
    }

    private void initializeBatchSchema() {
        ResourceDatabasePopulator popular = new ResourceDatabasePopulator(
                new ClassPathResource("org/springframework/batch/core/schema-mysql.sql")
        );
        popular.setContinueOnError(false);
        popular.execute(dataSource);
    }

    private boolean isBatchSchemaInitialized(String tenantId) {
        try {
            Connection conn = dataSource.getConnection();
            ResultSet rs = conn.getMetaData().getTables(tenantId, null, "batch_job_instance", null);
            return rs.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.SQL_ERROR);
        }
    }

    private void initializeDefaultSchema() {
        ResourceDatabasePopulator popular = new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql")
        );
        popular.setContinueOnError(false);
        popular.execute(dataSource);
    }

    private boolean isDefaultSchemaInitialized(String tenantId) {
        try {
            Connection conn = dataSource.getConnection();
            ResultSet rs = conn.getMetaData().getTables(tenantId, null, "app_user", null);
            return rs.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.SQL_ERROR);
        }
    }

    private void initAdminAccount() {
        if(userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.save(Role.builder()
                    .name(PredefinedRole.ADMIN_ROLE)
                    .description("Admin role")
                    .build());

            var roles = new HashSet<Role>();
            roles.add(adminRole);

            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(roles)
                    .build());

            log.warn("admin user has been created with default password: admin");
        }
    }
}
