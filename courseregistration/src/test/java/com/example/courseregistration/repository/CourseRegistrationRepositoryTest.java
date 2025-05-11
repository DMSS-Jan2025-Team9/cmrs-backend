package com.example.courseregistration.repository;

import com.example.courseregistration.model.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CourseRegistrationRepositoryTest {

    @Autowired
    private CourseRegistrationRepository repository;

    private Registration r1, r2, r3;

    @BeforeEach
    void setUp() {
        // clean slate between tests
        repository.deleteAll();

        // create three registrations with two different groupRegistrationIds
        r1 = new Registration();
        r1.setStudentId(1L);
        r1.setClassId(101L);
        r1.setRegistrationStatus("PENDING");
        r1.setGroupRegistrationId(1000L);

        r2 = new Registration();
        r2.setStudentId(2L);
        r2.setClassId(102L);
        r2.setRegistrationStatus("CONFIRMED");
        r2.setGroupRegistrationId(2000L);

        r3 = new Registration();
        r3.setStudentId(1L);
        r3.setClassId(103L);
        r3.setRegistrationStatus("CANCELLED");
        r3.setGroupRegistrationId(2000L);

        repository.save(r1);
        repository.save(r2);
        repository.save(r3);
    }

    @Test
    @DisplayName("findMaxGroupRegistrationId() should return the highest groupRegistrationId")
    void testFindMaxGroupRegistrationId() {
        Long maxGroupId = repository.findMaxGroupRegistrationId();
        assertThat(maxGroupId).isEqualTo(2000L);
    }

    @Test
    @DisplayName("findByGroupRegistrationId(...) should fetch only those with matching groupRegistrationId")
    void testFindByGroupRegistrationId() {
        List<Registration> grp1000 = repository.findByGroupRegistrationId(1000L);
        assertThat(grp1000)
            .hasSize(1)
            .containsExactly(r1);

        List<Registration> grp2000 = repository.findByGroupRegistrationId(2000L);
        assertThat(grp2000)
            .hasSize(2)
            .containsExactlyInAnyOrder(r2, r3);
    }

    @Test
    @DisplayName("filterRegistration(...) with all nulls returns everything")
    void testFilterRegistration_AllNulls() {
        List<Registration> all = repository.filterRegistration(
                null, null, null, null, null);
        assertThat(all)
            .hasSize(3)
            .containsExactlyInAnyOrder(r1, r2, r3);
    }

    @Test
    @DisplayName("filterRegistration(...) by studentId filters correctly")
    void testFilterRegistration_ByStudentId() {
        List<Registration> byStudent1 = repository.filterRegistration(
                null, 1L, null, null, null);
        assertThat(byStudent1)
            .allMatch(r -> r.getStudentId().equals(1L))
            .hasSize(2);
    }

    @Test
    @DisplayName("filterRegistration(...) by multiple fields returns exact match")
    void testFilterRegistration_ByMultipleCriteria() {
        List<Registration> result = repository.filterRegistration(
                null,             // registrationId
                1L,               // studentId
                101L,             // classId
                "PENDING",        // registrationStatus
                1000L             // groupRegistrationId
        );
        assertThat(result)
            .hasSize(1)
            .containsExactly(r1);
    }
}
