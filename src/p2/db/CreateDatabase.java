package p2.db;

import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase extends Database {

	/* ************************************************************** */
	/* ***                      Aufgabe 1a                        *** */
	/* ************************************************************** */


	// 2P
	private void createMatchTable() {
	    dropTable("match");  // ggf. alte Tabelle löschen

        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS match (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "location Text NOT NULL," +
                    "home_team TEXT NOT NULL," +
                    "away_team TEXT NOT NULL," +
                    "goals_home INTEGER NOT NULL," +
                    "goals_away INTEGER NOT NULL," +
                    "m_day_time TEXT NOT NULL)";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
		}
	  }

	// 2P
	private void createMatchDayTable() {
	    dropTable("match_day");  // ggf. alte Tabelle löschen

		try (Statement statement = connection.createStatement()) {
			String sql = "CREATE TABLE IF NOT EXISTS match_day (" +
					"season TEXT NOT NULL," +
					"round INTEGER NOT NULL," +
					"date TEXT PRIMARY KEY)";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/* Tabelle mit dem Namen name aus der Datenbank löschen */
	private void dropTable(String name) {
	    try (Statement statement = connection.createStatement()) {
	      String sql = "DROP TABLE IF EXISTS " + name;
	      statement.executeUpdate(sql);
	    } catch (SQLException e) {
	      e.printStackTrace();
	    }
	  }

	private void createTables() {
		createMatchTable();
		createMatchDayTable();
	}

	public static void main(String[] args) {
		CreateDatabase db = new CreateDatabase();
		db.createTables();
		db.disconnect();// TODO Auto-generated method stub
	}

}
