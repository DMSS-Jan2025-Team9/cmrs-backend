package com.example.usermanagement.repository;

import com.example.usermanagement.model.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional <Role> findByRoleName(String roleName);
    Optional <Role> findByDescription(String description);

    boolean existsByRoleName(String roleName);
    boolean existsByDescription(String Description);
}

