
/*static*/

DROP DATABASE `dbfit`;

/*static*/

CREATE DATABASE `dbfit`;

/*static*/

create table `dbfit`.`Produkte` (
	pid int not null auto_increment primary key,
	bezeichnung varchar(512) not null,
	preis decimal(16, 2)
);

/*static*/

insert into `dbfit`.`Produkte`(bezeichnung, preis)
values ('big pc', 1500), ('phone', 430), ('tv', 1200), ('tablet', 450);


/*static*/

CREATE PROCEDURE `dbfit`.`filterByPrice`(IN minPrice int)
	BEGIN
		select bezeichnung, preis 
        from produkte 
        where preis > minPrice;
	END
	
/*static*/

CREATE PROCEDURE `dbfit`.`filterPrices`()
	BEGIN 
		select bezeichnung, preis from produkte where preis > 200; 
	END

/*static*/

CREATE PROCEDURE `dbfit`.`getProdNames`()
	BEGIN
		select bezeichnung from produkte;
	END
	


	
	
	