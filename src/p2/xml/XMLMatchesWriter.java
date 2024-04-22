package p2.xml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import p2.data.Match;
import p2.general.Parameters;
import p2.io.CSVReaderMatches;

public class XMLMatchesWriter {
	private Document doc;

	/* ************************************************************** */
	/* ***                      Aufgabe 2a                        *** */
	/* ************************************************************** */

	// 2
	private Element findSeasonElement(String season) {
		Element seasonElement = null;
		seasonElement = doc.getRootElement().getChild(season);
		return seasonElement;
	}

	// 2

	private Element findRoundElement(Element seasonElement, int round) {

		// Überprüfen, ob das übergebene Saison-Element gültig ist
		if (seasonElement == null || seasonElement.getChildren() == null) {
			return null;
		}

		// Durchsuche die direkten Unterelemente des übergebenen Saison-Elements
		for (Element roundElement : seasonElement.getChildren()) {
			// Überprüfen, ob das Unterelement ein Rundenelement ist und die gewünschte Rundennummer hat
			if (roundElement.getName().equals("Round") && roundElement.getAttribute("number") != null) {
				//int roundNum = Integer.parseInt(roundElement.getAttribute("number"));
				int roundNum = 0;
				try {
					roundNum = roundElement.getAttribute("number").getIntValue();
				} catch (DataConversionException e) {
					throw new RuntimeException(e);
				}
				if (roundNum == round) {
					return roundElement; // Das gesuchte Rundenelement wurde gefunden
				}
			}
		}
		return null; // Das gesuchte Rundenelement wurde nicht gefunden
	}

	// 3
	private Element createMatchElement(Match m) {

		Element matchElement = new Element("Match");

		// Setze die Attribute für das Match-Element
		matchElement.setAttribute("roundNumber", String.valueOf(m.getRoundNumber()));
		matchElement.setAttribute("date", m.getDate().toString());
		matchElement.setAttribute("location", m.getLocation());
		matchElement.setAttribute("season", m.getSeason());

		// Erstelle das Element für die Heimmannschaft
		Element homeTeamElement = new Element("HomeTeam");
		homeTeamElement.setText(m.getHomeTeam());

		// Erstelle das Element für die Gastmannschaft
		Element awayTeamElement = new Element("AwayTeam");
		awayTeamElement.setText(m.getAwayTeam());

		// Erstelle das Element für das Ergebnis
		Element resultElement = new Element("Result");
		int goalsHomeTeam = m.getGoalsHomeTeam();
		int goalsAwayTeam = m.getGoalsAwayTeam();
		if (goalsHomeTeam == -1 || goalsAwayTeam == -1) {
			// Wenn Tore nicht gesetzt sind, setze das Attribut auf "NA"
			resultElement.setAttribute("status", "NA");
		} else {
			// Setze das Ergebnis auf die Anzahl der Tore der Heim- und Gastmannschaft
			resultElement.setText(goalsHomeTeam + " : " + goalsAwayTeam);
		}
		matchElement.addContent(homeTeamElement);
		matchElement.addContent(awayTeamElement);
		matchElement.addContent(resultElement);

		return matchElement;
	}

	// 3
	public void addMatches(Collection<Match> matches) {

		Element rootElement = doc.getRootElement(); // Annahme: Die Wurzel des Dokuments

		// Durchlaufen der übergebenen Matches
		for (Match match : matches) {
			// Bestimme das Saison-Element für das Match
			Element seasonElement = getOrCreateSeasonElement(doc, rootElement, match.getSeason());

			// Bestimme das Runden-Element für das Match
			Element roundElement = getOrCreateRoundElement(doc, seasonElement, match.getRoundNumber());

			// Erzeuge das Match-Element und hänge es unter dem Runden-Element ein
			Element matchElement = createMatchElement(match);
			roundElement.addContent(matchElement);
		}
	}
	private static Element getOrCreateSeasonElement(Document doc, Element rootElement, String season) {
		// Durchsuche vorhandene Saison-Elemente
		for (Element seasonElement : rootElement.getChildren("Season")) {
			if (seasonElement.getAttributeValue("value").equals(season)) {
				return seasonElement; // Rückgabe des vorhandenen Saison-Elements
			}
		}

		// Erzeuge ein neues Saison-Element, falls keines gefunden wurde
		Element newSeasonElement = new Element("Season");
		newSeasonElement.setAttribute("value", season);
		rootElement.addContent(newSeasonElement);
		return newSeasonElement;
	}

	private static Element getOrCreateRoundElement(Document doc, Element seasonElement, int roundNumber) {
		// Durchsuche vorhandene Runden-Elemente
		for (Element roundElement : seasonElement.getChildren("Round")) {
			if (Integer.parseInt(roundElement.getAttributeValue("number")) == roundNumber) {
				return roundElement; // Rückgabe des vorhandenen Runden-Elements
			}
		}

		// Erzeuge ein neues Runden-Element, falls keines gefunden wurde
		Element newRoundElement = new Element("Round");
		newRoundElement.setAttribute("number", String.valueOf(roundNumber));
		seasonElement.addContent(newRoundElement);
		return newRoundElement;
	}

	/* ********************************** */
	/* *** ab hier nichts mehr ändern *** */
	/* ********************************** */

	public void createDoc() {
		Element root = new Element("results_Bundesliga");
		this.doc = new Document(root);
	}

	public void writeDoc() {
		if (this.doc != null) {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			FileWriter fw;
			try {
				fw = new FileWriter(Parameters.xmlFile);
				out.output(doc, fw);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		CSVReaderMatches csvrm = new CSVReaderMatches();
		csvrm.parseAllFiles();
		Collection<Match> matches = csvrm.getMatches();
		XMLMatchesWriter xmlw = new XMLMatchesWriter();
		xmlw.createDoc();
		xmlw.addMatches(matches);
		xmlw.writeDoc();
	}

}
