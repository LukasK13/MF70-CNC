package lk1311;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;

/**
 * Erzeugt das Modul zur Handsteuerung
 * @author Lukas - 13.04.2016
 *
 */
public class Hand {
	private Hauptfenster Hauptfenster;
	private JPanel PanelButton_Hand, PanelSichtfeld_Hand;
	private JButton SpindelBtn, AusführenBtn, SteuerspannungBtn;
	private JLabel Achsausrichtung;
	private JScrollPane Befehle_Scroll;
	private CNCEditor Befehle;
	private GpioPinListenerDigital NotausListener;

	public HandFräsThread HandFräsThread;	
	public int AchseVerfahren = -1;
	public int SchrittweiteVerfahren = 10;
	public int SchrittweiteVerfahrenA = 0;
	public boolean EndschalterIgnorieren = false;
	/**
	 * 1: X-Achse
	 * 2: Y-Achse
	 * 3: Z-Achse
	 */
	public int AntastenAchse = 0;
	/**
	 * 1: Positive Richtung
	 * 2: Negative Richtung
	 */
	public int AntastenRichtung = 0;
	public CreateMenu HandMenu;
	public Menu HandMenuAchswahlXYZ, HandMenuAchswahlA;

	/**
	 * Löst die Initialisierung der Objekte aus
	 * @param Hauptfenster
	 */
	public Hand(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
		Initialisieren_Komponenten_Hand();
	}

	/**
	 * Initialisiert die GUI-Objekte und erzeugt die zugehörigen Listener
	 */
	private void Initialisieren_Komponenten_Hand() {
		PanelButton_Hand = new JPanel();
		PanelButton_Hand.setBackground(Color.LIGHT_GRAY);
		PanelButton_Hand.setLayout(null);

		HandMenu = new CreateMenu(PanelButton_Hand, Hauptfenster);

		Menu HandMenuMain = new Menu(null, null, null);
		HandMenuMain.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		HandMenuMain.Items[1] = new MenuItem("Achswahl", null, null);
		HandMenuMain.Items[2] = new MenuItem("Referenz", null, null);
		HandMenuMain.Items[3] = new MenuItem("<html>Koordinaten<br>system</html>", null, null);
		HandMenuMain.Items[4] = new MenuItem("I/O", null, new IO(Hauptfenster));
		HandMenuMain.Items[5] = new MenuItem("<html>Werkzeug<br>tabelle</html>", null, new Werkzeugdaten(Hauptfenster));
		HandMenuMain.Items[6] = new MenuItem("", new ImageIcon(Hand.class.getResource("/resources/Ausschalten.png")), new HandMenuBeenden(Hauptfenster));
		HandMenuMain.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);			

		HandMenuAchswahlListener HandMenuAchswahlListener = new HandMenuAchswahlListener(Hauptfenster);
		HandMenuAchswahlXYZ = new Menu(null, null, HandMenuMain, HandMenuAchswahlListener, HandMenuAchswahlListener);
		HandMenuAchswahlXYZ.Items[0] = new MenuItem("", Hauptfenster.Oben_Aktiv, null);
		HandMenuAchswahlXYZ.Items[1] = new MenuItem("<html>X-Achse<br>verfahren</html>", null, new HandMenuAchswahlXYZ(Hauptfenster));
		HandMenuAchswahlXYZ.Items[2] = new MenuItem("<html>Y-Achse<br>verfahren</html>", null, new HandMenuAchswahlXYZ(Hauptfenster));
		HandMenuAchswahlXYZ.Items[3] = new MenuItem("<html>Z-Achse<br>verfahren</html>", null, new HandMenuAchswahlXYZ(Hauptfenster));
		HandMenuAchswahlXYZ.Items[4] = new MenuItem("0.1", null, new HandMenuAchswahlXYZ(Hauptfenster));
		HandMenuAchswahlXYZ.Items[5] = new MenuItem("1.0", null, new HandMenuAchswahlXYZ(Hauptfenster));
		HandMenuAchswahlXYZ.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		HandMenuAchswahlXYZ.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		HandMenuMain.MenuNext[1] = HandMenuAchswahlXYZ;

		HandMenuAchswahlA = new Menu(null, HandMenuAchswahlXYZ, HandMenuMain, HandMenuAchswahlListener, HandMenuAchswahlListener);
		HandMenuAchswahlA.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		HandMenuAchswahlA.Items[1] = new MenuItem("<html>A-Achse<br>verfahren</html>", null, new HandMenuAchswahlA(Hauptfenster));
		HandMenuAchswahlA.Items[2] = new MenuItem("1°", null, new HandMenuAchswahlA(Hauptfenster));
		HandMenuAchswahlA.Items[3] = new MenuItem("5°", null, new HandMenuAchswahlA(Hauptfenster));
		HandMenuAchswahlA.Items[4] = new MenuItem("<html>Endschalter<br>Ignorieren</html>", null, new EndschalterIgnorieren(Hauptfenster));
		HandMenuAchswahlA.Items[5] = new MenuItem("", null, null);
		HandMenuAchswahlA.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		HandMenuAchswahlA.Items[7] = new MenuItem("", Hauptfenster.Unten_Aktiv, null);
		HandMenuAchswahlXYZ.MenuUp = HandMenuAchswahlA;

		Menu HandMenuReferenz = new Menu(null, null, HandMenuMain);
		HandMenuReferenz.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		HandMenuReferenz.Items[1] = new MenuItem("X-Referenz", null, new Referenz(Hauptfenster, 0));
		HandMenuReferenz.Items[2] = new MenuItem("Y-Referenz", null, new Referenz(Hauptfenster, 1));
		HandMenuReferenz.Items[3] = new MenuItem("Z-Referenz", null, new Referenz(Hauptfenster, 2));
		HandMenuReferenz.Items[4] = new MenuItem("A-Referenz", null, new Referenz(Hauptfenster, 3));
		HandMenuReferenz.Items[5] = new MenuItem("Alle", null, new Referenz(Hauptfenster, 4));		
		HandMenuReferenz.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		HandMenuReferenz.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		HandMenuMain.MenuNext[2] = HandMenuReferenz;

		Menu HandMenuKoordinatensystem = new Menu(null, null, HandMenuMain);
		HandMenuKoordinatensystem.Items[0] = new MenuItem("", Hauptfenster.Oben_Aktiv, null);
		HandMenuKoordinatensystem.Items[1] = new MenuItem("<html>X-Achse<br>nullen</html>", null, new AchseNullen(Hauptfenster, 0));
		HandMenuKoordinatensystem.Items[2] = new MenuItem("<html>Y-Achse<br>nullen</html>", null, new AchseNullen(Hauptfenster, 1));
		HandMenuKoordinatensystem.Items[3] = new MenuItem("<html>Z-Achse<br>nullen</html>", null, new AchseNullen(Hauptfenster, 2));
		HandMenuKoordinatensystem.Items[4] = new MenuItem("<html>Alle<br>nullen</html>", null, new AchseNullen(Hauptfenster, 3));
		HandMenuKoordinatensystem.Items[5] = new MenuItem("<html>Wert<br>eingabe</html>", null, new Werteingabe(Hauptfenster));
		HandMenuKoordinatensystem.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		HandMenuKoordinatensystem.Items[7] = new MenuItem("", Hauptfenster.Unten_Inaktiv, null);
		HandMenuMain.MenuNext[3] = HandMenuKoordinatensystem;

		Menu HandMenuAntasten = new Menu(null, HandMenuKoordinatensystem, HandMenuMain);
		HandMenuAntasten.Items[0] = new MenuItem("", Hauptfenster.Oben_Inaktiv, null);
		HandMenuAntasten.Items[1] = new MenuItem("<html>X-Achse<br>antasten</html>", null, new HandMenuAntasten(Hauptfenster));
		HandMenuAntasten.Items[2] = new MenuItem("<html>Y-Achse<br>antasten</html>", null, new HandMenuAntasten(Hauptfenster));
		HandMenuAntasten.Items[3] = new MenuItem("<html>Z-Achse<br>antasten</html>", null, new HandMenuAntasten(Hauptfenster));
		HandMenuAntasten.Items[4] = new MenuItem("Richtung +", null, new HandMenuAntasten(Hauptfenster));
		HandMenuAntasten.Items[5] = new MenuItem("Richtung -", null, new HandMenuAntasten(Hauptfenster));
		HandMenuAntasten.Items[6] = new MenuItem("", Hauptfenster.Zurück_Aktiv, null);
		HandMenuAntasten.Items[7] = new MenuItem("", Hauptfenster.Unten_Aktiv, null);
		HandMenuKoordinatensystem.MenuUp = HandMenuAntasten;

		HandMenu.ButtonMenuArray.setButtonMenu(HandMenuMain);

		//-----PanelSichtfeld_Hand-----	

		PanelSichtfeld_Hand = new JPanel();
		PanelSichtfeld_Hand.setBackground(Color.LIGHT_GRAY);
		PanelSichtfeld_Hand.setLayout(null);

		Achsausrichtung = new JLabel(new ImageIcon(Hauptfenster.class.getResource("/resources/Achsorientierung_500x520.png")));
		Achsausrichtung.setLocation(40, -15);
		Achsausrichtung.setSize(500, 523);
		PanelSichtfeld_Hand.add(Achsausrichtung);

		Befehle_Scroll = new JScrollPane();
		Befehle_Scroll.setBounds(562, 6, 550, 430);
		PanelSichtfeld_Hand.add(Befehle_Scroll);

		Befehle = new CNCEditor(Hauptfenster);
		Befehle.getEditor().setBounds(562, 6, 550, 430);
		Befehle_Scroll.setViewportView(Befehle.getEditor());

		SpindelBtn = new JButton("Spindel Ein");
		SpindelBtn.setBackground(new Color(0, 170, 0));
		SpindelBtn.setBounds(961, 450, 150, 40);
		SpindelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Hauptfenster.CNC.getSpindel().getState()) {
					Hauptfenster.CNC.getSpindel().setState(false);
					if(!Hauptfenster.CNC.getSpindel().getState()) {
						SpindelBtn.setText("Spindel Ein");
						SpindelBtn.setBackground(new Color(0, 170, 0));
					}					
				} else {
					Hauptfenster.CNC.getSpindel().setState(true);
					if(Hauptfenster.CNC.getSpindel().getState()) {
						SpindelBtn.setText("Spindel Aus");
						SpindelBtn.setBackground(Color.RED);
					}	
				}				
			}
		});
		PanelSichtfeld_Hand.add(SpindelBtn);

		AusführenBtn = new JButton("Ausführen");
		AusführenBtn.setBounds(799, 450, 150, 40);
		AusführenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Ausführen();
			}
		});		
		PanelSichtfeld_Hand.add(AusführenBtn);

		SteuerspannungBtn = new JButton("Steuerspannung");
		SteuerspannungBtn.setBackground(Color.LIGHT_GRAY);
		SteuerspannungBtn.setBounds(605, 450, 177, 40);
		SteuerspannungBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent c) {
				Steuerspannung();
			}
		});
		PanelSichtfeld_Hand.add(SteuerspannungBtn);

		NotausListener = new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(Hauptfenster.Notaus.getState() == PinState.HIGH) {
					Notaus();
				} 		
			}
		};		
	}

	/**
	 * Zeigt das Hand-Modul im Hauptfenster an und startet den Fräsraum
	 * @param visible Anzeigestatus des Moduls
	 */
	public void setVisible(boolean visible) {
		if(visible) {
			Hauptfenster.Notaus.addListener(NotausListener);
			HandFräsThread = new HandFräsThread(Hauptfenster);
			HandFräsThread.start();	
			//Hauptfenster.Update.setFräsraum(Fräsraum);
			Hauptfenster.setContentPane(PanelSichtfeld_Hand, PanelButton_Hand);
		} else {
			Hauptfenster.Notaus.removeListener(NotausListener);
			HandFräsThread.stopFräsraum();
		}
	}

	/**
	 * Schaltet den Zustand der Steuerspannung
	 */
	private void Steuerspannung() {
		if (Hauptfenster.SteuerspannungEin) {
			Hauptfenster.Schrittrelais.setState(PinState.LOW);
			Hauptfenster.Fräsenrelais.setState(PinState.LOW);
			SteuerspannungBtn.setBackground(Color.LIGHT_GRAY);
			Hauptfenster.SteuerspannungEin = false;
		} else {
			Hauptfenster.Schrittrelais.setState(PinState.HIGH);
			Hauptfenster.Fräsenrelais.setState(PinState.HIGH);
			SteuerspannungBtn.setBackground(Color.yellow);
			Hauptfenster.SteuerspannungEin = true;
		}
	}

	/**
	 * Stoppt den laufenden Betrieb und geht auf Notaus
	 */
	private void Notaus() {
		HandFräsThread.interrupt();
		Hauptfenster.CNC.getSpindel().setState(false);
	}

	/**
	 * Führt die eingegebenen Befehle aus (Antasten oder G-Code)
	 */
	private void Ausführen() {
		if((AntastenAchse != 0) && (AntastenRichtung != 0)) {
			if((AntastenAchse == 1) && (AntastenRichtung == 1)) {
				HandFräsThread.setAction("AntastenXPlus");
			} else if((AntastenAchse == 1) && (AntastenRichtung == 2)) {
				HandFräsThread.setAction("AntastenXMinus");
			} else if((AntastenAchse == 2) && (AntastenRichtung == 1)) {
				HandFräsThread.setAction("AntastenYPlus");
			} else if((AntastenAchse == 2) && (AntastenRichtung == 2)) {
				HandFräsThread.setAction("AntastenYMinus");
			} else if((AntastenAchse == 3) && (AntastenRichtung == 1)) {
				HandFräsThread.setAction("AntastenZPlus");
			} else if((AntastenAchse == 3) && (AntastenRichtung == 2)) {
				HandFräsThread.setAction("AntastenZMinus");
			}
			AntastenAchse = 0;
			AntastenRichtung = 0;
		} else {
			HandFräsThread.setAction(Befehle.getText());
		}
	}
}
