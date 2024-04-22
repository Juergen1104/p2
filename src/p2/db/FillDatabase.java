package p2.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p2.data.Match;
import p2.general.Parameters;
import p2.io.CSVReaderMatches;

import static p2.general.Parameters.dtfDay;
import static p2.general.Parameters.dtfDayTime;

public class FillDatabase extends Database {
    private static final Logger log = LoggerFactory.getLogger(FillDatabase.class);
    private Collection<Match> matches;

    private PreparedStatement p1, p2;

    /* ************************************************************** */
    /* ***                      Aufgabe 1b                        *** */
    /* ************************************************************** */

    // 2P
    private void prepareStatements() {

        try {
            p1 = connection.prepareStatement("INSERT INTO match_day (season, round, d," +
                    "ate) VALUES (?, ?, ?)");
            p2 = connection.prepareStatement("INSERT INTO match (location, home_team, away_team, goals_home, goals_away, m_day_time) VALUES (?, ?, ?, ?, ?, ?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 2
    private void fillMatchDayTable() {
        clearTable("match_day");    // alten Inhalt der Tabelle ggf. löschen
        Set<String> matchDate = new HashSet<String>();
        for (Match match : matches) {
            if (!matchDate.contains(match.getDate().format(dtfDay))) {
                try {
                    p1.setString(1, match.getSeason());
                    p1.setInt(2, match.getRoundNumber());
                    p1.setString(3, match.getDate().format(dtfDay));
                    p1.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                matchDate.add(match.getDate().format(dtfDay));
            }
        }
    }

    // 2
    private void fillMatchTable() {
        clearTable("match");                         // alten Inhalt der Tabelle ggf. löschen
        for (Match match : matches) {
            try {
                p2.setString(1, match.getLocation());
                p2.setString(2, match.getHomeTeam());
                p2.setString(3, match.getAwayTeam());
                p2.setInt(4, match.getGoalsHomeTeam() == -1 ? 0 : match.getGoalsHomeTeam());
                p2.setInt(5, match.getGoalsAwayTeam() == -1 ? 0 : match.getGoalsAwayTeam());
                p2.setString(6, match.getDate().format(dtfDayTime));
                p2.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        int a = 3;
    }

    /* Creates Prepared Statements, fills both tables and commits changes */
    private void fillTables() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println(e);
        }

        prepareStatements();
        fillMatchTable();
        fillMatchDayTable();

        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeStatements();
    }

    /* Create Collection of Match objects from lines in CSV files */
    public void loadData() {
        CSVReaderMatches csvrm = new CSVReaderMatches();

        for (String filename : Parameters.seasonFiles) {
            csvrm.parseFile(filename);
        }
        this.matches = csvrm.getMatches();
    }

    /* Methode entfernt alle Zeilen/Einträge aus der Tabelle name */
    private void clearTable(String name) {
        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE FROM " + name + ';';
            statement.execute(sql);
        } catch (SQLException throwables) {
            System.out.println(throwables);
        }
    }

    /* schließt die Prepared Statements p1 und p2, falls nötig/möglich */
    private void closeStatements() {
        try {
            if ((p1 != null) && (!p1.isClosed())) {
                p1.close();
            }
            if ((p2 != null) && (!p2.isClosed())) {
                p2.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FillDatabase fdb = new FillDatabase();
        fdb.loadData();     // Daten aus CSV einlesen
        fdb.fillTables();   // Tabellen füllen (und Änderungen committen)
        fdb.disconnect();
    }

}
