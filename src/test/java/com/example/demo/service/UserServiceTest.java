package com.example.demo.service;

import com.example.demo.constant.PredefinedRole;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;
    private Role role;

    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(2002, 5, 7);

        request = UserCreationRequest.builder()
                .username("john")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id(3L)
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();

        user = User.builder()
                .id(3L)
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();

        role = Role.builder()
                .name(PredefinedRole.USER_ROLE)
                .description("User role")
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        // Arrange
        // Mock - Stub
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(userMapper.toUser(any())).thenReturn(user);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        Mockito.when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        Mockito.when(userRepository.save(any())).thenReturn(user);
        Mockito.when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        // Act
        UserResponse response = userService.createUser(request);

        // Assert
        Assertions.assertEquals(3L, response.getId());
        Assertions.assertEquals("john", response.getUsername());
        Assertions.assertEquals("John", response.getFirstName());
        Assertions.assertEquals("Doe", response.getLastName());
        Assertions.assertEquals(LocalDate.of(2002, 5, 7), response.getDob());
    }

    @Test
    void createUser_userExisted_fail() {
        // Arrange
        // Mock - Stub
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act
        var exception = Assertions.assertThrows(AppException.class, () -> userService.createUser(request));

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorCode().getStatusCode());
        Assertions.assertEquals(1002, exception.getErrorCode().getCode());
        Assertions.assertEquals("User existed", exception.getErrorCode().getMessage());
    }

    @Test
    @WithMockUser(username = "john")
    void getMyProfile_valid_success() {
        // Arrange
        // Mock - Stub
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        // Act
        UserResponse response = userService.getMyProfile();

        // Assert
        Assertions.assertEquals(3L, response.getId());
        Assertions.assertEquals("john", response.getUsername());
        Assertions.assertEquals("John", response.getFirstName());
        Assertions.assertEquals("Doe", response.getLastName());
        Assertions.assertEquals(LocalDate.of(2002, 5, 7), response.getDob());
    }
}
