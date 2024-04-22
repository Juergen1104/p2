package p2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import p2.general.Parameters;

public abstract class Database {
	protected Connection connection;
	protected Properties properties;
	
	public Database() {
	    this.connect();
	    properties = new Properties();
	    properties.setProperty("PRAGMA foreign_keys", "ON");
	  }
	

	private void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection =
					DriverManager.getConnection("jdbc:sqlite:" + Parameters.dbFile);
			System.out.println("Connected to DB");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	protected void disconnect() {
		try {
			this.connection.close();
			System.out.println("Disconnected from DB");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
