package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
