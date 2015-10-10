/*1a*/

select 'test' as x;


/*static*/
select * from Produkte;




/*static*/

DROP PROCEDURE if exists CalcLength

/*static*/

CREATE PROCEDURE CalcLength(IN name varchar(100), OUT strlength int) 
	set strlength = length(name);

/*static*/
	
CalcLength('abc', @strlength)
CalcLength('FiveLetters', @strlength)

/*static*/

DROP PROCEDURE if exists CalcLength


/*static*/

drop function if exists sumab

/*static*/

create function sumab(a decimal(16, 4), b decimal(16, 4)) 
	returns decimal(16, 4) 
	deterministic return a + b;


/*static*/
	
sumab(150, 4)
sumab(9, 6)

/*static*/

DROP function if exists sumab


/*static*/

DROP PROCEDURE if exists testproc

/*static*/

CREATE PROCEDURE testproc() 
	BEGIN 
		SELECT bezeichnung FROM produkte; 
	END


/*static*/
	
testproc()

/*static*/

DROP PROCEDURE if exists testproc


/*static*/

DROP PROCEDURE if exists procInsert

/*static*/

CREATE PROCEDURE procInsert(in px INT) 
	BEGIN 
		insert into produkte(bezeichnung, preis) values ('tablet', px); 
	END


/*static*/
	
procInsert(588)


/*static*/

DROP PROCEDURE if exists procInsert



/*static*/

drop function if exists GiveFive

/*static*/

create function GiveFive() 
	returns decimal(16, 4) 
	deterministic return 5;



/*static*/
	
GiveFive()


/*static*/

DROP function if exists GiveFive



/*static*/

DROP PROCEDURE IF EXISTS PlusEins

/*static*/

CREATE PROCEDURE PlusEins(INOUT val int) 
	set val = val + 1;



/*static*/
	
PlusEins(41)


/*static*/

DROP PROCEDURE IF EXISTS PlusEins





/*1b*/

select bezeichnung from produkte where preis > 100;
