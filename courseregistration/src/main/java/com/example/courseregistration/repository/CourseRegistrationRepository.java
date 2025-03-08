package com.example.courseregistration.repository;

import com.example.courseregistration.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRepository extends JpaRepository<Registration, Long> {

    @Query("SELECT r FROM Registration r WHERE " +
            "(:registrationId IS NULL OR r.registrationId = :registrationId) AND " +
            "(:studentId IS NULL OR r.studentId = :studentId) AND " +
            "(:classId IS NULL OR r.classId = :classId) AND " +
            "(:registrationStatus IS NULL OR r.registrationStatus = :registrationStatus) AND " +
            "(:groupRegistrationId IS NULL OR r.groupRegistrationId = :groupRegistrationId)")
    List<Registration> filterRegistration(
            @Param("registrationId") Long registrationId,
            @Param("studentId") Long studentId,
            @Param("classId") Long classId,
            @Param("registrationStatus") String registrationStatus,
            @Param("groupRegistrationId") Long groupRegistrationId
    );
}
