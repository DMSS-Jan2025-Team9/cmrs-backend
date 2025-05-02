package com.example.courseregistration.controller;

import com.example.courseregistration.dto.*;
import com.example.courseregistration.service.CourseRegistrationService;
import com.example.courseregistration.service.NotificationPublisherService;
import com.example.courseregistration.service.WaitlistNotificationService;
import com.example.courseregistration.service.client.MicroserviceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@WebMvcTest(CourseRegistrationController.class)
public class CourseRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseRegistrationService courseRegistrationService;

    @MockitoBean
    private NotificationPublisherService notificationPublisherService;

    @MockitoBean
    private MicroserviceClient microserviceClient;

    @MockitoBean
    private WaitlistNotificationService waitlistNotificationService;

    @Test
    public void testGetAllRegistrations() throws Exception {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setRegistrationId(1L);
        List<RegistrationDTO> list = Collections.singletonList(dto);
        when(courseRegistrationService.filterRegistration(
                eq(1L), eq(2L), eq(3L), eq("status"), eq(4L), eq(true)
        )).thenReturn(list);

        mockMvc.perform(get("/api/courseRegistration")
                .param("registrationId", "1")
                .param("studentId", "2")
                .param("classId", "3")
                .param("registrationStatus", "status")
                .param("groupRegistrationId", "4")
                .param("groupRegistration", "true")
        )
        .andExpect(status().isOk())
        .andExpect(content().json("[{ 'registrationId': 1 }]"));
    }

    @Test
    public void testCreateRegistration() throws Exception {
        CreateRegistrationDTO createDTO = new CreateRegistrationDTO();
        RegistrationDTO dto = new RegistrationDTO();
        dto.setRegistrationId(1L);
        List<RegistrationDTO> created = Collections.singletonList(dto);
        when(courseRegistrationService.createRegistration(Mockito.any(CreateRegistrationDTO.class))).thenReturn(created);

        String json = objectMapper.writeValueAsString(createDTO);

        mockMvc.perform(post("/api/courseRegistration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
        .andExpect(status().isCreated())
        .andExpect(content().json("[{ 'registrationId': 1 }]"));
    }

    @Test
    public void testUpdateRegistrationStatus() throws Exception {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        RegistrationDTO responseDto = new RegistrationDTO();
        responseDto.setRegistrationId(1L);
        List<RegistrationDTO> updated = Collections.singletonList(responseDto);
        when(courseRegistrationService.updateRegistrationStatus(Mockito.any(UpdateRegistrationStatusDTO.class))).thenReturn(updated);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/courseRegistration/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(content().json("[{ 'registrationId': 1 }]"));
    }

    @Test
    public void testUnenrollRegistration() throws Exception {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setRegistrationId(1L);
        when(courseRegistrationService.unenrollRegistration(1L)).thenReturn(dto);

        mockMvc.perform(put("/api/courseRegistration/unenroll/{registrationId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("{ 'registrationId': 1 }"));
    }

    @Test
    public void testWaitlistNotification() throws Exception {
        StudentDTO student = new StudentDTO();
        student.setStudentId(100L);
        CourseClassDTO courseClass = new CourseClassDTO();
        when(microserviceClient.fetchStudentByFullId("fullId")).thenReturn(student);
        when(microserviceClient.fetchClass(10L)).thenReturn(courseClass);

        mockMvc.perform(post("/api/courseRegistration/waitlist-notification")
                .param("studentFullId", "fullId")
                .param("classId", "10")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.message").value("Waitlist notification sent for student fullId and class 10"));

        verify(notificationPublisherService).publishWaitlistNotification("fullId", 100L, courseClass);
    }

    @Test
    public void testVacancyNotification() throws Exception {
        StudentDTO student = new StudentDTO();
        student.setStudentId(200L);
        CourseClassDTO courseClass = new CourseClassDTO();
        when(microserviceClient.fetchStudentByFullId("fullId")).thenReturn(student);
        when(microserviceClient.fetchClass(20L)).thenReturn(courseClass);

        mockMvc.perform(post("/api/courseRegistration/vacancy-notification")
                .param("studentFullId", "fullId")
                .param("classId", "20")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.message").value("Vacancy notification sent for student fullId and class 20"));

        verify(notificationPublisherService).publishVacancyAvailableNotification("fullId", 200L, courseClass);
    }

    @Test
    public void testUpdateVacancy_Increase() throws Exception {
        CourseClassDTO original = new CourseClassDTO();
        original.setVacancy(5);
        CourseClassDTO updated = new CourseClassDTO();
        updated.setVacancy(10);

        when(microserviceClient.fetchClass(30L)).thenReturn(original).thenReturn(updated);
        doNothing().when(microserviceClient).updateVacancy(original, 10);

        mockMvc.perform(put("/api/courseRegistration/update-vacancy")
                .param("classId", "30")
                .param("vacancy", "10")
        )
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(updated)));

        verify(waitlistNotificationService).notifyWaitlistedStudents(30L, updated);
    }

    @Test
    public void testNotifyAllWaitlistedStudents() throws Exception {
        CourseClassDTO courseClass = new CourseClassDTO();
        when(microserviceClient.fetchClass(40L)).thenReturn(courseClass);

        mockMvc.perform(post("/api/courseRegistration/notify-waitlisted")
                .param("classId", "40")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.message").value("Notification sent to all waitlisted students for class 40"));

        verify(waitlistNotificationService).notifyWaitlistedStudents(40L, courseClass);
    }
}
