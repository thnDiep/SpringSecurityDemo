package com.example.demo.service;

import com.example.demo.config.tenancy.TenantContext;
import com.example.demo.constant.PredefineRole;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSeedService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createAdminIfNotExists(String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.save(Role.builder()
                    .name(PredefineRole.ADMIN_ROLE)
                    .description("Admin role")
                    .build());

            Role userRole = roleRepository.save(Role.builder()
                    .name(PredefineRole.USER_ROLE)
                    .description("User role")
                    .build());

            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(adminRole))
                    .build());

            log.warn("Admin created for tenant '{}'", tenantId);
        }
        TenantContext.clear();
    }
}
