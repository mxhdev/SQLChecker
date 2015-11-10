
/*1a*/

SELECT
c.firstname,
c.lastname,
r.Matrikelnummer,
SUM(r.NoReservedSeats)
FROM
reservation r,
customer c
WHERE
r.matrikelnummer = c.matrikelnummer
GROUP BY r.Matrikelnummer
HAVING SUM(r.NoReservedSeats) = 4;



/*1b*/

SELECT
COUNT(DISTINCT (r.Matrikelnummer))
FROM
reservation r,
customer cu,
address a,
country co
WHERE
r.Matrikelnummer = cu.Matrikelnummer
AND cu.AddressID = a.AddressID
AND a.town LIKE 'Berlin';



/*1c*/

SELECT
sum(`r`.`NoReservedSeats`) AS `NoReservedSeats`
FROM
(`flightexecution` `f`
JOIN `reservation` `r`)
WHERE
((`f`.`ICAO_Code_Origin` = 'EDDF')
AND (`f`.`ICAO_Code_Destination` = 'CYYZ')
AND (`f`.`FlightNo` = `r`.`FlightNo`)
AND (`f`.`DepartureDateAndTimeUTC` > NOW())
AND (`f`.`DepartureDateAndTimeUTC` < (NOW() + INTERVAL 65 DAY)));



/*1d*/

SELECT
DISTINCT r.FlightNo,
(SELECT SUM(rv.NoReservedSeats)
FROM reservation rv
WHERE rv.FlightNo = r.FlightNo) AS NoOfReservedSeatsPerFlight
FROM reservation r;


