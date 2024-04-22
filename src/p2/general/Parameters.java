package p2.general;

import java.time.format.DateTimeFormatter;


public class Parameters {
	public static final String sep = System.getProperty("file.separator");
	public static final String userDir = System.getProperty("user.dir");
	
	/* Pfadname zu dem Verzeichnis mit den CSV Dateien */
	public static final String csvDirResults = 
			userDir + sep + "resources" + sep + "bundesliga_results" + sep;
	
	/* Pfadnamen zu den CSV Dateien */
	public static final String csvFileSeason1819 =
			csvDirResults + "bundesliga-1819.csv";
	public static final String csvFileSeason1920 =
			csvDirResults + "bundesliga-1920.csv";
	public static final String csvFileSeason2021 =
			csvDirResults + "bundesliga-2021.csv";
	public static final String csvFileSeason2122 =
			csvDirResults + "bundesliga-2122.csv";
	public static final String csvFileSeason2223 =
			csvDirResults + "bundesliga-2223.csv";
	public static final String csvFileSeason2324 =
			csvDirResults + "bundesliga-2324.csv";
	
	/* Feld mit den Dateipfadnamen zu allen CSV Dateien */ 
	public static final String[] seasonFiles = {
			csvFileSeason1819, csvFileSeason1920, csvFileSeason2021, 
			csvFileSeason2122, csvFileSeason2223, csvFileSeason2324
	};
	
	/* XML Datei mit den Daten zu allen Spielen */ 
	public static final String xmlFile = userDir + sep + "resources" + sep + "bundesliga_results.xml";
	
	/* Pfadname zur Datenbank */
	public static final String dbFile = userDir + sep + "resources" + sep + "bundesliga.db";
	
	/* Formatierer für die Daten zu den Spieltagen (Tag + Uhrzeit), welche den Text erstellen,
	 * welcher sowohl in der Datenbank als auch in dem XML-File verwendet werden soll.
	 */
	public static final DateTimeFormatter dtfDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter dtfDayTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

}
