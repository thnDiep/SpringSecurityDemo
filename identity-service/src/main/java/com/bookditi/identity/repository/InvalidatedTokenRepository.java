package com.bookditi.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookditi.identity.entity.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
