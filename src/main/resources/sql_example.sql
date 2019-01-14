CREATE DATABASE IF NOT EXISTS test1;
USE test1;
CREATE TABLE IF NOT EXISTS user (
	id INT NOT NULL AUTO_INCREMENT,
	login VARCHAR(45) NOT NULL UNIQUE,
	password VARCHAR(45) NOT NULL,
	surname VARCHAR(100) NOT NULL,
	name VARCHAR(100) NOT NULL,
	isactive int NOT NULL DEFAULT 0,
	registration_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id)
);
CREATE INDEX surnameIndex ON user (surname);
CREATE TABLE IF NOT EXISTS applicant_data (
	idapplicant INT NOT NULL,
	date_of_brithday DATE,
	email VARCHAR(100),
	telephone_number VARCHAR(30),
	other TEXT,
	PRIMARY KEY (idapplicant),
	CONSTRAINT fk_idapplicant
	FOREIGN KEY (idapplicant) REFERENCES user (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE TABLE IF NOT EXISTS company (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL UNIQUE,
	adress VARCHAR(200),
	id_user INT NOT NULL,
	PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS employee_data (
	idemployee INT NOT NULL,
	idcompany INT NOT NULL,
	email VARCHAR(100),
	telephone_number VARCHAR(30),
	other TEXT,
	PRIMARY KEY (idemployee),
	CONSTRAINT fk_idemployee
	FOREIGN KEY (idemployee) REFERENCES user (id)
	ON DELETE RESTRICT
	ON UPDATE RESTRICT,
	CONSTRAINT fk_idcompany
	FOREIGN KEY (idcompany) REFERENCES company (id)
	ON DELETE RESTRICT
	ON UPDATE RESTRICT
);
CREATE TABLE IF NOT EXISTS vacancy (
	id INT NOT NULL AUTO_INCREMENT,
	idemployee INT NOT NULL,
	position VARCHAR(200) NOT NULL,
	salary DECIMAL(10, 3),
	salary_currency CHAR(3),
	requirements TEXT,
	time_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_idemploye_vacancy
	FOREIGN KEY (idemployee) REFERENCES user (id)
	ON DELETE RESTRICT
	ON UPDATE RESTRICT
);
CREATE INDEX positionIndex ON vacancy (position);
CREATE TABLE IF NOT EXISTS interview (
	id INT NOT NULL AUTO_INCREMENT,
	idapplicant INT NOT NULL,
	idvacancy INT NOT NULL,
	time_of_interview datetime,
	time_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	isactive BOOL  NOT NULL DEFAULT 1,
	PRIMARY KEY (id),
	CONSTRAINT fk_idapplicant_interviw
	FOREIGN KEY (idapplicant) REFERENCES applicant_data  (idapplicant)
	ON DELETE RESTRICT
	ON UPDATE RESTRICT,
	CONSTRAINT fk_idvacancy
	FOREIGN KEY (idvacancy) REFERENCES vacancy (id)
	ON DELETE RESTRICT
	ON UPDATE RESTRICT
);
CREATE INDEX idapplicantIndex ON interview (idapplicant);
CREATE INDEX idvacancy_idx ON interview (idvacancy);
CREATE TABLE IF NOT EXISTS interview_result (
	idinterview INT NOT NULL,
	recall TEXT,
	time_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	average_mark REAL,
	PRIMARY KEY (idinterview),
	CONSTRAINT fk_idinterview
	FOREIGN KEY (idinterview)
  REFERENCES interview (id)
	ON DELETE RESTRICT
	ON UPDATE RESTRICT
);
INSERT INTO user (login, password, surname, name) VALUES('Admin1', 'Admin1', '-', '-');

