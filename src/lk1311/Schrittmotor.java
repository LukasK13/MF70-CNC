package lk1311;

import com.pi4j.io.gpio.*;

/**
 * Stellt Methoden für das Betreiben eines Schrittmotors mit einem LM297 und einem LM298 Treiber bereit
 * @author Lukas Klass - 01.04.2015
 *
 */
public class Schrittmotor {
	private GpioPinDigitalOutput ClockPin, RichtungPin, SchrittweitePin; //Steuerpins für den Schrittmotor
	public final static int SchrittzahlHalb = 400; //Anzahl der Schritte im Halbschrittbetrieb für eine Umdrehung
	public final static int SchrittzahlVoll = 200; //Anzahl der Schritte im Vollschrittbetrieb für eine Umdrehung
	private final int MindestPause = 500000; //Pause zwischen logischem Ein und Aus

	/**
	 * Erzeugt eine neue Schrittmotorinstanz
	 * @param ClockPin GPIO-Pin an dem der Clock-Eingang des LM297 angeschlossen ist
	 * @param RichtungPin GPIO-Pin an dem der Richtungs-Eingang des LM297 angeschlossen ist
	 * @param SchrittweitePin GPIO-Pin an dem der Schrittweiten-Eingang des LM297 angeschlossen ist
	 */
	public Schrittmotor(GpioPinDigitalOutput ClockPin, GpioPinDigitalOutput RichtungPin, GpioPinDigitalOutput SchrittweitePin) {
		this.ClockPin = ClockPin;
		this.SchrittweitePin = SchrittweitePin;
		this.RichtungPin = RichtungPin;
	}

	/**
	 * Setzt die Richtung des Schrittmotors
	 * @param Richtung Richtung in die gedreht werden soll. True bedeutet, dass der Richtungs-Eingang des LM297 auf High geschaltet wird.
	 */
	public void setRichtung(boolean Richtung) {
		if(Richtung) {
			RichtungPin.setState(PinState.HIGH);
		} else {
			RichtungPin.setState(PinState.LOW);
		}		
	}

	/**
	 * Gibt die Richtung des Schrittmotors zurück
	 * @return Richtung in die gedreht wird. True bedeutet, dass der Richtungs-Eingang des LM297 auf High geschaltet ist.
	 */
	public boolean getRichtung() {
		if(RichtungPin.getState() == PinState.HIGH) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Setzt die Schrittweite des Schrittmotors
	 * @param Schrittweite Schrittweite des Schrittmotors. True: Vollschritt, False: Halbschritt
	 */
	public void setSchrittweite(boolean Schrittweite) {
		SchrittweitePin.setState(!Schrittweite);	
	}

	/**
	 * Gibt die Schrittweite des Schrittmotors zurück
	 * @return Schrittweite des Schrittmotors. True: Vollschritt, False: Halbschritt
	 */
	public boolean getSchrittweite() {
		return (SchrittweitePin.getState() == PinState.HIGH ? false : true);
		}

	/**
	 * Macht einen Schritt mit eingestellter Richtung und Schrittweite.
	 */
	public void doStep() {
		ClockPin.setState(true);
		try {
			Thread.currentThread();
			Thread.sleep(0, MindestPause);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		ClockPin.setState(false);
	}

	/**
	 * Macht einen Schritt mit angegebener Richtung und Schrittweite.
	 * @param Richtung Richtung in die der Scritt gehen soll. True: Positive Richtung, False: Negative Richtung
	 * @param Schrittweite Größe des Schritts. True: Vollschritt, False: Halbschritt
	 */
	public void doStep(boolean Richtung, boolean Schrittweite) {
		RichtungPin.setState(Richtung);
		SchrittweitePin.setState(!Schrittweite);
		ClockPin.setState(true);
		try {
			Thread.currentThread();
			Thread.sleep(0, MindestPause);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		ClockPin.setState(false);
	}
}
