package p2.data;

import java.io.Serializable;
import java.time.LocalDateTime;

import p2.general.Parameters;

public class Match implements Serializable{
	private static final long serialVersionUID = 1L;
	private int roundNumber;           // Spieltag (1,2,3,...)
	private LocalDateTime date;        // datum und Uhrzeit
	private String location;           // Spielort
	private String homeTeam;           // Name der Heimmannschaft
	private String awayTeam;           // Name der Gastmannschaft
	private int goalsHomeTeam;         // Anzahl Tore der Heimmannschaft
	private int goalsAwayTeam;         // Anzahl Tore der Gastmannschaft
	private String season;             // Saison (18/19, 19/29,...23/34)
	
	/* ***  Constructor using all instance variables  *** */
	public Match(int roundNumber, LocalDateTime date, String location, String homeTeam, String awayTeam,
			int goalsHomeTeam, int goalsAwayTeam, String season) {
		this.roundNumber = roundNumber;
		this.date = date;
		this.location = location;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.goalsHomeTeam = goalsHomeTeam;
		this.goalsAwayTeam = goalsAwayTeam;
		this.season = season;
	}
	
	
	/* ***  Getter methods  *** */


	public int getRoundNumber() {
		return roundNumber;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getLocation() {
		return location;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public int getGoalsHomeTeam() {
		return goalsHomeTeam;
	}

	public int getGoalsAwayTeam() {
		return goalsAwayTeam;
	}

	public String getSeason() {
		return season;
	}
	
	public String toString() {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append(Parameters.dtfDayTime.format(this.date) + " " + this.location + "\n");
		sBuf.append(this.homeTeam + " : " + this.awayTeam + "\n");
		if ((this.goalsHomeTeam != -1) && (this.goalsAwayTeam != -1)) {
			sBuf.append(this.goalsHomeTeam +  " : " + this.goalsAwayTeam + "\n");
		}
		else {
			sBuf.append("  -  \n");
		}
		
		return sBuf.toString();
	}
	
}
