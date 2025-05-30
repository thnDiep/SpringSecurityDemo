package com.bookditi.identity.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bookditi.identity.config.tenancy.TenantContext;
import com.bookditi.identity.constant.PredefinedRole;
import com.bookditi.identity.constant.TenantId;
import com.bookditi.identity.entity.Role;
import com.bookditi.identity.entity.User;
import com.bookditi.identity.exception.AppException;
import com.bookditi.identity.exception.ErrorCode;
import com.bookditi.identity.repository.RoleRepository;
import com.bookditi.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

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

    @NonFinal
    @Value("${default-admin-password:admin}")
    String password;

    @PostConstruct
    public void init() {
        log.info("ENV = " + env);
    }

    @Bean
    @Profile("!test")
    ApplicationRunner applicationRunner() {
        return args -> {
            for (String tenantId : tenantList) {
                TenantContext.setCurrentTenant(tenantId);
                if (!isBatchSchemaInitialized(tenantId)) {
                    initializeBatchSchema();
                    log.info("Batch schema is initialized for " + tenantId);
                }
                if (!isDefaultSchemaInitialized(tenantId)) {
                    initializeDefaultSchema();
                    log.info("Default schema is initialized for " + tenantId);
                }
                initAdminAccount();
            }
        };
    }

    private void initializeBatchSchema() {
        ResourceDatabasePopulator popular =
                new ResourceDatabasePopulator(new ClassPathResource("org/springframework/batch/core/schema-mysql.sql"));
        popular.setContinueOnError(false);
        popular.execute(dataSource);
    }

    private boolean isBatchSchemaInitialized(String tenantId) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            ResultSet rs = conn.getMetaData().getTables(tenantId, null, "batch_job_instance", null);
            return rs.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.SQL_ERROR);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private void initializeDefaultSchema() {
        ResourceDatabasePopulator popular = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        popular.setContinueOnError(false);
        popular.execute(dataSource);
    }

    private boolean isDefaultSchemaInitialized(String tenantId) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            ResultSet rs = conn.getMetaData().getTables(tenantId, null, "app_user", null);
            return rs.next();
        } catch (SQLException e) {
            throw new AppException(ErrorCode.SQL_ERROR);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private void initAdminAccount() {
        String username = "admin";
        if (userRepository.findByUsername(username).isEmpty()) {
            Role adminRole = roleRepository.save(Role.builder()
                    .name(PredefinedRole.ADMIN_ROLE)
                    .description("Admin role")
                    .build());

            var roles = new HashSet<Role>();
            roles.add(adminRole);

            userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .roles(roles)
                    .build());

            log.warn("admin user has been created with default password: admin");
        }
    }
}
