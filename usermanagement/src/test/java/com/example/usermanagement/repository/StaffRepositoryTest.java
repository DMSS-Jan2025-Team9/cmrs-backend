package com.example.usermanagement.repository;

import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StaffRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    private User testUser;
    private Staff testStaff;

    @BeforeEach
    public void setup() {
        // Create and save user directly through repository
        testUser = new User();
        testUser.setUsername("staffuser" + System.currentTimeMillis());
        testUser.setEmail("staff" + System.currentTimeMillis() + "@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        // Create and save staff directly through repository
        testStaff = new Staff();
        testStaff.setName("Test Staff");
        testStaff.setStaffFullId("S" + System.currentTimeMillis());
        testStaff.setDepartment("IT");
        testStaff.setPosition("Manager");
        testStaff.setUser(testUser);
        testStaff = staffRepository.save(testStaff);
    }

    @Test
    public void testFindByUserId() {
        // Act
        Optional<Staff> found = staffRepository.findByUser_UserId(testUser.getUserId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(testUser.getUserId(), found.get().getUser().getUserId());
        assertEquals(testStaff.getStaffFullId(), found.get().getStaffFullId());
        assertEquals("IT", found.get().getDepartment());
        assertEquals("Manager", found.get().getPosition());
    }

    @Test
    public void testFindByUserIdNotFound() {
        // Act
        Optional<Staff> found = staffRepository.findByUser_UserId(999999);

        // Assert
        assertFalse(found.isPresent());
    }
}