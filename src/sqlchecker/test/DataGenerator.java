package sqlchecker.test;

public class DataGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Anzahl der Datens‰tze in Tabelle i
		int i = 26;
		
		//Anzahl der Datens‰tze in Tabelle j
		int j = 45;
		int zufallszahl;
		
		//Medikament Anzahl = 46
		String[] medi = new String[]{"AccAcut600","Altriserium","Antibabypille2","Antibabypille3","Antidoxin","Aspirin","AspirinComplex","Bepanthen","Betablocker","Blutungshaemmer1","Blutungshaemmer2","Botox","BoxaGripal","Didextrotrin","Dolormin","Dopanium","Fuﬂpilzsalbe1","Fuﬂpilzsalbe12","Geringungsmittel","Geringungsmittel2","GripostatC","Iberogast","Ibupropeth","Insulin","Kopfschmerzmittel","Maltrodextrin","Medikament1","Medikament2","Medikament3","Medikament4","Medikament5","Meditonsin","Miakum","Nasenspray","Nasentropfen","Ohrtropfen","Placebo","Ritalin","Schlafmittel","Sergos","Serobentin","Sinupret","Sorium","Thomapirn","Viagra","WundUndHeilSalbe"};
		
		//Krankheit Anzahl = 26
		String[] krankheit = new String[]{"fachname","Alternaria","Apoplex","Arderum","","Commotio cerebri","","Effectus","Everin","Fraktur","FussiJuckus","Gastroenteritis","Gripus","Hatschi","HIV","Husterus","Incidento","Infarkt","Insomnia","KopfAua","Lebensmittelintoxikation","Malitosus","Namrium","NervusKlemus","Ohrus","Propus","Sarati","Solares","Wundus"};
		
		for(int a = 1; a <= i; a++){
			zufallszahl = (int)(Math.random() * j) + 0; 
			System.out.println("("+a+",'"+medi[zufallszahl]+"'),");
			
		}
		
	}

}
