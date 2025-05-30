package com.bookditi.identity.dto.response;

import java.io.Serializable;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    String description;
    Set<PermissionResponse> permissions;
}
