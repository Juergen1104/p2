package p2.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import static p2.general.Parameters.dtfDay;

public class FilterDatabase extends Database {

    private static String quote(String s) {
        return "'" + s + "'";
    }

    /* ************************************************************** */
    /* ***                      Aufgabe 1c                        *** */
    /* ************************************************************** */

    // 2
    public Collection<String> getAllClubs() {
        List<String> clubs = new ArrayList<>();

        String sql = "SELECT DISTINCT home_team FROM match UNION SELECT DISTINCT away_team FROM match";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            {
                while (resultSet.next()) {
                    clubs.add(resultSet.getString("home_team"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return clubs;
    }

    // 2
    public Collection<String> getAllSeasons() {
        Set<String> seasons = new TreeSet<>();
        try {
            String sql = "SELECT DISTINCT season FROM match_day";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            {
                while (resultSet.next()) {
                    seasons.add("Season " + resultSet.getString("season"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return seasons;
    }

    // 2
    public Collection<Integer> getPastRoundsInSeason(String season) {
        Set<Integer> rounds = new TreeSet<>();
        LocalDate currentDate = LocalDate.now();
        String sql = "SELECT round FROM match_day WHERE season = '" + season + "' AND date <= '" + currentDate.format(dtfDay) + "'";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            {
                while (resultSet.next()) {
                    rounds.add(resultSet.getInt("round"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rounds;
    }

    /* ************************************************************** */
    /* ***                      Aufgabe 1d                        *** */
    /* ************************************************************** */

    // 4
    public String getMatchHistory(String club1, String club2) {
        StringBuffer sBuf = new StringBuffer("Matches:\n" + club1 + "  -  " + club2 + ":\n\n");
        ResultSet resultSet = null;
        int winsClub1 = 0;
        int winsClub2 = 0;
        int ties = 0;

        String sql = "SELECT m_day_time, home_team, away_team, goals_home, goals_away " +
                "FROM match " +
                "WHERE (home_team = ? AND away_team = ?) OR (home_team = ? AND away_team = ?) " +
                "ORDER BY m_day_time";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, club1);
            statement.setString(2, club2);
            statement.setString(3, club2);
            statement.setString(4, club1);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString("m_day_time");
                String homeTeam = resultSet.getString("home_team");
                String awayTeam = resultSet.getString("away_team");
                int goalsHome = resultSet.getInt("goals_home");
                int goalsAway = resultSet.getInt("goals_away");

                sBuf.append(date).append(": ")
                        .append(homeTeam).append(" ")
                        .append(goalsHome).append(" : ").append(goalsAway).append(" ")
                        .append(awayTeam).append("\n");

                if (goalsHome > goalsAway) {
                    if (homeTeam.equals(club1)) {
                        winsClub1++;
                    } else {
                        winsClub2++;
                    }
                } else if (goalsHome < goalsAway) {
                    if (awayTeam.equals(club1)) {
                        winsClub1++;
                    } else {
                        winsClub2++;
                    }
                } else {
                    ties++;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        sBuf.append("\nSummary:\n")
                .append("Wins ").append(club1).append(": ").append(winsClub1).append("\n")
                .append("Wins ").append(club2).append(": ").append(winsClub2).append("\n")
                .append("Tie: ").append(ties);

        return sBuf.toString();
    }

    /* ************************************************************** */
    /* ***                      Aufgabe 1e                        *** */
    /* ************************************************************** */

    private class ClubResult {
        String club;
        int points = 0;                  // Punkte (3 je Sieg und 1 je Unentschieden)
        int goalsShot = 0;               // Tore, die die Mannschaft geschossen hat
        int goalsReceived = 0;           // Tore, die die Mannschaft "kassiert" hat

        public ClubResult(String club) {
            this.club = club;
        }

        public int getPoints() {
            return points;
        }

        public int getGoalDifference() {
            return goalsShot - goalsReceived;
        }
    }

    // 6
    public String getResultTable(String season, int round) {
        StringBuffer sBuf = new StringBuffer("Result Table:  Season " + season +
                ", round " + round + "\n\n");

        HashMap<String, ClubResult> rTable = new HashMap<>(); // Resultate für die einzelnen Vereine

        String sql = "SELECT m.home_team, m.away_team, m.goals_home, m.goals_away " +
                "FROM match m JOIN match_day md ON m.m_day_time LIKE md.date || '%' " +
                "WHERE md.season = ? AND md.round <= ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, season);
            statement.setInt(2, round);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String homeTeam = resultSet.getString("home_team");
                String awayTeam = resultSet.getString("away_team");
                int goalsHome = resultSet.getInt("goals_home");
                int goalsAway = resultSet.getInt("goals_away");

                processResult(rTable, homeTeam, goalsHome, goalsAway);
                processResult(rTable, awayTeam, goalsAway, goalsHome);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        rTable.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(ClubResult::getPoints)
                        .thenComparingInt(ClubResult::getGoalDifference).reversed()))
                .forEach(e -> sBuf.append(e.getValue().club).append("  ").append(e.getValue().points).append("  ")
                        .append(e.getValue().goalsShot).append(":").append(e.getValue().goalsReceived).append("\n"));
        return sBuf.toString();
    }

    private void processResult(Map<String, ClubResult> resultTable, String club, int goalsScored, int goalsConceded) {
        ClubResult clubResult = resultTable.getOrDefault(club, new ClubResult(club));

        clubResult.goalsShot = clubResult.goalsShot + goalsScored;
        clubResult.goalsReceived = clubResult.goalsReceived + goalsConceded;

        if (goalsScored > goalsConceded) {
            clubResult.points = clubResult.points + 3;
        } else if (goalsScored == goalsConceded) {
            clubResult.points = clubResult.points + 1;
        }
        resultTable.put(club, clubResult);
    }

    /* ************************************************************** */

    public void disconnect() {
        super.disconnect();
    }
}


