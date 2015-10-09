/*1a*/

select 'test' as x;


/*static*/
select * from produkte;


/*1b*/

create table Produkte (
	pid int not null auto_increment primary key,
	bezeichnung varchar(512) not null,
	preis decimal(16, 2)
);


/*static*/

CREATE FUNCTION filterProducts (gps INT) 
	returns TEXT 
	begin 
		declare bez TEXT; 
		set bez = (select bezeichnung from produkte where preis = gps); 
		return bez;
	end;


/*1c*/

insert into 
	Produkte 
values
	(1
	, 234);

/*static*/

select bezeichnung from produkte;

/*static*/

CREATE PROCEDURE TESTProcUC() BEGIN SELECT bezeichnung FROM produkte; END

/*static*/

TestProcUC()

/*static*/

filterProducts(59)
filterProducts(12)
filterProducts(814)
filterProducts(1000)
filterProducts(999)

/*1d*/

select bezeichnung from produkte where preis > 100;
