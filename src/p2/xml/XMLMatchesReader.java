package p2.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.io.FileNotFoundException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import p2.data.Match;

public class XMLMatchesReader {
	private File file;
	private String season;
	private int round;
	private Collection<Match> matches;

	public XMLMatchesReader(String filename, String season, int round) {
		this.file = new File(filename);
		this.season = season;
		this.round = round;
	}
	
	public void parseFile() {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			SAXParser parser;
			parser = parserFactory.newSAXParser();
			org.xml.sax.XMLReader xmlReader = parser.getXMLReader();
			
			FileReader reader = new FileReader(this.file);
			InputSource inputSource = new InputSource(reader);
			
			
			MatchContentHandler handler = new MatchContentHandler(season, round);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(inputSource);			
			this.matches = handler.getMatches();						
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Generiert einen Ergebnistext mit den Matches in der Liste sBuf */
	public String getResults() {
		StringBuffer sBuf = new StringBuffer("Results Season: " +  this.season +  ", Round: " + this.round + "\n\n");
		for (Match m : this.matches) {
			sBuf.append(m.toString() + "\n");
		}
		return sBuf.toString();
	}
	
	public Collection<Match> getMatches() {
		return this.matches;
	}
}
