-- Database: default_schema

DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `app_user_role`;
DROP TABLE IF EXISTS `invalidatedtoken`;
DROP TABLE IF EXISTS `permission`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `app_user`;

CREATE TABLE `app_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `dob` DATE DEFAULT NULL,
  `firstName` VARCHAR(255) DEFAULT NULL,
  `lastName` VARCHAR(255) DEFAULT NULL,
  `password` VARCHAR(255) DEFAULT NULL,
  `username` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `invalidatedtoken` (
  `id` VARCHAR(255) NOT NULL,
  `expiryTime` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `permission` (
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role` (
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `app_user_role` (
  `User_id` BIGINT NOT NULL,
  `roles_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`User_id`, `roles_name`),
  KEY `FK_app_user_role_role` (`roles_name`),
  CONSTRAINT `FK_app_user_role_user` FOREIGN KEY (`User_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK_app_user_role_role` FOREIGN KEY (`roles_name`) REFERENCES `role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role_permission` (
  `Role_name` VARCHAR(255) NOT NULL,
  `permissions_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`Role_name`, `permissions_name`),
  KEY `FK_role_permission_permission` (`permissions_name`),
  CONSTRAINT `FK_role_permission_role` FOREIGN KEY (`Role_name`) REFERENCES `role` (`name`),
  CONSTRAINT `FK_role_permission_permission` FOREIGN KEY (`permissions_name`) REFERENCES `permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

