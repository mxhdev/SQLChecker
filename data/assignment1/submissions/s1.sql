/*1a*/

SELECT bezeichnung, preis FROM produkte
-- Dies ist ein Kommentar für s1 1a
/*1b*/

INSERT INTO 
	produkte(`bezeichnung`, `preis`) 
VALUES
	('chair', 80);
/*1c*/

SELECT bezeichnung, preis FROM produkte

/*
result of this for s1 1c:
p;;p;p
*/