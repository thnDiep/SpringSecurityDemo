package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.filter.UserSearchFilter;
import com.example.demo.entity.User;

public interface UserRepositoryCustom {
    Page<User> searchUsers(UserSearchFilter userSearchFilter, Pageable pageable);
}
