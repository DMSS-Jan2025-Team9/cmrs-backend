CREATE DATABASE registration_db;
USE registration_db;

CREATE TABLE registration (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    class_id INT NOT NULL,
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    registration_status VARCHAR(255) NOT NULL,
    group_registration_id INT DEFAULT NULL 
);


/* Sample data */
-- Insert data for individual registration
INSERT INTO registration (student_id, class_id, registration_status, group_registration_id)
VALUES 
(6, 1, 'Registered', NULL), 
(7, 2, 'Registered', NULL),  
(9, 3, 'Waitlisted', NULL);  

-- Insert data for group registration
INSERT INTO registration (student_id, class_id, registration_status, group_registration_id)
VALUES 
(4, 4, 'Registered', 1),  
(5, 4, 'Registered', 1), 
(6, 4, 'Registered', 1);  
