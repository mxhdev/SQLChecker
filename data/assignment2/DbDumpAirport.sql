CREATE DATABASE  IF NOT EXISTS `airport` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `airport`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: localhost    Database: airport
-- ------------------------------------------------------
-- Server version	5.6.14

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `AddressID` int(11) NOT NULL,
  `ZIPCode` varchar(10) NOT NULL,
  `Town` varchar(100) NOT NULL,
  `Street` varchar(100) NOT NULL,
  `CountryISO3166_2LetterCode` char(2) NOT NULL,
  PRIMARY KEY (`AddressID`),
  KEY `CountryISO3166_2LetterCode` (`CountryISO3166_2LetterCode`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`CountryISO3166_2LetterCode`) REFERENCES `country` (`CountryISO3166_2LetterCode`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'13405','Berlin','Flughafen Tegel','DE'),(2,'40474','Düsseldorf','Flughafenstraße 120','DE'),(3,'60547','Frankfurt am Main','Frankfurt Flughafen','DE'),(4,'85356','München','Nordallee 25','DE'),(5,'95700','Paris','Paris Charles de Gaulle Airport','FR'),(6,'19-004','Attiki Odos','Spata Artemida','GR'),(7,'TW6','London','Greater London TW6','GB'),(8,'40','Ciampino Roma','Via Appia Nuova 1651','IT'),(9,'2061','Gardermoen','Edvard Munchs veg','NO'),(10,'6301','Mississauga','Silver Dart Dr','CA'),(11,'60512','Frankfurt am Main','Hauptstraße 1','DE'),(12,'60157','Frankfurt am Main','Fischerstraße 42','DE'),(13,'60134','Frankfurt am Main','Bankweg 135','DE'),(14,'65495','Frankfurt am Main','Windgasse 9','DE'),(15,'64358','Frankfurt am Main','Achenbachstraße 5','DE'),(16,'65260','Frankfurt am Main','Adalbertstraße 3','DE'),(17,'60154','Frankfurt am Main','Adam-Opel-Straße 4','DE'),(18,'10115','Berlin','Zimmerstraße 4','DE'),(19,'10179','Berlin','Rudi-Dutschke-Straße 134','DE'),(20,'10243','Berlin','Oranienstraße 9','DE');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `airport`
--

DROP TABLE IF EXISTS `airport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `airport` (
  `ICAO_Code` varchar(4) NOT NULL,
  `AirportName` varchar(100) NOT NULL,
  `AddressID` int(11) NOT NULL,
  PRIMARY KEY (`ICAO_Code`),
  KEY `AddressID` (`AddressID`),
  CONSTRAINT `airport_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `address` (`AddressID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airport`
--

LOCK TABLES `airport` WRITE;
/*!40000 ALTER TABLE `airport` DISABLE KEYS */;
INSERT INTO `airport` VALUES ('CYYZ','Toronto Pearson International Airport',10),('EDDF','Frankfurt am Main',3),('EDDL','Düsseldorf International',2),('EDDM','München (Franz Josef Strauß)',4),('EDDT','Berlin-Tegel (Otto-Lilienthal)',1),('EGLC','London City Airport',7),('ENGM','Flughafen Oslo-Gardermoen',9),('LFPG','Paris-Charles de Gaulle',5),('LGAV','Athens International Airport',6),('LIRA','Flughafen Rom-Ciampino',8);
/*!40000 ALTER TABLE `airport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `CountryISO3166_2LetterCode` char(2) NOT NULL,
  `CountryName` varchar(100) NOT NULL,
  PRIMARY KEY (`CountryISO3166_2LetterCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES ('CA','Canada'),('DE','Germany'),('FR','France'),('GB','United Kingdom'),('GR','Greece'),('IT','Italy'),('NO','Norway');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `Matrikelnummer` int(11) NOT NULL,
  `LastName` varchar(50) NOT NULL,
  `FirstName` varchar(50) NOT NULL,
  `AddressID` int(11) NOT NULL,
  PRIMARY KEY (`Matrikelnummer`),
  KEY `AddressID` (`AddressID`),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `address` (`AddressID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (15943155,'Müller','Elias',13),(15946782,'Richter','Thomas',20),(16579435,'Schmidt','Alexander',11),(28943155,'Weber','Sabrina',16),(31764155,'Koch','Nicole',17),(35941358,'Weber','Sabine',18),(35943143,'Schneider','Daniel',14),(35943147,'Schmidt','Stefanie',12),(73941738,'Fischer','David',15),(99188655,'Neumann','Emil',19);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flightexecution`
--

DROP TABLE IF EXISTS `flightexecution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `flightexecution` (
  `FlightNo` varchar(7) NOT NULL,
  `DepartureDateAndTimeUTC` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ICAO_Code_Origin` varchar(4) NOT NULL,
  `ICAO_Code_Destination` varchar(4) NOT NULL,
  `PlaneID` int(11) NOT NULL,
  `FlightDurationInMinutes` int(11) NOT NULL,
  PRIMARY KEY (`FlightNo`,`DepartureDateAndTimeUTC`),
  KEY `PlaneID` (`PlaneID`),
  KEY `ICAO_Code_Origin` (`ICAO_Code_Origin`),
  KEY `ICAO_Code_Destination` (`ICAO_Code_Destination`),
  CONSTRAINT `flightexecution_ibfk_1` FOREIGN KEY (`PlaneID`) REFERENCES `plane` (`PlaneID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `flightexecution_ibfk_2` FOREIGN KEY (`ICAO_Code_Origin`) REFERENCES `airport` (`ICAO_Code`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `flightexecution_ibfk_3` FOREIGN KEY (`ICAO_Code_Destination`) REFERENCES `airport` (`ICAO_Code`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flightexecution`
--

LOCK TABLES `flightexecution` WRITE;
/*!40000 ALTER TABLE `flightexecution` DISABLE KEYS */;
INSERT INTO `flightexecution` VALUES ('IBE1684','2014-12-31 10:30:45','LIRA','ENGM',2,200),('IBE1764','2014-12-31 10:30:45','EGLC','EDDT',1,180),('IBE1846','2014-12-31 10:30:45','EDDF','EDDL',3,160),('IBE3843','2014-12-31 10:30:45','ENGM','EDDM',2,120),('IBE4681','2014-12-31 10:30:45','EDDF','LIRA',4,180),('LH1167','2014-12-31 10:30:45','LFPG','LGAV',2,180),('LH1354','2014-12-31 10:30:45','EDDT','LIRA',1,120),('LH1761','2014-12-31 10:30:45','EDDF','EDDT',4,240),('LH1769','2014-12-31 10:30:45','LIRA','EDDF',1,180),('LH1943','2014-12-31 10:30:45','EDDL','EGLC',2,180),('LH1973','2014-12-31 10:30:45','EDDF','LFPG',3,180),('LH2301','2014-07-26 10:50:00','EDDF','CYYZ',2,340),('LH3333','2014-06-05 09:30:45','EDDF','EDDM',1,155),('LH3584','2014-12-31 10:30:45','EDDM','EDDF',1,180),('LH3842','2014-12-31 10:30:45','EGLC','EDDL',4,120),('LH5301','2014-07-02 16:20:00','EDDF','CYYZ',1,740),('LH7660','2014-12-31 10:30:45','LGAV','EDDM',3,240);
/*!40000 ALTER TABLE `flightexecution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `flights_from_eddf_commingweek`
--

DROP TABLE IF EXISTS `flights_from_eddf_commingweek`;
/*!50001 DROP VIEW IF EXISTS `flights_from_eddf_commingweek`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `flights_from_eddf_commingweek` (
  `FlightNo` tinyint NOT NULL,
  `DepartureDateAndTimeUTC` tinyint NOT NULL,
  `ICAO_Code_Destination` tinyint NOT NULL,
  `PlaneType` tinyint NOT NULL,
  `NoOfSeats` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `kunden_details`
--

DROP TABLE IF EXISTS `kunden_details`;
/*!50001 DROP VIEW IF EXISTS `kunden_details`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `kunden_details` (
  `Matrikelnummer` tinyint NOT NULL,
  `Nachname` tinyint NOT NULL,
  `Vorname` tinyint NOT NULL,
  `Stadt` tinyint NOT NULL,
  `Strasse` tinyint NOT NULL,
  `Land` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `kunden_deutschland`
--

DROP TABLE IF EXISTS `kunden_deutschland`;
/*!50001 DROP VIEW IF EXISTS `kunden_deutschland`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `kunden_deutschland` (
  `Matrikelnummer` tinyint NOT NULL,
  `Nachname` tinyint NOT NULL,
  `Vorname` tinyint NOT NULL,
  `Stadt` tinyint NOT NULL,
  `Strasse` tinyint NOT NULL,
  `Land` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `plane`
--

DROP TABLE IF EXISTS `plane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plane` (
  `PlaneID` int(11) NOT NULL,
  `NoOfSeats` int(11) NOT NULL,
  `PlaneType` varchar(100) NOT NULL,
  `PlaneName` varchar(100) NOT NULL,
  PRIMARY KEY (`PlaneID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plane`
--

LOCK TABLES `plane` WRITE;
/*!40000 ALTER TABLE `plane` DISABLE KEYS */;
INSERT INTO `plane` VALUES (1,555,'Airbus','Airbus A380'),(2,85,'Fokker','Fokker F100'),(3,162,'Boeing','Boeing 737'),(4,380,'Airbus','Airbus A340');
/*!40000 ALTER TABLE `plane` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation` (
  `ReservationID` int(11) NOT NULL,
  `Matrikelnummer` int(11) NOT NULL,
  `NoReservedSeats` int(11) NOT NULL,
  `Comment` varchar(1000) DEFAULT NULL,
  `FlightNo` varchar(7) NOT NULL,
  `DepartureDateAndTimeUTC` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ReservationID`),
  KEY `Matrikelnummer` (`Matrikelnummer`),
  KEY `FlightNo` (`FlightNo`,`DepartureDateAndTimeUTC`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`Matrikelnummer`) REFERENCES `customer` (`Matrikelnummer`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`FlightNo`, `DepartureDateAndTimeUTC`) REFERENCES `flightexecution` (`FlightNo`, `DepartureDateAndTimeUTC`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,16579435,1,'','LH1973','2014-12-31 10:30:45'),(2,35943147,1,'','LH3584','2014-12-31 10:30:45'),(3,15943155,2,'Die Plätze sollten nebeneinander liegen','LH1167','2014-12-31 10:30:45'),(4,35943143,1,'','LH7660','2014-12-31 10:30:45'),(5,73941738,1,'','LH3842','2014-12-31 10:30:45'),(6,28943155,3,'','LH1769','2014-12-31 10:30:45'),(7,31764155,1,'','IBE1846','2014-12-31 10:30:45'),(8,35941358,1,'Fensterplatz bitte','IBE1764','2014-12-31 10:30:45'),(9,99188655,2,'','LH1354','2014-12-31 10:30:45'),(10,15946782,1,'','IBE1684','2014-12-31 10:30:45'),(11,28943155,1,'','LH1761','2014-12-31 10:30:45'),(12,31764155,4,'','IBE4681','2014-12-31 10:30:45'),(13,35941358,1,'Platz am Gang bitte','IBE3843','2014-12-31 10:30:45'),(14,99188655,2,'','LH1943','2014-12-31 10:30:45'),(15,15946782,1,'','IBE3843','2014-12-31 10:30:45'),(16,31764155,2,'','LH2301','2014-07-26 10:50:00'),(17,15946782,7,'','LH2301','2014-07-26 10:50:00'),(18,99188655,1,'','LH5301','2014-07-02 18:20:00');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

INSERT INTO `airport`.`country` (`CountryISO3166_2LetterCode`, `CountryName`) VALUES
('AD', 'Andorra'),
('AE', 'United Arab Emirates'),
('AF', 'Afghanistan'),
('AG', 'Antigua and Barbuda'),
('AI', 'Anguilla'),
('AL', 'Albania'),
('AM', 'Armenia'),
('AO', 'Angola'),
('AQ', 'Antarctica'),
('AR', 'Argentina'),
('AS', 'American Samoa'),
('AT', 'Austria'),
('AU', 'Australia'),
('AW', 'Aruba'),
('AX', 'Aland Islands'),
('AZ', 'Azerbaijan'),
('BA', 'Bosnia and Herzegovina'),
('BB', 'Barbados'),
('BD', 'Bangladesh'),
('BE', 'Belgium'),
('BF', 'Burkina Faso'),
('BG', 'Bulgaria'),
('BH', 'Bahrain'),
('BI', 'Burundi'),
('BJ', 'Benin'),
('BL', 'Saint Barth lemy'),
('BM', 'Bermuda'),
('BN', 'Brunei Darussalam'),
('BO', 'Bolivia'),
('BQ', 'Bonaire, Sint Eustatius and Saba'),
('BR', 'Brazil'),
('BS', 'Bahamas'),
('BT', 'Bhutan'),
('BV', 'Bouvet Island'),
('BW', 'Botswana'),
('BY', 'Belarus'),
('BZ', 'Belize'),
('CC', 'Cocos (Keeling) Islands'),
('CD', 'Congo, the Democratic Republic of the'),
('CF', 'Central African Republic'),
('CG', 'Congo'),
('CH', 'Switzerland'),
('CI', 'Cte dIvoire'),
('CK', 'Cook Islands'),
('CL', 'Chile'),
('CM', 'Cameroon'),
('CN', 'China'),
('CO', 'Colombia'),
('CR', 'Costa Rica'),
('CU', 'Cuba'),
('CV', 'Cape Verde'),
('CW', 'Cura?ao'),
('CX', 'Christmas Island'),
('CY', 'Cyprus'),
('CZ', 'Czech Republic'),
('DJ', 'Djibouti'),
('DK', 'Denmark'),
('DM', 'Dominica'),
('DO', 'Dominican Republic'),
('DZ', 'Algeria'),
('EC', 'Ecuador'),
('EE', 'Estonia'),
('EG', 'Egypt'),
('EH', 'Western Sahara'),
('ER', 'Eritrea'),
('ES', 'Spain'),
('ET', 'Ethiopia'),
('FI', 'Finland'),
('FJ', 'Fiji'),
('FK', 'Falkland Islands (Malvinas)'),
('FM', 'Federated States of Micronesia'),
('FO', 'Faroe Islands'),
('GA', 'Gabon'),
('GD', 'Grenada'),
('GE', 'Georgia'),
('GF', 'French Guiana'),
('GG', 'Guernsey'),
('GH', 'Ghana'),
('GI', 'Gibraltar'),
('GL', 'Greenland'),
('GM', 'Gambia'),
('GN', 'Guinea'),
('GP', 'Guadeloupe'),
('GQ', 'Equatorial Guinea'),
('GS', 'South Georgia and the South Sandwich Islands'),
('GT', 'Guatemala'),
('GU', 'Guam'),
('GW', 'Guinea-Bissau'),
('GY', 'Guyana'),
('HK', 'Hong Kong'),
('HM', 'Heard Island and McDonald Islands'),
('HN', 'Honduras'),
('HR', 'Croatia'),
('HT', 'Haiti'),
('HU', 'Hungary'),
('ID', 'Indonesia'),
('IE', 'Ireland'),
('IL', 'Israel'),
('IM', 'Isle of Man'),
('IN', 'India'),
('IO', 'British Indian Ocean Territory'),
('IQ', 'Iraq'),
('IR', 'Iran'),
('IS', 'Iceland'),
('JE', 'Jersey'),
('JM', 'Jamaica'),
('JO', 'Jordan'),
('JP', 'Japan'),
('KE', 'Kenya'),
('KG', 'Kyrgyzstan'),
('KH', 'Cambodia'),
('KI', 'Kiribati'),
('KM', 'Comoros'),
('KN', 'Saint Kitts and Nevis'),
('KP', 'North Korea'),
('KR', 'South Korea'),
('KW', 'Kuwait'),
('KY', 'Cayman Islands'),
('KZ', 'Kazakhstan'),
('LA', 'Laos'),
('LB', 'Lebanon'),
('LC', 'Saint Lucia'),
('LI', 'Liechtenstein'),
('LK', 'Sri Lanka'),
('LR', 'Liberia'),
('LS', 'Lesotho'),
('LT', 'Lithuania'),
('LU', 'Luxembourg'),
('LV', 'Latvia'),
('LY', 'Libya'),
('MA', 'Morocco'),
('MC', 'Monaco'),
('MD', 'Moldova'),
('ME', 'Montenegro'),
('MF', 'Saint Martin (French part)'),
('MG', 'Madagascar'),
('MH', 'Marshall Islands'),
('MK', 'Macedonia'),
('ML', 'Mali'),
('MM', 'Myanmar'),
('MN', 'Mongolia'),
('MO', 'Macao'),
('MP', 'Northern Mariana Islands'),
('MQ', 'Martinique'),
('MR', 'Mauritania'),
('MS', 'Montserrat'),
('MT', 'Malta'),
('MU', 'Mauritius'),
('MV', 'Maldives'),
('MW', 'Malawi'),
('MX', 'Mexico'),
('MY', 'Malaysia'),
('MZ', 'Mozambique'),
('NA', 'Namibia'),
('NC', 'New Caledonia'),
('NE', 'Niger'),
('NF', 'Norfolk Island'),
('NG', 'Nigeria'),
('NI', 'Nicaragua'),
('NL', 'Netherlands'),
('NP', 'Nepal'),
('NR', 'Nauru'),
('NU', 'Niue'),
('NZ', 'New Zealand'),
('OM', 'Oman'),
('PA', 'Panama'),
('PE', 'Peru'),
('PF', 'French Polynesia'),
('PG', 'Papua New Guinea'),
('PH', 'Philippines'),
('PK', 'Pakistan'),
('PL', 'Poland'),
('PM', 'Saint Pierre and Miquelon'),
('PN', 'Pitcairn'),
('PR', 'Puerto Rico'),
('PS', 'Palestine, State of'),
('PT', 'Portugal'),
('PW', 'Palau'),
('PY', 'Paraguay'),
('QA', 'Qatar'),
('RE', 'R?union'),
('RO', 'Romania'),
('RS', 'Serbia'),
('RU', 'Russian Federation'),
('RW', 'Rwanda'),
('SA', 'Saudi Arabia'),
('SB', 'Solomon Islands'),
('SC', 'Seychelles'),
('SD', 'Sudan'),
('SE', 'Sweden'),
('SG', 'Singapore'),
('SH', 'Saint Helena, Ascension and Tristan da Cunha'),
('SI', 'Slovenia'),
('SJ', 'Svalbard and Jan Mayen'),
('SK', 'Slovakia'),
('SL', 'Sierra Leone'),
('SM', 'San Marino'),
('SN', 'Senegal'),
('SO', 'Somalia'),
('SR', 'Suriname'),
('SS', 'South Sudan'),
('ST', 'Sao Tome and Principe'),
('SV', 'El Salvador'),
('SX', 'Sint Maarten (Dutch part)'),
('SY', 'Syrian Arab Republic'),
('SZ', 'Swaziland'),
('TC', 'Turks and Caicos Islands'),
('TD', 'Chad'),
('TF', 'French Southern Territories'),
('TG', 'Togo'),
('TH', 'Thailand'),
('TJ', 'Tajikistan'),
('TK', 'Tokelau'),
('TL', 'Timor-Leste'),
('TM', 'Turkmenistan'),
('TN', 'Tunisia'),
('TO', 'Tonga'),
('TR', 'Turkey'),
('TT', 'Trinidad and Tobago'),
('TV', 'Tuvalu'),
('TW', 'Taiwan, Province of China'),
('TZ', 'Tanzania, United Republic of'),
('UA', 'Ukraine'),
('UG', 'Uganda'),
('UM', 'United States Minor Outlying Islands'),
('US', 'United States'),
('UY', 'Uruguay'),
('UZ', 'Uzbekistan'),
('VA', 'Vatican City State'),
('VC', 'Saint Vincent and the Grenadines'),
('VE', 'Venezuela, Bolivarian Republic of'),
('VG', 'Virgin Islands, British'),
('VI', 'Virgin Islands, U.S.'),
('VN', 'Viet Nam'),
('VU', 'Vanuatu'),
('WF', 'Wallis and Futuna'),
('WS', 'Samoa'),
('YE', 'Yemen'),
('YT', 'Mayotte'),
('ZA', 'South Africa'),
('ZM', 'Zambia'),
('ZW', 'Zimbabwe');


-- b)

INSERT INTO `airport`.`flightexecution` (`FlightNo`, `DepartureDateAndTimeUTC`, `ICAO_Code_Origin`, `ICAO_Code_Destination`, `PlaneID`, `FlightDurationInMinutes`) VALUES
('LH1231', '2014-12-01 17:30:45', 'LIRA', 'ENGM', 2, 200),
('LH1232', '2014-12-02 13:20:45', 'EGLC', 'EDDT', 1, 180),
('LH1233', '2014-12-03 15:15:45', 'EDDF', 'EDDL', 3, 160),
('LH1234', '2014-12-04 17:35:45', 'ENGM', 'EDDM', 2, 120),
('LH1235', '2014-12-05 19:10:45', 'EDDF', 'LIRA', 4, 180),
('IBE1231', '2014-12-06 11:30:45', 'LFPG', 'LGAV', 2, 180),
('IBE1232', '2014-12-07 20:20:45', 'EDDT', 'LIRA', 1, 120),
('IBE1233', '2014-12-08 23:40:45', 'EDDF', 'EDDT', 4, 240),
('IBE1234', '2014-12-09 12:45:45', 'LIRA', 'EDDF', 1, 180),
('IBE1235', '2014-12-10 10:35:45', 'EDDL', 'EGLC', 2, 180);