USE user_management_db;

UPDATE `user` 
    set username = 'U202020'
WHERE user_id = 2 and username = 'S202020';

INSERT INTO `staff` (staff_id, user_id, name, staff_full_id, first_name, last_name, department, position)
VALUES (2,1,'Test Admin 01','S101010','Test','Admin 01','Information System Engineering','Lecturer');


INSERT INTO `student` (student_id,user_id,name,program_id,enrolled_at,first_name,job_id,last_name,program_name,student_full_id)
VALUES (3,2,'Test Student 01',2,'2025-04-26 11:04:39','Test',NULL,'Student','Master of Data Science','U202020')
,(4,4,'Test Student 02',3,'2025-04-26 11:04:39','Test',NULL,'Student','Bachelor of Cybersecurity','U042582');

