CREATE DATABASE course_db;
USE course_db;

CREATE TABLE course (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    course_code VARCHAR(50) UNIQUE NOT NULL,
    registration_start DATETIME NOT NULL,
    registration_end DATETIME NOT NULL,
    max_capacity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    course_desc VARCHAR(1000)  
);

/* Sample data */
INSERT INTO course (course_name, course_code, registration_start, registration_end, max_capacity, status, course_desc)
VALUES
('Introduction to Computer Science', 'CS101', '2025-03-01 08:00:00', '2025-05-30 17:00:00', 100, 'active', 'A foundational course that covers the basics of computer science, programming, and problem-solving techniques.'),
('Advanced Database Systems', 'CS201', '2025-04-01 08:00:00', '2025-06-30 17:00:00', 50, 'active', 'This course explores complex database systems, including distributed databases, data warehousing, and SQL optimization.'),
('Web Development Fundamentals', 'CS301', '2025-03-15 08:00:00', '2025-05-31 17:00:00', 75, 'active', 'An introductory course on web development using HTML, CSS, and JavaScript for building dynamic websites.'),
('Machine Learning with Python', 'CS401', '2025-03-01 08:00:00', '2025-06-15 17:00:00', 60, 'active', 'A course on machine learning algorithms and implementation using Python, covering supervised and unsupervised learning techniques.'),
('Data Structures and Algorithms', 'CS102', '2025-03-01 08:00:00', '2025-05-31 17:00:00', 120, 'active', 'Learn fundamental data structures like lists, stacks, queues, trees, and algorithms for problem-solving and optimization.'),
('Cloud Computing Basics', 'CS501', '2025-04-15 08:00:00', '2025-07-15 17:00:00', 80, 'inactive', 'An introduction to cloud computing concepts, including virtualization, cloud providers, and services like AWS and Azure.'),
('Cybersecurity Fundamentals', 'CS601', '2025-03-10 08:00:00', '2025-06-30 17:00:00', 90, 'active', 'Covering the basics of cybersecurity, including network security, cryptography, and risk management.');

-- Program Table
CREATE TABLE program (
    program_id INT AUTO_INCREMENT PRIMARY KEY,
    program_name VARCHAR(255) NOT NULL,
    program_desc VARCHAR(1000)
);

INSERT INTO program (program_name, program_desc) VALUES
('Bachelor of Computer Science', 'A program that provides foundational knowledge in computer science and software development.'),
('Master of Data Science', 'A program that focuses on data analytics, machine learning, and artificial intelligence.'),
('Bachelor of Cybersecurity', 'A program focused on network security, cryptography, and cybersecurity practices.');


-- Program-Course Relationship Table
CREATE TABLE program_course (
    program_id INT,
    course_id INT,
    PRIMARY KEY (program_id, course_id),
    FOREIGN KEY (program_id) REFERENCES program(program_id),
    FOREIGN KEY (course_id) REFERENCES course(course_id)
);

-- Sample Data for Program-Course Relationship
-- Associating courses with programs
INSERT INTO program_course (program_id, course_id) VALUES
(1, 1), -- Bachelor of Computer Science -> Introduction to Computer Science
(1, 2), -- Bachelor of Computer Science -> Advanced Database Systems
(1, 5), -- Bachelor of Computer Science -> Data Structures and Algorithms
(2, 4), -- Master of Data Science -> Machine Learning with Python
(2, 6), -- Master of Data Science -> Cloud Computing Basics
(3, 7); -- Bachelor of Cybersecurity -> Cybersecurity Fundamentals

