package com.bookditi.identity.mapper;

import org.mapstruct.Mapper;

import com.bookditi.identity.dto.request.PermissionRequest;
import com.bookditi.identity.dto.response.PermissionResponse;
import com.bookditi.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
