package p2.xml;

import org.jdom2.Document;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import p2.data.Match;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class XMLResultWriter {
	private File file;
	private Document doc;
	
	
	/* ************************************************************** */
	/* ***                      Aufgabe 3b                        *** */
	/* ************************************************************** */

	
	// 6P
	public void writeResult(Match m, int goalsHome, int goalsAway) {

		Element matchElement = findMatchElement(m, doc.getRootElement().getChildren("Season"));
		if (matchElement != null) {
			// Aktualisieren Sie das Resultat im Match-Element
			Element resultElement = matchElement.getChild("Result");
			if (resultElement != null) {
				resultElement.setText(goalsHome + " : " + goalsAway);
				if (goalsHome == -1 || goalsAway == -1) {
					resultElement.setAttribute("status", "NA");
				} else {
					resultElement.removeAttribute("status");
				}
			}

			// Speichern Sie das aktualisierte XML-Dokument
			saveXML(doc);
		}
	}
	private static Element findMatchElement(Match m, List<Element> seasons) {
		for (Element seasonElement : seasons) {
			for (Element roundElement : seasonElement.getChildren("Round")) {
				for (Element matchElement : roundElement.getChildren("Match")) {
					if (matchElement.getChildText("HomeTeam").equals(m.getHomeTeam())
							&& matchElement.getChildText("AwayTeam").equals(m.getAwayTeam())
							&& m.getDate().toString().equals(matchElement.getAttributeValue("date"))) {
						return matchElement;
					}
				}
			}
		}
		return null;
	}

	private static void saveXML(Document doc) {
		try {
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter("matches.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* *********************************************************** */
	/*                ab hier nichts mehr ändern                   */
	/* *********************************************************** */
	
	/* Konstruktor: setzt den Namen für die Datei mit dem XML File */
	public XMLResultWriter(String filename ) {
		this.file = new File(filename);	
	}
	
	/* Methode prüft, ob das XML Dokument bereits geladen ist. Wenn nicht, wird 
	 * versucht, das Dokument aus der Datei zu laden. */
	public void loadXMLFileIfNotDoneBefore() {
		if (doc == null) {
			try {
				if (file.exists()) {
					this.doc = new SAXBuilder().build(file.getPath());
				} else {
					System.out.println("File " + file.getCanonicalPath() + " does not exist");
				}
			} catch(IOException ioe){
				ioe.printStackTrace();
			} catch(JDOMException jde){
				jde.printStackTrace();
			}
		}
	}
	
	
	/* *** save possible changes to file *** */	
	public void saveDoc() {
		if (this.doc != null) {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			FileWriter fw;
			try {
				fw = new FileWriter(this.file);
				out.output(doc, fw);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
