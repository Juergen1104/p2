package p2.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import p2.data.Match;
import p2.general.Parameters;

public class CSVReaderMatches {
	Collection<Match> matches = new ArrayList<Match>();
	String season;
	
	/* Methode öffnet die Datei, deren Namen als Parameter übergeben wird,
	 * ignoriert die erste Zeile (mit den Spaltenüberschriften) und 
	 * ruft für jede weitere (in Spalten aufgeteilte) Zeile die Methode 
	 * parseLine auf 
	 * Der Name der Saison wird aus dem Dateinamen abgeleitet.
	 */
	public void parseFile(String filename) {	
		try (BufferedReader bufR = new BufferedReader(new FileReader(filename))){
			String line = bufR.readLine(); // ignore first line
			season = filename.substring(filename.length()-8, filename.length()-4);
			season = season.substring(0,2) + "/" + season.substring(2);
			while ((line = bufR.readLine()) != null) {
				parseLine(line.split(",",7));
			}
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/* Methode verwendet die Spalteneinträge (Tokens) aus einer Zeile einer CSV-Datei
	 * um daraus eine Instanz der Klasse Match zu generieren, die in der Sammlung
	 * matches gespeichert wird.
	 * Für noch fehlende Resultate wird für die Tore jeweils der Wert -1 eingetragen 
	 */
	private void parseLine(String[] tokens) {		
		int roundNumber  = Integer.parseInt(tokens[1]); 
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm");
		LocalDateTime date = LocalDateTime.parse(tokens[2],dtf);
		String location = tokens[3];
		String homeTeam = tokens[4];
		String awayTeam = tokens[5];
		String[] tore = tokens[6].split(" - ");
		int goalsHomeTeam = -1, goalsAwayTeam = -1;
		if (tore.length == 2) {
			goalsHomeTeam = Integer.parseInt(tore[0]);
			goalsAwayTeam = Integer.parseInt(tore[1]);
		}
		String season = this.season;
		Match m = new Match(roundNumber, date, location, homeTeam, awayTeam, goalsHomeTeam, goalsAwayTeam, season);
		this.matches.add(m);	
	}
	

	/* Methode gibt die generierten  Match Instanzen in einer Sammlung zurück */
	public Collection<Match> getMatches() {
		return this.matches;
	}
	
	/* Ruft für alle Dateien im Verzeichnis resources/bundesliga_results die
	 * Methode parseFile auf
	 */
	public void parseAllFiles() {
		for (String filename : Parameters.seasonFiles) {
			parseFile(filename);
		}
	}
	
	/* ******************************************************************* */
	
	/* ...  zum Testen der Klasse */
	public void printInfo() {
		System.out.println(this.matches.size());
	}
	
	/* .... zum Testen der Klasse */
	public static void main(String[] args) {
		CSVReaderMatches csvrm = new CSVReaderMatches();
		for (String filename : Parameters.seasonFiles) {
			csvrm.parseFile(filename);
		}			
		csvrm.printInfo();
	}
	
}
