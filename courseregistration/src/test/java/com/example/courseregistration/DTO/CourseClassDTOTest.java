package com.example.courseregistration.DTO;
import com.example.courseregistration.dto.CourseClassDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class CourseClassDTOTest {

    @Test
    void defaultConstructor_defaultsAreNullOrZero() {
        CourseClassDTO dto = new CourseClassDTO();

        assertNull(dto.getClassId(), "classId should be null by default");
        assertNull(dto.getCourseId(), "courseId should be null by default");
        assertNull(dto.getCourseCode(), "courseCode should be null by default");
        assertNull(dto.getCourseName(), "courseName should be null by default");
        assertNull(dto.getDayOfWeek(), "dayOfWeek should be null by default");
        assertNull(dto.getStartTime(), "startTime should be null by default");
        assertNull(dto.getEndTime(), "endTime should be null by default");
        assertEquals(0, dto.getMaxCapacity(), "maxCapacity should be 0 by default");
        assertEquals(0, dto.getVacancy(), "vacancy should be 0 by default");
    }

    @Test
    void settersAndGetters_shouldWorkProperly() {
        CourseClassDTO dto = new CourseClassDTO();

        Long classId = 10L;
        Long courseId = 20L;
        String code = "CS101";
        String name = "Intro to CS";
        String day = "Monday";
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(11, 0);
        int capacity = 30;
        int vacancy = 25;

        dto.setClassId(classId);
        dto.setCourseId(courseId);
        dto.setCourseCode(code);
        dto.setCourseName(name);
        dto.setDayOfWeek(day);
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setMaxCapacity(capacity);
        dto.setVacancy(vacancy);

        assertEquals(classId, dto.getClassId());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(code, dto.getCourseCode());
        assertEquals(name, dto.getCourseName());
        assertEquals(day, dto.getDayOfWeek());
        assertEquals(start, dto.getStartTime());
        assertEquals(end, dto.getEndTime());
        assertEquals(capacity, dto.getMaxCapacity());
        assertEquals(vacancy, dto.getVacancy());
    }

    @Test
    void toString_includesAllFields() {
        CourseClassDTO dto = new CourseClassDTO();
        dto.setClassId(1L);
        dto.setCourseId(2L);
        dto.setCourseCode("ABC123");
        dto.setCourseName("Test Course");
        dto.setDayOfWeek("Friday");
        dto.setStartTime(LocalTime.of(8, 30));
        dto.setEndTime(LocalTime.of(10, 30));
        dto.setMaxCapacity(50);
        dto.setVacancy(45);

        String str = dto.toString();

        assertTrue(str.contains("classId=1"));
        assertTrue(str.contains("courseId=2"));
        assertTrue(str.contains("courseCode='ABC123'"));
        assertTrue(str.contains("courseName='Test Course'"));
        assertTrue(str.contains("dayOfWeek='Friday'"));
        assertTrue(str.contains("startTime=08:30"));
        assertTrue(str.contains("endTime=10:30"));
        assertTrue(str.contains("maxCapacity=50"));
        assertTrue(str.contains("vacancy=45"));
    }
}
