package com.bookditi.identity.service;

import java.util.HashSet;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookditi.identity.constant.PredefinedRole;
import com.bookditi.identity.dto.filter.UserSearchFilter;
import com.bookditi.identity.dto.pagination.PaginationResponse;
import com.bookditi.identity.dto.request.UserCreationRequest;
import com.bookditi.identity.dto.request.UserUpdateRequest;
import com.bookditi.identity.dto.response.UserResponse;
import com.bookditi.identity.entity.Role;
import com.bookditi.identity.entity.User;
import com.bookditi.identity.exception.AppException;
import com.bookditi.identity.exception.ErrorCode;
import com.bookditi.identity.mapper.PaginationMapper;
import com.bookditi.identity.mapper.UserMapper;
import com.bookditi.identity.repository.RoleRepository;
import com.bookditi.identity.repository.UserRepository;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PaginationResponse<UserResponse> searchUsers(UserSearchFilter filter, int page) {
        Pageable pageable = PageRequest.of(page, 2, Sort.by("id").ascending());

        Page<UserResponse> userResponsePage =
                userRepository.searchUsers(filter, pageable).map(userMapper::toUserResponse);
        return PaginationResponse.<UserResponse>builder()
                .pagination(PaginationMapper.toPaginationMeta(userResponsePage))
                .data(userResponsePage.getContent())
                .build();
    }

    //    @PreAuthorize("hasAuthority('APPROVE_POST')")
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "userPages", key = "#page")
    public PaginationResponse<UserResponse> getUsers(int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id").ascending());
        Page<UserResponse> userResponsePage = userRepository.findAll(pageable).map(userMapper::toUserResponse);
        return PaginationResponse.<UserResponse>builder()
                .pagination(PaginationMapper.toPaginationMeta(userResponsePage))
                .data(userResponsePage.getContent())
                .build();
    }

    @PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
    @Cacheable(value = "users", key = "#userId")
    public UserResponse getUserById(Long userId) {
        return userMapper.toUserResponse(
                userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#userId"),
        @CacheEvict(value = "userPages", allEntries=true)
    })
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "userPages", allEntries=true)
    })
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserResponse getMyProfile() {
        SecurityContext context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
}
