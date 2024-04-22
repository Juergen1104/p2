package p2.db;

import p2.data.Match;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static p2.general.Parameters.dtfDayTime;

public class UpdateDatabase extends Database {

    private PreparedStatement p1, p2;

    private static String quote(String s) {
        return "'" + s + "'";
    }

    /* ************************************************************** */
    /* ***                      Aufgabe 3a                        *** */
    /* ************************************************************** */

    // 4P
    public void insertMatchResult(Match m, int goalsHome, int goalsAway) {

        String sql = "UPDATE match SET GOALS_HOME = ?, GOALS_AWAY = ?" +
                " WHERE HOME_TEAM = ?" +
                " AND AWAY_TEAM =?" +
                " AND M_DAY_TIME = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, goalsHome);
            statement.setInt(2, goalsAway);
            statement.setString(3, m.getHomeTeam());
            statement.setString(4, m.getAwayTeam());
            statement.setString(5, m.getDate().format(dtfDayTime));
            boolean numUpd = statement.execute();
            statement.close();
            connection.close();
            //todo kein Update in der Datenbank, aber warum?

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        super.disconnect();
    }
}



