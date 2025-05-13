package com.example.demo.repository;

import com.example.demo.dto.UserSearchFilter;
import com.example.demo.entity.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> searchUsers(UserSearchFilter userSearchFilter);
}
