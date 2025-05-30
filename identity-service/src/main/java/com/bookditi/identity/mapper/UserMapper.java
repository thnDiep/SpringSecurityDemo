package com.bookditi.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bookditi.identity.dto.request.UserCreationRequest;
import com.bookditi.identity.dto.request.UserUpdateRequest;
import com.bookditi.identity.dto.response.UserInfo;
import com.bookditi.identity.dto.response.UserResponse;
import com.bookditi.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserInfo toUserInfo(User user);
}
