package p2.xml;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import p2.data.Match;

import static java.time.LocalDateTime.parse;

public class MatchContentHandler implements ContentHandler {
    private String season;       // ausgewählte Saison
    private int round;           // ausgewählte Runde

    private List<Match> matches = new ArrayList<>(9); // Liste für die Matches
    private String charSequence;         // im XML-File "gefundene" Zeichensequenzen

    private String currentSeason;
    private LocalDateTime dateTime;
    private int currentRound;
    private boolean isInSelectedSeasonAndRound;
    private String homeTeam;
    private String awayTeam;
    private String location;
    private String date;
    private int goalsHomeTeam;
    private int goalsAwayTeam;

    private boolean isInSelectedSeasonAndRound() {
        return isInSelectedSeasonAndRound;
    }

    /* ************************************************************** */
    /* ***                      Aufgabe 2b                        *** */
    /* ************************************************************** */

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        if (qName.equalsIgnoreCase("Season")) {
            currentSeason = atts.getValue("value");
        } else if (qName.equalsIgnoreCase("Round")) {
            currentRound = Integer.parseInt(atts.getValue("number"));
            isInSelectedSeasonAndRound = currentSeason.equals(season) && currentRound == round;

        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("Match")) {
            dateTime = parse(atts.getValue("date"));
            location = atts.getValue("location");
        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("HomeTeam")) {
            homeTeam = "";
        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("AwayTeam")) {
            awayTeam = "";
        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("Result")) {
            charSequence = "";
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("HomeTeam")) {
            homeTeam = charSequence.trim();
        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("AwayTeam")) {
            awayTeam = charSequence.trim();

        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("Location")) {
            location = charSequence.trim();

        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("Result")) {
            if (charSequence.trim().equals("NA")) {
                goalsHomeTeam = -1;
                goalsAwayTeam = -1;
            } else {
                String[] goals = charSequence.trim().split(":");
                goalsHomeTeam = Integer.parseInt(goals[0].trim());
                goalsAwayTeam = Integer.parseInt(goals[1].trim());
            }
        } else if (isInSelectedSeasonAndRound && qName.equalsIgnoreCase("Match")) {
            matches.add(new Match(currentRound, dateTime, location, homeTeam, awayTeam, goalsHomeTeam, goalsAwayTeam, currentSeason));
        }
    }

    /* **************************************************** */
    /*              ab hier nichts mehr ändern              */
    /* **************************************************** */

    public Collection<Match> getMatches() {
        return this.matches;
    }

    public MatchContentHandler(String season, int round) {
        this.season = season;
        this.round = round;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        charSequence = new String(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
    }

    @Override
    public void endPrefixMapping(String arg0) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void processingInstruction(String arg0, String arg1) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDocumentLocator(Locator arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        // TODO Auto-generated method stub

    }

}
