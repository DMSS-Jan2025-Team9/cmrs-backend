package com.example.usermanagement.repository;

//import com.example.usermanagement.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.usermanagement.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional <User> findByUsername(String username);
    Optional <User> findByEmail(String email);
    Optional <User> findByUserId(Integer userId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
