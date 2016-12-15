package lk1311;

import com.pi4j.io.gpio.GpioPinAnalogOutput;

/**
 * Stellt Funktionen für den Betrieb der Spindel bereit
 * @author Lukas Klass - 07.10.2015
 *
 */
public class Spindel {
	private Hauptfenster Hauptfenster;
	private GpioPinAnalogOutput SpindelOut;
	private boolean SpindelEin = false;
	private int Drehzahl = 0;
	private double DrehzahlProzent;
	
	/**
	 * Erzeugt eine neue Spindelinstanz
	 * @param Hauptfenster Hauptfenster, das die Instanz erzeugt
	 * @param SpindelOut Analoger Ausgang, an dem die Spindel angeschlossen ist
	 */
	public Spindel(Hauptfenster Hauptfenster, GpioPinAnalogOutput SpindelOut) {
		this.Hauptfenster = Hauptfenster;
		this.SpindelOut = SpindelOut;
	}
	
	/**
	 * Schaltet die Spindel ein oder aus
	 * @param State gewünschter Schaltungszustand: true = eingeschaltet, false = ausgeschaltet
	 */
	public void setState(boolean Status) {
		if(Status) {
			if(Hauptfenster.SteuerspannungEin) {
				SpindelEin = true;
				Update();
			} else {
				System.out.println("Bitte Steuerspannung einschalten.");
			}			
		} else {
			SpindelEin = false;
			SpindelOut.setValue(1.0);
		}
	}
	
	/**
	 * Gibt den Schaltungszustand der Spindel zurück
	 * @return true = eingeschaltet, false = ausgeschaltet
	 */
	public boolean getState() {
		return SpindelEin;
	}
	
	/**
	 * Wendet die Drehzahleinstellungen auf den analogen Ausgang an
	 */
	private void Update() {
		if((Drehzahl * DrehzahlProzent / 100.0) <= 16000.0) {
			SpindelOut.setValue(Math.log(16001.0 - Drehzahl * DrehzahlProzent / 100.0) / 9.8);
		} else if((Drehzahl * DrehzahlProzent / 100.0) > 20000.0){
			SpindelOut.setValue(0.0);
		} else {
			SpindelOut.setValue(Math.log((20001.0 - Drehzahl * DrehzahlProzent / 100.0) / 0.00000001) / 28.5);
		}
	}
	
	/**
	 * Stellt die angegebene Spindeldrehzahl ein
	 * @param Drehzahl gewünschte Spindeldrehzahl
	 */
	public void setDrehzahl(int Drehzahl) {
		this.Drehzahl = Drehzahl;
		Update();
	}
	
	/**
	 * Gibt die Spindeldrehzahl zurück
	 * @return Drehzahl
	 */
	public int getDrehzahl() {
		return Drehzahl;
	}

	/**
	 * Stellt den Vorteiler für die Drehzahl ein
	 * @param DrehzahlProzent Vorteiler
	 */
	public void setDrehzahlProzent(double DrehzahlProzent) {
		this.DrehzahlProzent = DrehzahlProzent;
		Update();
	}
	
	/**
	 * Gibt den Vorteiler für die Drehzahl zurück
	 * @return Vorteiler
	 */
	public double getDrehzahlProzent() {
		return DrehzahlProzent;
	}
}
