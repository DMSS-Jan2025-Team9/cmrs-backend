-- V2__add_data.sql
USE user_management_db;

-- Sample data
INSERT INTO `role` VALUES (1,'admin','Administrator with full access rights'),(2,'student','Regular user who can view and register for courses');
INSERT INTO `permission` VALUES (1,'view_course','Permission to view courses'),(2,'create_course','Permission to create courses'),(3,'register_course','Permission to register for courses'),(4,'view_users','Permission to view user list'),(5,'manage_user_roles','Permission to assign roles to users'),(6,'view_roles','Permission to view role list'),(7,'manage_roles','Permission to create/update/delete roles'),(8,'view_permissions','Permission to view permissions list'),(9,'manage_permissions','Permission to create/update/delete permissions'),(10,'manage_role_permissions','Permission to assign permissions to roles');
INSERT INTO `role_permission` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10);
INSERT INTO `staff` VALUES (1,3,'Test Admin','S035464','Test','Admin','Computing','Associate Professor');
INSERT INTO `student` VALUES (2,5,'Test Student',1,'2025-04-26 11:04:39','Test',NULL,'Student','Bachelor of Computer Science','U058904');
INSERT INTO `user` VALUES (3,'S035464','$2a$10$.vh4pg2LP/P9ZPFTp8AS3uTaRe37gEfDGjB5WA3G6kRqyvzko5UZG','S035464@staff.university.edu','2025-04-26 10:50:09','2025-04-26 10:50:09'),(4,'U042582','$2a$10$TB0HDZBa2WL8oLN4GluN1u3VjwWeiH0EZx6idVOZigiu6rh/I.CDW','U042582@university.edu','2025-04-26 11:00:43','2025-04-26 11:00:43'),(5,'U058904','$2a$10$CVfdeQRicBav7ZRu8xt4XeiXCKVlYu8K3OmczPyoA8eMdiUdpkvJu','U058904@university.edu','2025-04-26 11:04:39','2025-04-26 11:04:39');
INSERT INTO `user_role` VALUES (1,1),(3,1),(1,2),(4,2),(5,2);
