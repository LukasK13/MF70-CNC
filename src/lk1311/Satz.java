package lk1311;

import java.awt.*;
import javax.swing.*;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Satz {

	private Hauptfenster Hauptfenster;
	private JPanel PanelButton_Satz;
	public SatzFräsThread SatzFräsThread;
	private GpioPinListenerDigital NotausListener;
	public CNCEditor Editor;
	public CreateMenu SatzMenu; 

	public Satz(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
		Initialisieren_Komponenten_Satz();
	}

	private void Initialisieren_Komponenten_Satz() {
		PanelButton_Satz = new JPanel();
		PanelButton_Satz.setBackground(Color.LIGHT_GRAY);
		PanelButton_Satz.setLayout(null);
		
		SatzMenu = new CreateMenu(PanelButton_Satz, Hauptfenster);
		
		Menu SatzMenuMain = new Menu(null, null, null);
		SatzMenuMain.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		SatzMenuMain.Items[1] = new MenuItem("Datei", null, null);
		SatzMenuMain.Items[2] = new MenuItem("Bearbeiten", null, null);
		SatzMenuMain.Items[3] = new MenuItem("Ansicht", null, null);
		SatzMenuMain.Items[4] = new MenuItem("Start", null, null);
		SatzMenuMain.Items[5] = new MenuItem("Optionen", null, null);
		SatzMenuMain.Items[6] = new MenuItem("", null, null);
		SatzMenuMain.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);			
		
		Menu SatzMenuDatei = new Menu(null, null, SatzMenuMain, null, new Return(Hauptfenster));
		SatzMenuDatei.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		SatzMenuDatei.Items[1] = new MenuItem("Neu", null, new DateiNeu(Hauptfenster));
		SatzMenuDatei.Items[2] = new MenuItem("Öffnen", null, new DateiÖffnen(Hauptfenster));
		SatzMenuDatei.Items[3] = new MenuItem("Speichern", null, new DateiSpeichern(Hauptfenster));
		SatzMenuDatei.Items[4] = new MenuItem("<html>Speichern<br>Unter</html>", null, new DateiSpeichernUnter(Hauptfenster));
		SatzMenuDatei.Items[5] = new MenuItem("<html>Dateien<br>Verwalten</html>", null, new DateiVerwalten(Hauptfenster));
		SatzMenuDatei.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		SatzMenuDatei.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		SatzMenuMain.MenuNext[1] = SatzMenuDatei;
		
		Menu SatzMenuBearbeiten = new Menu(null, null, SatzMenuMain);
		SatzMenuBearbeiten.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		SatzMenuBearbeiten.Items[1] = new MenuItem("Rückgängig", null, new Rückgängig(Hauptfenster));
		SatzMenuBearbeiten.Items[2] = new MenuItem("Kopieren", null, new Kopieren(Hauptfenster));
		SatzMenuBearbeiten.Items[3] = new MenuItem("Einfügen", null, new Einfügen(Hauptfenster));
		SatzMenuBearbeiten.Items[4] = new MenuItem("", null, null);
		SatzMenuBearbeiten.Items[5] = new MenuItem("", null, null);
		SatzMenuBearbeiten.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		SatzMenuBearbeiten.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		SatzMenuMain.MenuNext[2] = SatzMenuBearbeiten;
	
		Menu SatzMenuAnsicht = new Menu(null, null, SatzMenuMain);
		SatzMenuAnsicht.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		SatzMenuAnsicht.Items[1] = new MenuItem("<html>Gehe zu<br>Zeile</html>", null, new GeheZuZeile(Hauptfenster));
		SatzMenuAnsicht.Items[2] = new MenuItem("Suchen", null, new Suchen(Hauptfenster));
		SatzMenuAnsicht.Items[3] = new MenuItem("<html>Schrift<br>Größe</html>", null, new Schriftgröße(Hauptfenster));
		SatzMenuAnsicht.Items[4] = new MenuItem("", null, null);
		SatzMenuAnsicht.Items[5] = new MenuItem("", null, null);
		SatzMenuAnsicht.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		SatzMenuAnsicht.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		SatzMenuMain.MenuNext[3] = SatzMenuAnsicht;
		
		Menu SatzMenuStart = new Menu(null, null, SatzMenuMain, null, new Return(Hauptfenster));
		SatzMenuStart.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		SatzMenuStart.Items[1] = new MenuItem("<html>Fräsen<br>Starten</html>", null, new FräsenStarten(Hauptfenster));
		SatzMenuStart.Items[2] = new MenuItem("<html>Fräsen<br>Stoppen</html>", null, new FräsenStoppen(Hauptfenster));
		SatzMenuStart.Items[3] = new MenuItem("", null, null);
		SatzMenuStart.Items[4] = new MenuItem("", null, null);
		SatzMenuStart.Items[5] = new MenuItem("", null, null);
		SatzMenuStart.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		SatzMenuStart.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		SatzMenuMain.MenuNext[4] = SatzMenuStart;
		
		Menu SatzMenuOptionen = new Menu(null, null, SatzMenuMain, null, new Return(Hauptfenster));
		SatzMenuOptionen.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		SatzMenuOptionen.Items[1] = new MenuItem("Befehle", null, new Befehle(Hauptfenster));
		SatzMenuOptionen.Items[2] = new MenuItem("", null, null);
		SatzMenuOptionen.Items[3] = new MenuItem("", null, null);
		SatzMenuOptionen.Items[4] = new MenuItem("", null, null);
		SatzMenuOptionen.Items[5] = new MenuItem("", null, null);
		SatzMenuOptionen.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		SatzMenuOptionen.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		SatzMenuMain.MenuNext[5] = SatzMenuOptionen;
		
		SatzMenu.ButtonMenuArray.setButtonMenu(SatzMenuMain);	

		//-----PanelSichtfeld_Hand-----	

		Editor = new CNCEditor(Hauptfenster);
		
		NotausListener = new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(Hauptfenster.Notaus.getState() == PinState.HIGH) {
					Notaus();
				} 		
			}
		};	
	}

	/**
	 * Stoppt den laufenden Betrieb und geht auf Notaus
	 */
	private void Notaus() {
		SatzFräsThread.interrupt();
		Hauptfenster.CNC.getSpindel().setState(false);
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			Hauptfenster.Notaus.addListener(NotausListener);
			SatzFräsThread = new SatzFräsThread(Hauptfenster);
			SatzFräsThread.start();	
			//Hauptfenster.Update.setFräsraum(Fräsraum);
			Hauptfenster.setContentPane(Editor.EditorPanel, PanelButton_Satz);
		} else {
			Hauptfenster.Notaus.removeListener(NotausListener);
			SatzFräsThread.stopFräsraum();
		}
	}
}

