package com.example.courseregistration.service.client;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.StudentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MicroserviceClientTest {

    @InjectMocks
    private MicroserviceClient client;

    @Mock
    private RestTemplate restTemplate;

    private static final String CLASS_API_GET =
        "https://app.cmrsapp.site/course-management/api/classSchedule/classId/{classId}";
    private static final String CLASS_API_PUT =
        "https://app.cmrsapp.site/course-management//api/classSchedule/editClassSchedule/{classId}";
    private static final String COURSE_DETAILS_API =
        "https://app.cmrsapp.site/course-management/api/courses/courseId/{courseId}";
    private static final String STUDENT_API =
        "https://app.cmrsapp.site/user-management/api/students/studentFullId/{studentFullId}";
    private static final String STUDENT_ID_API =
        "https://app.cmrsapp.site/user-management/api/students/{studentId}";

    private static final Long CLASS_ID = 10L;
    private static final Long COURSE_ID = 20L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "rest", restTemplate);
    }

    // --- fetchClass(...) tests ---

    @Test
    void fetchClass_returnsDto_whenComplete() {
        CourseClassDTO dto = new CourseClassDTO();
        dto.setClassId(CLASS_ID);
        dto.setCourseId(COURSE_ID);
        dto.setCourseCode("ABC123");
        dto.setCourseName("Test Course");

        when(restTemplate.getForObject(CLASS_API_GET, CourseClassDTO.class, CLASS_ID))
            .thenReturn(dto);

        CourseClassDTO result = client.fetchClass(CLASS_ID);

        assertThat(result).isSameAs(dto);
        verify(restTemplate).getForObject(CLASS_API_GET, CourseClassDTO.class, CLASS_ID);
    }

    @Test
    void fetchClass_throwsNotFound_whenGetReturnsNull() {
        when(restTemplate.getForObject(CLASS_API_GET, CourseClassDTO.class, CLASS_ID))
            .thenReturn(null);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> client.fetchClass(CLASS_ID)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getReason()).contains("Class not found");
    }

    @Test
    void fetchClass_populatesCourseDetails_whenMissing() {
        CourseClassDTO dto = new CourseClassDTO();
        dto.setClassId(CLASS_ID);
        dto.setCourseId(COURSE_ID);

        when(restTemplate.getForObject(CLASS_API_GET, CourseClassDTO.class, CLASS_ID))
            .thenReturn(dto);

        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("courseCode", "XYZ789");
        courseMap.put("courseName", "Another Course");
        when(restTemplate.getForObject(COURSE_DETAILS_API, Map.class, COURSE_ID))
            .thenReturn(courseMap);

        CourseClassDTO result = client.fetchClass(CLASS_ID);

        assertThat(result.getCourseCode()).isEqualTo("XYZ789");
        assertThat(result.getCourseName()).isEqualTo("Another Course");
    }

    @Test
    void fetchClass_fallbackOnException_inCourseFetch() {
        // use spy to force fetchCourseDetails to throw
        MicroserviceClient spyClient = Mockito.spy(client);
        ReflectionTestUtils.setField(spyClient, "rest", restTemplate);

        CourseClassDTO dto = new CourseClassDTO();
        dto.setClassId(CLASS_ID);
        dto.setCourseId(COURSE_ID);

        when(restTemplate.getForObject(CLASS_API_GET, CourseClassDTO.class, CLASS_ID))
            .thenReturn(dto);

        // simulate exception in inner fetchCourseDetails call
        doThrow(new RuntimeException("Service down"))
            .when(spyClient).fetchCourseDetails(COURSE_ID);

        CourseClassDTO result = spyClient.fetchClass(CLASS_ID);

        assertThat(result.getCourseCode()).isEqualTo("COURSE-" + COURSE_ID);
        assertThat(result.getCourseName()).isEqualTo("Course " + COURSE_ID);
    }

    @Test
    void fetchClass_throwsNotFound_whenHttpNotFound() {
        when(restTemplate.getForObject(CLASS_API_GET, CourseClassDTO.class, CLASS_ID))
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
            ));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> client.fetchClass(CLASS_ID)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- updateVacancy(...) tests ---

    @Test
    void updateVacancy_sendsPutRequest() {
        CourseClassDTO dto = new CourseClassDTO();
        dto.setClassId(CLASS_ID);
        dto.setCourseId(COURSE_ID);
        dto.setDayOfWeek("MONDAY");
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(10, 0));
        dto.setMaxCapacity(30);

        int newVacancy = 5;
        client.updateVacancy(dto, newVacancy);

        Map<String, Object> expected = new HashMap<>();
        expected.put("courseId", COURSE_ID);
        expected.put("dayOfWeek", "MONDAY");
        expected.put("startTime", "09:00");
        expected.put("endTime", "10:00");
        expected.put("maxCapacity", 30);
        expected.put("vacancy", newVacancy);

        verify(restTemplate)
            .put(CLASS_API_PUT, expected, CLASS_ID);
    }

    // --- validateStudentExists(...) tests ---

    @Test
    void validateStudentExists_passesWhenFound() {
        when(restTemplate.getForEntity(STUDENT_API, String.class, "full123"))
            .thenReturn(ResponseEntity.ok("exists"));

        client.validateStudentExists("full123");

        verify(restTemplate)
            .getForEntity(STUDENT_API, String.class, "full123");
    }

    @Test
    void validateStudentExists_throwsWhenNotFound() {
        when(restTemplate.getForEntity(STUDENT_API, String.class, "full123"))
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
            ));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> client.validateStudentExists("full123")
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getReason())
            .contains("Student with Full Id full123 does not exist");
    }

    // --- fetchStudentByFullId(...) tests ---

    @Test
    void fetchStudentByFullId_returnsDto() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentFullId("full123");

        when(restTemplate.getForObject(STUDENT_API, StudentDTO.class, "full123"))
            .thenReturn(dto);

        StudentDTO result = client.fetchStudentByFullId("full123");

        assertThat(result).isSameAs(dto);
    }

    @Test
    void fetchStudentByFullId_throwsWhenNull() {
        when(restTemplate.getForObject(STUDENT_API, StudentDTO.class, "full123"))
            .thenReturn(null);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> client.fetchStudentByFullId("full123")
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void fetchStudentByFullId_throwsWhenHttpNotFound() {
        when(restTemplate.getForObject(STUDENT_API, StudentDTO.class, "full123"))
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
            ));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> client.fetchStudentByFullId("full123")
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- fetchStudentById(...) tests ---

    @Test
    void fetchStudentById_returnsDto() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(99L);

        when(restTemplate.getForObject(STUDENT_ID_API, StudentDTO.class, 99L))
            .thenReturn(dto);

        StudentDTO result = client.fetchStudentById(99L);

        assertThat(result).isSameAs(dto);
    }

    @Test
    void fetchStudentById_throwsWhenNull() {
        when(restTemplate.getForObject(STUDENT_ID_API, StudentDTO.class, 99L))
            .thenReturn(null);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> client.fetchStudentById(99L)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void fetchStudentById_throwsWhenHttpNotFound() {
        when(restTemplate.getForObject(STUDENT_ID_API, StudentDTO.class, 99L))
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
            ));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> client.fetchStudentById(99L)
        );
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- fetchCourseDetails(...) tests ---

    @Test
    void fetchCourseDetails_returnsMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("courseCode", "C1");

        when(restTemplate.getForObject(COURSE_DETAILS_API, Map.class, COURSE_ID))
            .thenReturn(map);

        Map<String, Object> result = client.fetchCourseDetails(COURSE_ID);

        assertThat(result).isSameAs(map);
    }

    @Test
    void fetchCourseDetails_returnsNullWhenNotFound() {
        when(restTemplate.getForObject(COURSE_DETAILS_API, Map.class, COURSE_ID))
            .thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
            ));

        assertThat(client.fetchCourseDetails(COURSE_ID)).isNull();
    }

    @Test
    void fetchCourseDetails_returnsNullOnError() {
        when(restTemplate.getForObject(COURSE_DETAILS_API, Map.class, COURSE_ID))
            .thenThrow(new RuntimeException("Oops"));

        assertThat(client.fetchCourseDetails(COURSE_ID)).isNull();
    }
}
