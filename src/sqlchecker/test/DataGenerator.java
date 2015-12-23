package sqlchecker.test;

import java.sql.Timestamp;

public class DataGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Anzahl der Datensätze in Tabelle i
		int i = 45;
		
		//Anzahl der Datensätze in Tabelle j
		int j = 12;
		int zufallszahl1;
		int zufallszahl2;
		int zufallszahl3;
		int zufallszahl4;
		int zufallszahl5;
		int zufallszahl6;
		
		//PNR der Ärzte Anzahl = 13
		int[] arzt = new int[]{4,5,8,12,13,14,23,33,34,35,40,43,45};
		
		//Medikament Anzahl = 46
		String[] medi = new String[]{"AccAcut600","Altriserium","Antibabypille2","Antibabypille3","Antidoxin","Aspirin","AspirinComplex","Bepanthen","Betablocker","Blutungshaemmer1","Blutungshaemmer2","Botox","BoxaGripal","Didextrotrin","Dolormin","Dopanium","Fußpilzsalbe1","Fußpilzsalbe12","Geringungsmittel","Geringungsmittel2","GripostatC","Iberogast","Ibupropeth","Insulin","Kopfschmerzmittel","Maltrodextrin","Medikament1","Medikament2","Medikament3","Medikament4","Medikament5","Meditonsin","Miakum","Nasenspray","Nasentropfen","Ohrtropfen","Placebo","Ritalin","Schlafmittel","Sergos","Serobentin","Sinupret","Sorium","Thomapirn","Viagra","WundUndHeilSalbe"};
		
		//Krankheit Anzahl = 25
		String[] krankheit = new String[]{"Alternaria","Apoplex","Arderum","Commotio cerebri","Effectus","Everin","Fraktur","FussiJuckus","Gastroenteritis","Gripus","Hatschi","HIV","Husterus","Incidento","Infarkt","Insomnia","KopfAua","Lebensmittelintoxikation","Malitosus","Namrium","NervusKlemus","Ohrus","Propus","Sarati","Solares","Wundus"};
		for(int a = 1; a <= i; a++){
			zufallszahl1 = (int)(Math.random() * i) + 1; 
			zufallszahl2 = (int)(Math.random() * j) + 0;
			zufallszahl3 = (int)(Math.random() * 25) + 0;
			zufallszahl4 = (int)(Math.random() * 832) + 1;
			zufallszahl5 = (int)(Math.random() * 10) + 1;
			zufallszahl6 = (int)(Math.random() * 59) + 0;
			System.out.println("('"+medi[a]+"', '"+ krankheit[zufallszahl3] +"'),");
			
		}
		
	}
	
	public static String timeStampGenerator(){
		long offset = Timestamp.valueOf("2016-01-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2016-02-01 00:00:00").getTime();
		long diff = end - offset + 1;
		Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
		
		return rand.toString();
	}

}
