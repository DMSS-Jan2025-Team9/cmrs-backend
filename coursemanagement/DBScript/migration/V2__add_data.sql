-- V2__add_data.sql

-- Insert sample data into course table
INSERT INTO course (course_name, course_code, registration_start, registration_end, max_capacity, status, course_desc)
VALUES
('Introduction to Computer Science', 'CS101', '2025-03-01 08:00:00', '2025-05-30 17:00:00', 100, 'active', 'A foundational course that covers the basics of computer science, programming, and problem-solving techniques.'),
('Advanced Database Systems', 'CS201', '2025-04-01 08:00:00', '2025-06-30 17:00:00', 50, 'active', 'This course explores complex database systems, including distributed databases, data warehousing, and SQL optimization.'),
('Web Development Fundamentals', 'CS301', '2025-03-15 08:00:00', '2025-05-31 17:00:00', 75, 'active', 'An introductory course on web development using HTML, CSS, and JavaScript for building dynamic websites.'),
('Machine Learning with Python', 'CS401', '2025-03-01 08:00:00', '2025-06-15 17:00:00', 60, 'active', 'A course on machine learning algorithms and implementation using Python, covering supervised and unsupervised learning techniques.'),
('Data Structures and Algorithms', 'CS102', '2025-03-01 08:00:00', '2025-05-31 17:00:00', 120, 'active', 'Learn fundamental data structures like lists, stacks, queues, trees, and algorithms for problem-solving and optimization.'),
('Cloud Computing Basics', 'CS501', '2025-04-15 08:00:00', '2025-07-15 17:00:00', 80, 'inactive', 'An introduction to cloud computing concepts, including virtualization, cloud providers, and services like AWS and Azure.'),
('Cybersecurity Fundamentals', 'CS601', '2025-03-10 08:00:00', '2025-06-30 17:00:00', 90, 'active', 'Covering the basics of cybersecurity, including network security, cryptography, and risk management.');


-- Insert sample data into program table
INSERT INTO program (program_name, program_desc) VALUES
('Bachelor of Computer Science', 'A program that provides foundational knowledge in computer science and software development.'),
('Master of Data Science', 'A program that focuses on data analytics, machine learning, and artificial intelligence.'),
('Bachelor of Cybersecurity', 'A program focused on network security, cryptography, and cybersecurity practices.');

-- Insert sample data into program_course table
INSERT INTO program_course (program_id, course_id) VALUES
(1, 1),
(1, 2),
(1, 5),
(2, 4),
(2, 6),
(3, 7);

-- Insert sample data into class table
INSERT INTO class (course_id, day_of_week, start_time, end_time, max_capacity, vacancy)
VALUES
(1, 'Monday', '08:00:00', '09:00:00', 50, 50),
(2, 'Wednesday', '14:00:00', '16:00:00', 25, 25),
(4, 'Friday', '13:00:00', '15:00:00', 30, 30),
(7, 'Tuesday', '10:00:00', '12:00:00', 45, 45);
