package com.bookditi.identity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookditi.identity.dto.filter.UserSearchFilter;
import com.bookditi.identity.entity.User;

public interface UserRepositoryCustom {
    Page<User> searchUsers(UserSearchFilter userSearchFilter, Pageable pageable);
}
