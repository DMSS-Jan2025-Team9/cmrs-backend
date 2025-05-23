package com.example.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usermanagement.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByRoleNameIn(List<String> roleNames);

    Optional<Role> findByRoleName(String roleName);

    Optional<Role> findByDescription(String description);

    Optional<Role> findByRoleId(Integer userId);

    void deleteByRoleId(Integer userId);

    boolean existsByRoleName(String roleName);

    boolean existsByDescription(String Description);
}
