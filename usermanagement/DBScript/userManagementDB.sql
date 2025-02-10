CREATE DATABASE user_management_db;
USE user_management_db;

CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Roles table: stores available roles (admin, student, etc.)
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE, 
    description VARCHAR(255) 
);

-- 3. Permissions table: stores individual permissions
CREATE TABLE permission (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(255) NOT NULL UNIQUE, 
    description VARCHAR(255)  
);

-- 4. User-Role Relationship: Assigns one or more roles to users
CREATE TABLE user_role (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
);

-- 5. Role-Permission Relationship: Assigns permissions to roles
CREATE TABLE role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(permission_id) ON DELETE CASCADE
);

-- 6. Admin table: stores admin-specific information (linked to users table)
CREATE TABLE admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,  -- Foreign key to users table
    name VARCHAR(255) NOT NULL,  -- Renamed full_name to name
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE
);

-- 7. Student table: stores student-specific information (linked to users table)
CREATE TABLE student (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,  -- Foreign key to users table
    name VARCHAR(255) NOT NULL, 
    program_id INT NOT NULL,  
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE
);

-- 8. Sample Data

-- Adding roles (admin, student)
INSERT INTO role (role_name, description) 
VALUES ('admin', 'Administrator with full access rights'), 
       ('student', 'Regular user who can view and register for courses');

-- Adding permissions (view_course, create_course, etc.)
INSERT INTO permission (permission_name, description) 
VALUES ('view_course', 'Permission to view courses'),
       ('create_course', 'Permission to create courses'),
       ('register_course', 'Permission to register for courses');

-- Assigning permissions to the admin role
INSERT INTO role_permission (role_id, permission_id) 
VALUES ((SELECT role_id FROM role WHERE role_name = 'admin'), 
        (SELECT permission_id FROM permission WHERE permission_name = 'view_course')),
       ((SELECT role_id FROM role WHERE role_name = 'admin'), 
        (SELECT permission_id FROM permission WHERE permission_name = 'create_course')),
       ((SELECT role_id FROM role WHERE role_name = 'admin'), 
        (SELECT permission_id FROM permission WHERE permission_name = 'register_course'));

-- Assigning the student role to a user
INSERT INTO user (username, password, email) 
VALUES ('admin1', 'hashedPassword1', 'admin1@example.com');
INSERT INTO user_role (user_id, role_id) 
VALUES (LAST_INSERT_ID(), (SELECT role_id FROM role WHERE role_name = 'admin'));

-- Example insert for student
INSERT INTO user (username, password, email) 
VALUES ('student1', 'hashedPassword2', 'student1@example.com');
INSERT INTO student (user_id, name, program_id) 
VALUES (LAST_INSERT_ID(), 'John Doe', 1);
INSERT INTO user_role (user_id, role_id) 
VALUES (LAST_INSERT_ID(), (SELECT role_id FROM role WHERE role_name = 'student'));
