/*1a*/

SELECT bezeichnung FROM produkte
-- Dies ist ein Kommentar für s1fail 1a

/*1b*/

INSERT INTO123 
	produkte(`bezeichnung`, `preis`) 
VALUES
	('chair', 80)
-- s1fail 1b Kommentar
/*1c*/

SELECT bezeichnung, preis FROM produkte

/*
result of this in s1fail 1c:
pie;e;pf;pf
*/