DROP TABLE IF EXISTS `app_user_role`;
DROP TABLE IF EXISTS `booking_seat`;
DROP TABLE IF EXISTS `role_permission`;

DROP TABLE IF EXISTS `app_user`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `invalidatedtoken`;
DROP TABLE IF EXISTS `permission`;

DROP TABLE IF EXISTS `booking`;
DROP TABLE IF EXISTS `seat`;
DROP TABLE IF EXISTS `room`;


CREATE TABLE `app_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dob` date DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3k4cplvh82srueuttfkwnylq0` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bookingTime` datetime(6) DEFAULT NULL,
  `status` enum('CANCELLED','EXPIRED','SUCCESS','WAITING_PAYMENT') DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa1kqgedve8hmkeqfbjje79jku` (`user_id`),
  CONSTRAINT `FKa1kqgedve8hmkeqfbjje79jku` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `invalidatedtoken` (
  `id` varchar(255) NOT NULL,
  `expiryTime` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `permission` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `role_permission` (
  `Role_name` varchar(255) NOT NULL,
  `permissions_name` varchar(255) NOT NULL,
  PRIMARY KEY (`Role_name`,`permissions_name`),
  KEY `FKpe68e0lxdlph1no11e24o85jt` (`permissions_name`),
  CONSTRAINT `FK5neja2e8cwqtyvtkih4aa3rds` FOREIGN KEY (`Role_name`) REFERENCES `role` (`name`),
  CONSTRAINT `FKpe68e0lxdlph1no11e24o85jt` FOREIGN KEY (`permissions_name`) REFERENCES `permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `totalCols` bigint NOT NULL,
  `totalRows` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4u0jmlnmhlwfvlwkaijwmiy1k` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `seat` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `status` enum('AVAILABLE','BOOKED','HOLD') DEFAULT NULL,
  `version` int DEFAULT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKs4ycyjn1vejec9dc86hy7od43` (`room_id`,`code`),
  CONSTRAINT `FKku9hcffqk00g8kloiopxgxd0q` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `app_user_role` (
  `User_id` bigint NOT NULL,
  `roles_name` varchar(255) NOT NULL,
  PRIMARY KEY (`User_id`,`roles_name`),
  KEY `FKpwa3udywimy4jb5mpwexi6hly` (`roles_name`),
  CONSTRAINT `FKg3q97bc7w05esp2359pdy82oe` FOREIGN KEY (`User_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKpwa3udywimy4jb5mpwexi6hly` FOREIGN KEY (`roles_name`) REFERENCES `role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `booking_seat` (
  `Booking_id` bigint NOT NULL,
  `seats_id` bigint NOT NULL,
  KEY `FK99u90u6au9lemfh7nc11b0m8n` (`seats_id`),
  KEY `FKeugb96fyrs42dttdnt1shqera` (`Booking_id`),
  CONSTRAINT `FK99u90u6au9lemfh7nc11b0m8n` FOREIGN KEY (`seats_id`) REFERENCES `seat` (`id`),
  CONSTRAINT `FKeugb96fyrs42dttdnt1shqera` FOREIGN KEY (`Booking_id`) REFERENCES `booking` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;