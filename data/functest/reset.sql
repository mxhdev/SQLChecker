
/*static*/

DROP DATABASE IF EXISTS `dbfit`;

/*static*/

CREATE DATABASE `dbfit`;

/*static*/

create table `dbfit`.`Produkte` (
	pid int not null auto_increment primary key,
	bezeichnung varchar(512) not null,
	preis decimal(16, 2)
);