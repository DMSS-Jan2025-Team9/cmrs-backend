package com.example.usermanagement.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void assignStudentRole(Integer userId) {
        entityManager.createNativeQuery("INSERT INTO user_role (user_id, role_id) VALUES (:userId, 2)")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
