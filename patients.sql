CREATE SCHEMA IF NOT EXISTS `patients`;

USE `patients`;

CREATE TABLE IF NOT EXISTS `patients`.`patients`
(
    `id`              int          NOT NULL AUTO_INCREMENT,
    `patient`         varchar(256) NOT NULL,
    `dob`             date         DEFAULT NULL,
    `encounter_date`  date         DEFAULT NULL,
    `provider`        varchar(256) DEFAULT NULL,
    `encounter_note`  text,
    `chief_complaint` varchar(256),
    `provider_npi`    int          NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE USER IF NOT EXISTS 'application'@'localhost' IDENTIFIED BY 'Welcome1';

GRANT SELECT, INSERT, UPDATE, DELETE on `patients`.* TO 'application'@'localhost' WITH GRANT OPTION;