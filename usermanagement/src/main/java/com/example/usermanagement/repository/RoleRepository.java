package com.example.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usermanagement.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional <Role> findByRoleName(String roleName);
    Optional <Role> findByDescription(String description);
    Optional <Role> findByRoleId(Integer userId);

    Optional <Role> deleteByRoleId(Integer userId);

    boolean existsByRoleName(String roleName);
    boolean existsByDescription(String Description);
}

