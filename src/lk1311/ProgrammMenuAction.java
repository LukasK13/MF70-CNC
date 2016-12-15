package lk1311;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.table.*;

class Befehle implements MenuFunc {
	private Hauptfenster Hauptfenster;

	public Befehle(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(Hauptfenster.splitPaneGroß.getLeftComponent() == Hauptfenster.splitPaneKlein) {
			MenuButton[2].setBackground(Color.YELLOW);

			JPanel BefehlPanel = new JPanel();
			BefehlPanel.setBackground(Color.LIGHT_GRAY);
			BefehlPanel.setLayout(null);

			JScrollPane BefehlTableScroll = new JScrollPane();
			BefehlTableScroll.setBounds(0, 0, 1160, 720);
			BefehlPanel.add(BefehlTableScroll);

			JTable BefehlTable = new JTable();
			BefehlTable.setModel(new DefaultTableModel(
					new Object[][] {
							{"G00", "X, Y, Z", "Lineare Interpolation zu (X|Y) / (Z) im Eilgang"},
							{"G01", "X, Y, Z", "Lineare Interpolation zu (X|Y) / (Z) in normaler Geschwindigkeit"},
							{null, null, null},
							{"G02", "X, Y, I, J", "Kreisbogen vom (I|J) zu (X|Y) im Uhrzeigersinn"},
							{"G03", "X, Y, I, J", "Kreisbogen vom (I|J) zu (X|Y) im Gegenuhrzeigersinn"},
							{"G04", "I, J, W", "Kreisbogen um (I|J) mit dem Winkel R im Uhrzeigersinn"},
							{"G05", "I, J, W", "Kreisbogen um (I|J) mit dem Winkel R im Gegenuhrzeigersinn"},
							{null, null, null},
							{"G40", "-", "Keine Werkzeugbahnkorrektur"},
							{"G41", "-", "Werkzeugbahnkorrektur in Vorschubrichtung links"},
							{"G42", "-", "Werkzeugbahnkorrektur in Vorschubrichtung rechts"},
							{null, null, null},
							{"G72", "N, W", "Zyklus ab N bis Aufrufer mit W Wiederholungen"},
							{"", null, null},
							{"G90", "-", "Setzt die Bemaßung auf Absolutmaß"},
							{"G91", "-", "Setzt die Bemaßung auf Relativmaß"},
							{null, null, null},
							{"M00", "-", "Programmhalt"},
							{"M02", "-", "Programmende"},
							{"M03", "-", "Spindel ein"},
							{"M05", "-", "Spindel aus"},
							{null, null, null},
							{"F", "Vorschubgeschwindigkeit", "Setzt die Vorschubgeschwindigkeit"},
							{null, null, null},
							{"S", "Spindeldrehzahl", "Setzt die Spindeldrehzahl"},
							{null, null, null},
							{"T", "Werkzeugnummer", "Wählt das Werkzeug mit der angegbenen Nummer"},
					},
					new String[] {"Befehl", "Argumente", "Beschreibung"}
					) {
				boolean[] columnEditables = new boolean[] {false, false, false};

				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
			BefehlTable.setShowVerticalLines(true);
			BefehlTable.getColumnModel().getColumn(0).setPreferredWidth(75);
			BefehlTable.getColumnModel().getColumn(0).setMinWidth(25);
			BefehlTable.getColumnModel().getColumn(0).setMaxWidth(75);
			BefehlTable.getColumnModel().getColumn(1).setPreferredWidth(300);
			BefehlTable.getColumnModel().getColumn(1).setMinWidth(125);
			BefehlTable.getColumnModel().getColumn(1).setMaxWidth(300);
			BefehlTable.getColumnModel().getColumn(2).setPreferredWidth(350);
			BefehlTable.getColumnModel().getColumn(2).setMinWidth(300);
			BefehlTable.setRowHeight(25);
			BefehlTable.setFont(new Font("Tahoma", Font.PLAIN, 20));
			BefehlTable.setEditingColumn(0);
			BefehlTable.setEditingColumn(1);
			BefehlTable.setEditingColumn(2);

			BefehlTableScroll.setViewportView(BefehlTable);

			Hauptfenster.splitPaneGroß.setLeftComponent(BefehlPanel);
			Hauptfenster.splitPaneGroß.setDividerLocation(1160);
		} else {
			Hauptfenster.repaint();
			MenuButton[2].setBackground(Color.LIGHT_GRAY);
		}
	}
}


class Return implements MenuFunc {
	private Hauptfenster Hauptfenster;
	
	public Return(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.repaint();
	}
}


class DateiNeu implements MenuFunc {
	Hauptfenster Hauptfenster;

	public DateiNeu(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.DateiNeu();
	}
}


class DateiÖffnen implements MenuFunc {
	Hauptfenster Hauptfenster;

	public DateiÖffnen(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.DateiÖffnen();
	}
}


class DateiSpeichern implements MenuFunc {
	Hauptfenster Hauptfenster;

	public DateiSpeichern(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.DateiSpeichern(false);
	}
}


class DateiSpeichernUnter implements MenuFunc {
	Hauptfenster Hauptfenster;

	public DateiSpeichernUnter(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.DateiSpeichern(true);
	}
}

class DateiVerwalten implements MenuFunc {
	private Hauptfenster Hauptfenster;

	public DateiVerwalten(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(Hauptfenster.splitPaneGroß.getLeftComponent() == Hauptfenster.splitPaneKlein) {
			MenuButton[5].setBackground(Color.YELLOW);
			
			FileManager fileManager = new FileManager();
			Hauptfenster.splitPaneGroß.setLeftComponent(fileManager.getGui());
			Hauptfenster.splitPaneGroß.setDividerLocation(1160);
			fileManager.showRootFile();
		} else {
			Hauptfenster.repaint();
			MenuButton[5].setBackground(Color.LIGHT_GRAY);
		}
	}
}


class Kopieren implements MenuFunc {
	Hauptfenster Hauptfenster;

	public Kopieren(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.Kopieren();
	}
}


class Einfügen implements MenuFunc {
	Hauptfenster Hauptfenster;

	public Einfügen(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.Einfügen();
	}
}


class Rückgängig implements MenuFunc {
	Hauptfenster Hauptfenster;

	public Rückgängig(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.Rückgängig();
	}
}


class Schriftgröße implements MenuFunc {
	Hauptfenster Hauptfenster;

	public Schriftgröße(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.Schriftgröße();
	}
}


class GeheZuZeile implements MenuFunc {
	Hauptfenster Hauptfenster;

	public GeheZuZeile(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.GeheZuZeile();
	}
}

class Suchen implements MenuFunc {
	Hauptfenster Hauptfenster;

	public Suchen(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.Suchen();
	}
}

class ProgrammPrüfen implements MenuFunc {
	Hauptfenster Hauptfenster;

	public ProgrammPrüfen(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Editor.Prüfen();
	}
}


class SimulationMenuAnzeigen implements MenuFunc {
	Hauptfenster Hauptfenster;

	public SimulationMenuAnzeigen(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}
	
	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Simulator.anzeigen();
	}
}


class SimulationStarten implements MenuFunc {
	Hauptfenster Hauptfenster;

	public SimulationStarten(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {		
		if(MenuButton[1].getText().equals("<html>Simulation<br>Starten</html>")) {
			Hauptfenster.Programm.Simulator.KomponentenZeichnen();
			Hauptfenster.Programm.Simulator.SimulationStarten(true);
			MenuButton[1].setText("<html>Simulation<br>Stoppen</html>");
		} else if(MenuButton[1].getText().equals("<html>Simulation<br>Stoppen</html>")) {
			Hauptfenster.Programm.Simulator.SimulatorEngine.Pause = true;
			MenuButton[1].setText("<html>Simulation<br>Fortsetzen</html>");
		} else if(MenuButton[1].getText().equals("<html>Simulation<br>Fortsetzen</html>")) {
			Hauptfenster.Programm.Simulator.SimulatorEngine.Pause = false;
			MenuButton[1].setText("<html>Simulation<br>Stoppen</html>");
		}		
	}
}


class Werkstück implements MenuFunc {
	Hauptfenster Hauptfenster;

	public Werkstück(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Programm.Simulator.Werkstück();
	}
}

