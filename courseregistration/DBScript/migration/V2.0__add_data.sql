-- V2__add_data.sql
USE registration_db;

-- Sample data for individual registrations
INSERT INTO registration (student_id, class_id, registration_status, group_registration_id)
VALUES 
(6, 1, 'Registered', NULL), 
(7, 2, 'Registered', NULL),  
(9, 3, 'Waitlisted', NULL);  

-- Sample data for group registrations
INSERT INTO registration (student_id, class_id, registration_status, group_registration_id)
VALUES 
(4, 4, 'Registered', 1),  
(5, 4, 'Registered', 1), 
(6, 4, 'Registered', 1);  