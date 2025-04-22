package com.example.demo.config.batch;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class UserItemProcessor implements ItemProcessor<User, UserDto> {

    @Override
    public UserDto process(User user) throws Exception {
        Set<Role> roles = user.getRoles();
        String roleStr = roles.stream().map(Role::getName).collect(Collectors.joining(","));

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .roles(roleStr)
                .build();
    }
}
