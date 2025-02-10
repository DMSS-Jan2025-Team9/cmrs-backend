CREATE DATABASE notification_db;
USE notification_db;

CREATE TABLE notification (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,  
    notification_message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP
);

-- 2. Sample data
INSERT INTO notification (user_id, notification_message) 
VALUES (1, 'You are enrolled in the course ABC');
