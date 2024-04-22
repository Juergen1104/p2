package p2.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import p2.data.Match;
import p2.db.FilterDatabase;
import p2.db.UpdateDatabase;
import p2.general.Parameters;
import p2.io.CSVReaderMatches;
import p2.xml.XMLMatchesReader;
import p2.xml.XMLResultWriter;
import p2.xml.XMLMatchesWriter;

public class P2_GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private static int w = 600, h = 400;
	private static DateTimeFormatter dtfDay = DateTimeFormatter.ofPattern("dd. MM. yyyy");
	private static DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
	private FilterDatabase fdb = new FilterDatabase();
	private UpdateDatabase udb = new UpdateDatabase();
	private XMLResultWriter xmlRW = new XMLResultWriter(Parameters.xmlFile);
	private JComboBox<String> clubs1 = new JComboBox<>(), clubs2 = new JComboBox<>();
	private JComboBox<String> seasons = new JComboBox<>();
	private JComboBox<Integer> rounds = new JComboBox<>();
	private JComboBox<String> seasons2 = new JComboBox<>();
	private JComboBox<Integer> rounds2 = new JComboBox<>();
	private JComboBox<Integer> rounds3 = new JComboBox<>();
	private JTextField[] resultFields;
	private JTextArea a1,a2,a3,a4;
	private JPanel inputPanel;

	public P2_GUI() {
		super.setTitle("Results Bundesliga");
		this.addWindowListener((WindowListener) new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if (fdb != null){
					fdb.disconnect();
					udb.disconnect();
				}
				if (xmlRW != null) {
					xmlRW.saveDoc();
				}
				System.exit(0);
			}
		});
				
		createComponents();
		this.getContentPane().setPreferredSize(new Dimension(w,h));
		this.pack();
	}
	
	
	private void createComponents() {
		Container c = this.getContentPane();
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));	
		
		JPanel p1 = new JPanel();
		fillP1(p1);
		
		JPanel p2 = new JPanel();
		fillP2(p2);
		
		JPanel p3 = new JPanel();
		fillP3(p3);
		
		JPanel p4 = new JPanel();
		fillP4(p4);
		
		JPanel p5 = new JPanel();
		fillP5(p5);
		
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		tabPane.add("Match results", p1);
		tabPane.add("Result Table", p2);
		tabPane.add("Write XML File", p3);
		tabPane.add("Results Match Day", p4);
		tabPane.add("Insert Results", p5);
		c.add(tabPane);	
		
	}
	
	
	private void fillP1(JPanel p) {
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
		top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		Collection<String> clubNames = fdb.getAllClubs();
		if ((clubNames != null) && (clubNames.size() > 0)) {
			fillComboBox(this.clubs1,clubNames);
			this.clubs1.setSelectedIndex(0);
			fillComboBox(this.clubs2,clubNames);
			selectLastElementInCombobox(this.clubs2);
		}
		
		this.a1 = new JTextArea(25,10);

		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(
				e -> {
					String club1 = (String)this.clubs1.getSelectedItem();
					String club2 = (String)this.clubs2.getSelectedItem();
					if (club1 != null && club2 != null && !club1.equals(club2)) {
						this.a1.setText(fdb.getMatchHistory(club1,club2));
						this.a1.setCaretPosition(0);
					} else {
						JOptionPane.showMessageDialog(P2_GUI.this, "You have to select two different clubs");
					}
				}
				);
		

		top.add(this.clubs1);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(this.clubs2);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(submitButton);
		top.setPreferredSize(new Dimension(w,50));
		
		JScrollPane scroller = new JScrollPane(this.a1);
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		
		p.add(top);
		p.add(scroller);
	}
	
	private void fillP2(JPanel p) {
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
		top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		Collection<String> seasons = fdb.getAllSeasons();
//		List<String> entries = 
//				seasons.stream().map(x -> x.substring(0, x.length() - 2) + "/" + x.substring(x.length()-2))
//				                .collect(Collectors.toList());
		fillComboBox(this.seasons,seasons);
		selectLastElementInCombobox(this.seasons);
		
		String selS  = (String)this.seasons.getSelectedItem();
		if (selS != null) {
			selS = selS.substring(selS.length()-5);
			Collection<Integer> rValues = fdb.getPastRoundsInSeason(selS);
			fillComboBox(this.rounds, rValues);
			selectLastElementInCombobox(this.rounds);
		}
				
		this.a2 = new JTextArea(25,10);
		
		ActionListener al = e -> {
			String selSeason  = (String)this.seasons.getSelectedItem();
			//String season = selSeason.substring(selSeason.length()-5).replace("/", "");	
			String season = selSeason.substring(selSeason.length()-5);
			int round = (Integer) this.rounds.getSelectedItem();
			this.a2.setText(fdb.getResultTable(season,round));
			this.a2.setCaretPosition(0);

		};

		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(al);
		
		ActionListener al2 = e -> {
			String selSeason  = (String)this.seasons.getSelectedItem();
			selSeason = selSeason.substring(selSeason.length()-5);
			Collection<Integer> roundVals = fdb.getPastRoundsInSeason(selSeason);
			fillComboBox(this.rounds, roundVals);
			selectLastElementInCombobox(this.rounds);
		};
		
		this.seasons.addActionListener(al2);

		top.add(this.seasons);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(this.rounds);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(submitButton);
		top.setPreferredSize(new Dimension(w,50));
		
		JScrollPane scroller = new JScrollPane(this.a2);
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		
		p.add(top);
		p.add(scroller);
	}
	
	private void fillP3(JPanel p) {
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
		top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		this.a3 = new JTextArea(25,10);
		JButton writeXMLButton = new JButton("Write XML File");
		writeXMLButton.addActionListener(
				e -> {	
					CSVReaderMatches csvrm = new CSVReaderMatches();				
					for (String filename : Parameters.seasonFiles) {
						csvrm.parseFile(filename);
					}		
					Collection<Match> matches = csvrm.getMatches();
					XMLMatchesWriter xmlw = new XMLMatchesWriter();
					xmlw.createDoc();
					xmlw.addMatches(matches);
					xmlw.writeDoc();
					
					StringBuffer sBuf = new StringBuffer();
					try (BufferedReader bufR = new BufferedReader(new FileReader(Parameters.xmlFile))){
						bufR
						.lines()                              
						.forEach(s -> sBuf.append(s + "\n"));
					}
					catch (Exception ioe){
						System.out.println(ioe);
					}
					
					this.a3.setText(sBuf.toString());
					this.a3.setCaretPosition(0);

				});
		

		top.add(writeXMLButton);
		top.setPreferredSize(new Dimension(w,50));
		
		JScrollPane scroller = new JScrollPane(this.a3);
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		
		p.add(top);
		p.add(scroller);
	}
	
	private void fillP4(JPanel p) {
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
		top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		this.seasons2.addItem("18/19");
		this.seasons2.addItem("19/20");
		this.seasons2.addItem("20/21");
		this.seasons2.addItem("21/22");
		this.seasons2.addItem("22/23");
		this.seasons2.addItem("23/24");
		selectLastElementInCombobox(this.seasons2);

		for (int i=1;i<=34;i++) {
			this.rounds2.addItem(i);
		}
		this.rounds2.setSelectedIndex(0);
		
		this.a4 = new JTextArea(25,10);
		
		//top.add(writeXMLButton);
		top.setPreferredSize(new Dimension(w,50));
		
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(
				e -> {
					String selSeason = (String)this.seasons2.getSelectedItem();
					int selRound = (Integer)this.rounds2.getSelectedItem();
					XMLMatchesReader xmlReader = new XMLMatchesReader(Parameters.xmlFile,selSeason, selRound);
					xmlReader.parseFile();
					this.a4.setText(xmlReader.getResults());
					this.a4.setCaretPosition(0);

				});
		
		top.add(this.seasons2);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(this.rounds2);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(submitButton);
		top.setPreferredSize(new Dimension(w,50));
		
		JScrollPane scroller = new JScrollPane(this.a4);
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		
		p.add(top);
		p.add(scroller);
	}

	private void fillP5(JPanel p) {
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
		top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel currSeasonLabel = new JLabel("Season 23/24");
		JLabel roundLabel = new JLabel("Round: ");
		for (int i=1;i<=34;i++) {
			this.rounds3.addItem(i);
		}
		this.rounds3.setSelectedIndex(25);
		
		this.inputPanel = new JPanel();
		this.inputPanel.setLayout(new BoxLayout(this.inputPanel,BoxLayout.Y_AXIS));
		this.inputPanel.setPreferredSize(new Dimension(w-50,h-70));
		JScrollPane scroller = new JScrollPane(this.inputPanel);
		
		JButton submitButton = new JButton("Show matches");
		submitButton.addActionListener(
				e -> {
					int selRound = (Integer)this.rounds3.getSelectedItem();
					//Collection<Match> matchesOfRound = udb.getMatchesOfRound("23/24", selRound);;
					
					XMLMatchesReader xmlReader = new XMLMatchesReader(Parameters.xmlFile,"23/24", selRound);
					xmlReader.parseFile();
					Collection<Match> matchesOfRound = xmlReader.getMatches();
					
					P2_GUI.this.inputPanel.removeAll();
					P2_GUI.this.inputPanel.setLayout(new GridLayout(matchesOfRound.size()*4,3,10,0));
					P2_GUI.this.inputPanel.setPreferredSize(new Dimension(w-50,matchesOfRound.size()*100));

					P2_GUI.this.resultFields = new JTextField[2*matchesOfRound.size()];
					int i=0;
					for (Match m : matchesOfRound) {
						JLabel dateLabel = new JLabel(dtfDay.format(m.getDate()));
						JLabel timeLabel = new JLabel(dtfTime.format(m.getDate()));
						P2_GUI.this.inputPanel.add(dateLabel);
						P2_GUI.this.inputPanel.add(timeLabel);
						P2_GUI.this.inputPanel.add(new JLabel(""));
						
						JLabel club1Label = new JLabel(m.getHomeTeam());
						JLabel club2Label = new JLabel(m.getAwayTeam());
						P2_GUI.this.inputPanel.add(club1Label);
						P2_GUI.this.inputPanel.add(club2Label);
						P2_GUI.this.inputPanel.add(new JLabel(""));

						P2_GUI.this.resultFields[2*i] = new JTextField("" + m.getGoalsHomeTeam());
						P2_GUI.this.resultFields[2*i+1] = new JTextField("" + m.getGoalsAwayTeam());
						P2_GUI.this.inputPanel.add(P2_GUI.this.resultFields[2*i]);
						P2_GUI.this.inputPanel.add(P2_GUI.this.resultFields[2*i+1]);
						JButton submit = new JButton("Submit");
						submit.addActionListener(new InsertResultListener(m, 2*i, 2*i+1));
						P2_GUI.this.inputPanel.add(submit);
						
						for (int k=1;k<=3;k++) {
							P2_GUI.this.inputPanel.add(new JLabel(""));
						}

						i++;
					}
					P2_GUI.this.revalidate();
				});
		
		top.add(currSeasonLabel);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(roundLabel);
		top.add(this.rounds3);
		top.add(Box.createRigidArea(new Dimension(10,0)));
		top.add(submitButton);
		top.setPreferredSize(new Dimension(w,50));
		
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));		
		p.add(top);
		p.add(scroller);
	}
	
	private class InsertResultListener implements ActionListener {
		private Match m;
		private JTextField t1, t2;
		
		public InsertResultListener(Match m, int i, int j) {
			this.m = m;
			this.t1 = P2_GUI.this.resultFields[i];
			this.t2 = P2_GUI.this.resultFields[j];
		}
		
		public void actionPerformed(ActionEvent e) {
			try {
				int goalsHome = Integer.parseInt(t1.getText().trim());
				int goalsAway = Integer.parseInt(t2.getText().trim());
				P2_GUI.this.udb.insertMatchResult(m,goalsHome,goalsAway);
				P2_GUI.this.xmlRW.loadXMLFileIfNotDoneBefore();
				P2_GUI.this.xmlRW.writeResult(m, goalsHome, goalsAway);
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(P2_GUI.this, "Not a number");
			}
		}
	}
	
	
	private static <T> void fillComboBox(JComboBox<T> cb, Collection<T> elemente){
		Vector<T> items = new Vector<>(elemente);
		if (items != null){
			cb.removeAllItems();
			items
			.stream()
			.forEach(item -> cb.addItem(item));
			if (cb.getItemCount() > 0){
				cb.setSelectedIndex(0);
			}
		}
	}
	
	private static void selectLastElementInCombobox(JComboBox<?> cb) {
		int n = cb.getItemCount();
		cb.setSelectedIndex(n-1);
	}
	
	public static void main(String[] args) {
		P2_GUI gui = new P2_GUI();
		gui.setVisible(true);
	}



}
