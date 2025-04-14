CREATE DATABASE recommendation_db;
USE recommendation_db;

CREATE TABLE program_recommendation_rules (
  rule_id INT AUTO_INCREMENT PRIMARY KEY,
  program_id INT NOT NULL,
  type VARCHAR(255),
  value VARCHAR(255),
  weight DOUBLE PRECISION
);


/* Sample data */
INSERT INTO program_recommendation_rules (program_id, type, value, weight) VALUES
(1, 'TITLE_CONTAINS', 'Programming', 1.0),
(1, 'LEVEL_EQUALS', 'Beginner', 0.5),
(2, 'CATEGORY_EQUALS', 'Data Science', 1.0),
(2, 'TITLE_CONTAINS', 'Data', 1.0),
(2, 'LEVEL_EQUALS', 'Beginner', 0.5),
(3, 'TITLE_CONTAINS', 'Cybersecurity', 1.0),
(3, 'TITLE_CONTAINS', 'Cloud', 0.8),
(3, 'CATEGORY_EQUALS', 'Cybersecurity', 1.0),
(3, 'LEVEL_EQUALS', 'Beginner', 0.5)

CREATE TABLE recommendation (
    recommendation_id INT AUTO_INCREMENT PRIMARY KEY,
    program_id INT NOT NULL,
    course_id INT NOT NULL
);

/* Sample data */
INSERT INTO recommendation (program_id, course_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(1, 2),
(2, 2);