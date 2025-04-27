package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ApiResponse;
import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StaffDto;
import com.example.usermanagement.dto.StaffResponseDto;
import com.example.usermanagement.dto.StaffUpdateRequestDto;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.User;
import com.example.usermanagement.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaffService staffService;

    @Autowired
    private ObjectMapper objectMapper;

    private Staff staff;
    private StaffResponseDto staffResponseDto;
    private StaffDto staffDto;
    private StaffUpdateRequestDto updateRequestDto;
    private PasswordUpdateDto passwordUpdateDto;
    private List<StaffResponseDto> staffResponseDtoList;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("staff001");
        user.setEmail("staff001@example.com");

        staff = new Staff();
        staff.setStaffId(1);
        staff.setUser(user);
        staff.setName("John Doe");
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setStaffFullId("S001");
        staff.setDepartment("IT");
        staff.setPosition("Developer");

        staffResponseDto = StaffResponseDto.builder()
                .staffId(1)
                .userId(1)
                .username("staff001")
                .email("staff001@example.com")
                .fullName("John Doe")
                .firstName("John")
                .lastName("Doe")
                .staffFullId("S001")
                .department("IT")
                .position("Developer")
                .roles(Arrays.asList("ROLE_staff"))
                .build();

        staffResponseDtoList = Arrays.asList(staffResponseDto);

        staffDto = new StaffDto();
        staffDto.setFirstName("John");
        staffDto.setLastName("Doe");
        staffDto.setStaffFullId("S001");
        staffDto.setDepartment("IT");
        staffDto.setPosition("Developer");

        updateRequestDto = new StaffUpdateRequestDto();
        updateRequestDto.setFirstName("Updated");
        updateRequestDto.setLastName("Name");
        updateRequestDto.setEmail("updated.name@example.com");
        updateRequestDto.setDepartment("HR");
        updateRequestDto.setPosition("Manager");
        updateRequestDto.setRoles(Arrays.asList("ROLE_staff", "ROLE_admin"));

        passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setCurrentPassword("oldPassword");
        passwordUpdateDto.setNewPassword("newPassword");
        passwordUpdateDto.setConfirmPassword("newPassword");
    }

    @Test
    void testGetAllStaff() throws Exception {
        when(staffService.getAllStaffResponses()).thenReturn(staffResponseDtoList);

        mockMvc.perform(get("/api/staff/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].staffId", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].fullName", is("John Doe")))
                .andExpect(jsonPath("$[0].department", is("IT")))
                .andExpect(jsonPath("$[0].position", is("Developer")));

        verify(staffService, times(1)).getAllStaffResponses();
    }

    @Test
    void testGetStaffById() throws Exception {
        when(staffService.getStaffResponseByUserId(anyInt())).thenReturn(staffResponseDto);

        mockMvc.perform(get("/api/staff/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.fullName", is("John Doe")))
                .andExpect(jsonPath("$.department", is("IT")))
                .andExpect(jsonPath("$.position", is("Developer")));

        verify(staffService, times(1)).getStaffResponseByUserId(1);
    }

    @Test
    void testUpdateStaff() throws Exception {
        when(staffService.updateStaff(anyLong(), any())).thenReturn(staff);

        mockMvc.perform(put("/api/staff/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(staffDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.department", is("IT")))
                .andExpect(jsonPath("$.position", is("Developer")));

        verify(staffService, times(1)).updateStaff(eq(1L), any());
    }

    @Test
    void testUpdateStaffWithRoles() throws Exception {
        StaffResponseDto updatedStaffResponse = StaffResponseDto.builder()
                .staffId(1)
                .userId(1)
                .username("staff001")
                .email("updated.name@example.com")
                .fullName("Updated Name")
                .firstName("Updated")
                .lastName("Name")
                .staffFullId("S001")
                .department("HR")
                .position("Manager")
                .roles(Arrays.asList("ROLE_staff", "ROLE_admin"))
                .build();

        when(staffService.updateStaff(anyInt(), any())).thenReturn(updatedStaffResponse);

        mockMvc.perform(put("/api/staff/update/{userId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.fullName", is("Updated Name")))
                .andExpect(jsonPath("$.department", is("HR")))
                .andExpect(jsonPath("$.position", is("Manager")))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("ROLE_staff", "ROLE_admin")));

        verify(staffService, times(1)).updateStaff(eq(1), any());
    }

    @Test
    void testUpdatePasswordSuccess() throws Exception {
        when(staffService.updatePassword(anyInt(), any())).thenReturn(true);

        mockMvc.perform(post("/api/staff/{userId}/password", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Password updated successfully")));

        verify(staffService, times(1)).updatePassword(eq(1), any());
    }

    @Test
    void testUpdatePasswordFailure() throws Exception {
        when(staffService.updatePassword(anyInt(), any())).thenReturn(false);

        mockMvc.perform(post("/api/staff/{userId}/password", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Failed to update password")));

        verify(staffService, times(1)).updatePassword(eq(1), any());
    }

    @Test
    void testDeleteStaff() throws Exception {
        doNothing().when(staffService).deleteStaff(anyInt());

        mockMvc.perform(delete("/api/staff/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Staff deleted successfully")));

        verify(staffService, times(1)).deleteStaff(1);
    }
}