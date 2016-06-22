
/*static*/

drop function if exists sumab

/*static*/

create function sumab(a decimal(16, 4), b decimal(16, 4)) 
	returns decimal(16, 4) 
	deterministic return a + b;


/*static*/
	
sumab(150, 4)
sumab(9, 6)
sumab(40, 2)
sumab(400, 2)
sumab(123, 9)

/*1a*/

sumab(5, 6)
