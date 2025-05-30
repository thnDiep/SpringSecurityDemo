package com.bookditi.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookditi.identity.dto.request.RoleRequest;
import com.bookditi.identity.dto.response.RoleResponse;
import com.bookditi.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
