package com.example.usermanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.usermanagement.model.Permission;


public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(String permissionName);
    Optional <Permission> deleteByPermissionId(Integer permissionId);
    Optional <Permission> findByPermissionId(Integer permissionId);
    List<Permission> findByPermissionNameIn(List<String> names);

    boolean existsByPermissionName(String permissionName);
    boolean existsByPermissionId(Integer permissionId);
}
