package lk1311;

import java.awt.*;
import javax.swing.*;

public class Programm {

	private Hauptfenster Hauptfenster;
	private JPanel PanelButton_Programm;
	public CNCEditor Editor;
	public Simulator Simulator;
	public CreateMenu ProgrammMenu; 

	public Programm(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
		Initialisieren_Komponenten_Programm();
	}

	private void Initialisieren_Komponenten_Programm() {
		PanelButton_Programm = new JPanel();
		PanelButton_Programm.setBackground(Color.LIGHT_GRAY);
		PanelButton_Programm.setLayout(null);
		
		ProgrammMenu = new CreateMenu(PanelButton_Programm, Hauptfenster);
		
		Menu ProgrammMenuMain = new Menu(null, null, null);
		ProgrammMenuMain.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		ProgrammMenuMain.Items[1] = new MenuItem("Datei", null, null);
		ProgrammMenuMain.Items[2] = new MenuItem("Bearbeiten", null, null);
		ProgrammMenuMain.Items[3] = new MenuItem("Einfügen", null, null);
		ProgrammMenuMain.Items[4] = new MenuItem("Ansicht", null, null);
		ProgrammMenuMain.Items[5] = new MenuItem("Simulation", null, null);
		ProgrammMenuMain.Items[6] = new MenuItem("Optionen", null, null);
		ProgrammMenuMain.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);			
		
		Menu ProgrammMenuDatei = new Menu(null, null, ProgrammMenuMain, null, new Return(Hauptfenster));
		ProgrammMenuDatei.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		ProgrammMenuDatei.Items[1] = new MenuItem("Neu", null, new DateiNeu(Hauptfenster));
		ProgrammMenuDatei.Items[2] = new MenuItem("Öffnen", null, new DateiÖffnen(Hauptfenster));
		ProgrammMenuDatei.Items[3] = new MenuItem("Speichern", null, new DateiSpeichern(Hauptfenster));
		ProgrammMenuDatei.Items[4] = new MenuItem("<html>Speichern<br>Unter</html>", null, new DateiSpeichernUnter(Hauptfenster));
		ProgrammMenuDatei.Items[5] = new MenuItem("<html>Dateien<br>Verwalten</html>", null, new DateiVerwalten(Hauptfenster));
		ProgrammMenuDatei.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		ProgrammMenuDatei.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		ProgrammMenuMain.MenuNext[1] = ProgrammMenuDatei;
		
		Menu ProgrammMenuBearbeiten = new Menu(null, null, ProgrammMenuMain);
		ProgrammMenuBearbeiten.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		ProgrammMenuBearbeiten.Items[1] = new MenuItem("Rückgängig", null, new Rückgängig(Hauptfenster));
		ProgrammMenuBearbeiten.Items[2] = new MenuItem("Kopieren", null, new Kopieren(Hauptfenster));
		ProgrammMenuBearbeiten.Items[3] = new MenuItem("Einfügen", null, new Einfügen(Hauptfenster));
		ProgrammMenuBearbeiten.Items[4] = new MenuItem("", null, null);
		ProgrammMenuBearbeiten.Items[5] = new MenuItem("", null, null);
		ProgrammMenuBearbeiten.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		ProgrammMenuBearbeiten.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		ProgrammMenuMain.MenuNext[2] = ProgrammMenuBearbeiten;
		
		Menu ProgrammMenuEinfügen = new Menu(null, null, ProgrammMenuMain);
		ProgrammMenuEinfügen.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		ProgrammMenuEinfügen.Items[1] = new MenuItem("", null, null);
		ProgrammMenuEinfügen.Items[2] = new MenuItem("", null, null);
		ProgrammMenuEinfügen.Items[3] = new MenuItem("", null, null);
		ProgrammMenuEinfügen.Items[4] = new MenuItem("", null, null);
		ProgrammMenuEinfügen.Items[5] = new MenuItem("", null, null);
		ProgrammMenuEinfügen.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		ProgrammMenuEinfügen.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		ProgrammMenuMain.MenuNext[3] = ProgrammMenuEinfügen;
	
		Menu ProgrammMenuAnsicht = new Menu(null, null, ProgrammMenuMain);
		ProgrammMenuAnsicht.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		ProgrammMenuAnsicht.Items[1] = new MenuItem("<html>Gehe zu<br>Zeile</html>", null, new GeheZuZeile(Hauptfenster));
		ProgrammMenuAnsicht.Items[2] = new MenuItem("Suchen", null, new Suchen(Hauptfenster));
		ProgrammMenuAnsicht.Items[3] = new MenuItem("<html>Schrift<br>Größe</html>", null, new Schriftgröße(Hauptfenster));
		ProgrammMenuAnsicht.Items[4] = new MenuItem("", null, null);
		ProgrammMenuAnsicht.Items[5] = new MenuItem("", null, null);
		ProgrammMenuAnsicht.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		ProgrammMenuAnsicht.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		ProgrammMenuMain.MenuNext[4] = ProgrammMenuAnsicht;
		
		Menu ProgrammMenuSimulation = new Menu(null, null, ProgrammMenuMain, new SimulationMenuAnzeigen(Hauptfenster), new Return(Hauptfenster));
		ProgrammMenuSimulation.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		ProgrammMenuSimulation.Items[1] = new MenuItem("<html>Simulation<br>Starten</html>", null, new SimulationStarten(Hauptfenster));
		ProgrammMenuSimulation.Items[2] = new MenuItem("<html>Werkstück<br>Dimensionen</html>", null, new Werkstück(Hauptfenster));
		ProgrammMenuSimulation.Items[3] = new MenuItem("", null, null);
		ProgrammMenuSimulation.Items[4] = new MenuItem("", null, null);
		ProgrammMenuSimulation.Items[5] = new MenuItem("", null, null);
		ProgrammMenuSimulation.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		ProgrammMenuSimulation.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		ProgrammMenuMain.MenuNext[5] = ProgrammMenuSimulation;
		
		Menu ProgrammMenuOptionen = new Menu(null, null, ProgrammMenuMain, null, new Return(Hauptfenster));
		ProgrammMenuOptionen.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		ProgrammMenuOptionen.Items[1] = new MenuItem("<html>Programm<br>Prüfen</html>", null, new ProgrammPrüfen(Hauptfenster));
		ProgrammMenuOptionen.Items[2] = new MenuItem("Befehle", null, new Befehle(Hauptfenster));
		ProgrammMenuOptionen.Items[3] = new MenuItem("", null, null);
		ProgrammMenuOptionen.Items[4] = new MenuItem("", null, null);
		ProgrammMenuOptionen.Items[5] = new MenuItem("", null, null);
		ProgrammMenuOptionen.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		ProgrammMenuOptionen.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		ProgrammMenuMain.MenuNext[6] = ProgrammMenuOptionen;
		
		ProgrammMenu.ButtonMenuArray.setButtonMenu(ProgrammMenuMain);	

		//-----PanelSichtfeld_Hand-----	

		Editor = new CNCEditor(Hauptfenster);
		Simulator = new Simulator(Hauptfenster);
	}

	public void setVisible(boolean visible) {
		if(visible) {
			Hauptfenster.setContentPane(Editor.EditorPanel, PanelButton_Programm);
		}
	}
}

