package com.example.demo.repository;

import com.example.demo.dto.UserSearchFilter;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
    Page<User> searchUsers(UserSearchFilter userSearchFilter, Pageable pageable);
}
