
/*1a*/

select 'test' as x;


/*static*/
select * from produkte;

/*1b*/

insert into 
	produkte 
values
	(1
	, 234);

/*static*/

select bezeichnung from produkte;

/*1c*/

select bezeichnung from produkte where preis > 100;