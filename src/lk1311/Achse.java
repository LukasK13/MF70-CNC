package lk1311;

import com.pi4j.io.gpio.*;

/**
 * Stellt Methoden für das Betrieben einer Achse mit einem Schrittmotor bereit.</br>
 * Anmerkung L297: Logisch Eins am H/F-Eingang bedeutet Halbschrittbetrieb, Logisch Null am H/F-Eingang bedeutet Vollschrittbetrieb</br>
 * Schrittweite true bedeutet Vollschrittbetrieb, Schrittweite false bedeutet Halbschrittbetrieb
 * @author Lukas Klass - 01.04.2015
 *
 */
public class Achse {
	private Schrittmotor Schrittmotor;
	private GpioPinDigitalInput EndschalterPlus, EndschalterMinus;
	private String Achsname;
	private boolean RichtungPlus;
	private double Nullpunkt = 0, KoordinateRelativ = 0, Achslänge = 0;	
	private final int PauseReferenz = 650000;
	private Hauptfenster Hauptfenster;

	/**
	 * Erzeugt eine neue Achsinstanz
	 * @param ClockPin GPIO-Pin an dem der Clock-Eingang des LM297 angeschlossen ist
	 * @param RichtungPin GPIO-Pin an dem der Richtungs-Eingang des LM297 angeschlossen ist
	 * @param SchrittweitePin GPIO-Pin an dem der Schrittweiten-Eingang des LM297 angeschlossen ist
	 * @param EndschalterPlus GPIO-Pin an dem der Endschalter in positiver Richtung angeschlossen ist
	 * @param EndschalterMinus GPIO-Pin an dem der Endschalter in negativer Richtung angeschlossen ist
	 * @param Achsname Name der Achse
	 */
	public Achse(GpioPinDigitalOutput ClockPin, GpioPinDigitalOutput RichtungPin, GpioPinDigitalOutput SchrittweitePin, GpioPinDigitalInput EndschalterPlus, GpioPinDigitalInput EndschalterMinus, String Achsname, Hauptfenster Hauptfenster) {
		this.EndschalterPlus = EndschalterPlus;
		this.EndschalterMinus = EndschalterMinus;
		this.Achsname = Achsname;
		this.Hauptfenster = Hauptfenster;
		Schrittmotor = new Schrittmotor(ClockPin, RichtungPin, SchrittweitePin);
	}

	/**
	 * Erzeugt eine neue Achsinstanz mit Korrektur der positiven Achsrichtung
	 * @param ClockPin GPIO-Pin an dem der Clock-Eingang des LM297 angeschlossen ist
	 * @param RichtungPin GPIO-Pin an dem der Richtungs-Eingang des LM297 angeschlossen ist
	 * @param SchrittweitePin GPIO-Pin an dem der Schrittweiten-Eingang des LM297 angeschlossen ist
	 * @param EndschalterPlus GPIO-Pin an dem der Endschalter in positiver Richtung angeschlossen ist
	 * @param EndschalterMinus GPIO-Pin an dem der Endschalter in negativer Richtung angeschlossen ist
	 * @param RichtungPlus Logischer Wert, der am LM297 anliegen muss um in positiver Richtung zu verfahren
	 * @param Achsname Name der Achse
	 */
	public Achse(GpioPinDigitalOutput ClockPin, GpioPinDigitalOutput RichtungPin, GpioPinDigitalOutput SchrittweitePin, GpioPinDigitalInput EndschalterPlus, GpioPinDigitalInput EndschalterMinus, boolean RichtungPlus, String Achsname, Hauptfenster Hauptfenster) {
		this.EndschalterPlus = EndschalterPlus;
		this.EndschalterMinus = EndschalterMinus;
		this.RichtungPlus = RichtungPlus;
		this.Achsname = Achsname;
		this.Hauptfenster = Hauptfenster;
		Schrittmotor = new Schrittmotor(ClockPin, RichtungPin, SchrittweitePin);
	}

	
	
	/**
	 * Setzt den Logischen Wert, der am LM297 anliegen muss um in positiver Richtung zu verfahren
	 * @param RichtungPlus Plus-Richtung
	 */
	public void setRichtungPlus(boolean RichtungPlus) {
		this.RichtungPlus = RichtungPlus;
	}

	/**
	 * Gibt den Logischen Wert zurück, der am LM297 anliegen muss um in positiver Richtung zu verfahren
	 * @return Plus-Richtung
	 */
	public boolean getRichtungPlus() {
		return RichtungPlus;
	}

	/**
	 * Stellt die angegebene Richtung ein.
	 * @param Richtung Richtung in die der Schritt erfolgen soll. True: positive Richtung, False: negative Richtung
	 */
	public void setRichtung(boolean Richtung) {
		if(Richtung) {
			Schrittmotor.setRichtung(RichtungPlus);
		} else {
			Schrittmotor.setRichtung(!RichtungPlus);
		}		
	}

	/**
	 * Gibt die eingestellte Richtung zurück. True: positive Richtung, False: negative Richtung
	 * @return
	 */
	public boolean getRichtung() {
		return (Schrittmotor.getRichtung() == RichtungPlus ? true : false);
	}
	
	/**
	 * Stellt die angegebene Schrittweite ein.
	 * @param Schrittweite Schrittweite mit der der Schritt ausgeführt werden soll. True: Vollschritt, False: Halbschritt
	 */
	public void setSchrittweite(boolean Schrittweite) {
		Schrittmotor.setSchrittweite(Schrittweite);
	}
	
	/**
	 * Gibt die eingestellte Schrittweite zurück. True: Vollschritt, False: Halbschritt
	 * @return
	 */
	public boolean getSchrittweite() {
		return Schrittmotor.getSchrittweite();
	}

	
	
	/**
	 * Führt eine Referenzfahrt in positiver Richtung durch.
	 * @return Rückmeldung über den Erfolg der Referenzfahrt
	 * @throws EndschalterException Falls während der Referenzfahrt der falsche Endschalter erreicht wird
	 */
	public boolean ReferenzPlus() throws EndschalterException {
		boolean Return = false;
		Schrittmotor.setRichtung(RichtungPlus);
		Schrittmotor.setSchrittweite(false);
		while(!Thread.currentThread().isInterrupted() && EndschalterPlus.getState() == PinState.HIGH) {
			Schrittmotor.doStep();
			sleep(PauseReferenz);
			KoordinateRelativ += 0.0025;
		}
		if(EndschalterPlus.getState() == PinState.LOW) {
			Return = true;
		} else if(EndschalterMinus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "negativ");
		}
		return Return;
	}

	/**
	 * Führt eine Referenzfahrt in negativer Richtung durch.
	 * @return Rückmeldung über den Erfolg der Referenzfahrt
	 * @throws EndschalterException Falls während der Referenzfahrt der falsche Endschalter erreicht wird
	 */
	public boolean ReferenzMinus() throws EndschalterException {
		boolean Return = false;
		Schrittmotor.setRichtung(!RichtungPlus);
		Schrittmotor.setSchrittweite(false);
		while(!Thread.currentThread().isInterrupted() && EndschalterMinus.getState() == PinState.HIGH) {
			Schrittmotor.doStep();
			sleep(PauseReferenz);
			KoordinateRelativ -= 0.0025;
		}
		if(EndschalterMinus.getState() == PinState.LOW) {
			Return = true;
		} else if(EndschalterMinus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "positiv");
		}
		return Return;
	}

	/**
	 * Führt eine Referenzfahrt in beide Richtungen durch und stellt anschließend den Absoluten Nullpunkt ein.
	 */
	public void Referenz() {
		try {
			ReferenzPlus();
		} catch (EndschalterException e) {
			e.printStackTrace();
		}
		if(!Thread.currentThread().isInterrupted()) {
			KoordinateRelativ = 0;
		}		
		try {
			ReferenzMinus();
		} catch (EndschalterException e) {
			e.printStackTrace();
		}
		if(!Thread.currentThread().isInterrupted()) {
			Achslänge = Math.abs(KoordinateRelativ);
			KoordinateRelativ /= 2;
		}		
		VerfahrenOhneEndschalter(true, true, 100, 5);
	}

	
	
	/**
	 * Stellt die Parameter ein und macht einen Schritt.
	 * @param Richtung Richtung in die der Schritt erfolgen soll. True: positive Richtung, False: negative Richtung
	 * @param Schrittweite Schrittweite mit der der Schritt ausgeführt werden soll. True: Vollschritt, False: Halbschritt
	 * @throws EndschalterException Falls während des Verfahrens ein Endschalter erreicht wird
	 */
	public void doSingleStep(boolean Richtung, boolean Schrittweite) throws EndschalterException {
		if(!Thread.currentThread().isInterrupted() && EndschalterPlus.getState() == PinState.HIGH && EndschalterMinus.getState() == PinState.HIGH) {
			Schrittmotor.setSchrittweite(Schrittweite);
			Schrittmotor.setRichtung(Richtung ? RichtungPlus : !RichtungPlus);
			
			Schrittmotor.doStep();
			if(Richtung) {
				KoordinateRelativ += (Schrittweite ? 0.005 : 0.0025);
			} else {
				KoordinateRelativ -= (Schrittweite ? 0.005 : 0.0025);
			}
		} else if(EndschalterPlus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "positiv");
		} else if(EndschalterMinus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "negativ");
		}
	}

	/**
	 * Stellt keine Parameter ein und macht einen Schritt.
	 * @param Richtung Richtung in die der Schritt erfolgen soll. True: positive Richtung, False: negative Richtung
	 * @param Schrittweite Schrittweite mit der der Schritt ausgeführt werden soll. True: Vollschritt, False: Halbschritt
	 * @throws EndschalterException Falls während des Verfahrens ein Endschalter erreicht wird
	 */
	public void doMultipleStep(boolean Richtung, boolean Schrittweite) throws EndschalterException {
		if(!Thread.currentThread().isInterrupted() && EndschalterPlus.getState() == PinState.HIGH && EndschalterMinus.getState() == PinState.HIGH) {
			Schrittmotor.doStep();
			if(Richtung) {
				KoordinateRelativ += (Schrittweite ? 0.005 : 0.0025);
			} else {
				KoordinateRelativ -= (Schrittweite ? 0.005 : 0.0025);
			}
		} else if(EndschalterPlus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "positiv");
		} else if(EndschalterMinus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "negativ");
		}
	}

	/**
	 * Verfährt mit der Achse mit den angegebenen Parametern.
	 * @param Richtung Richtung in die gefahren werden soll. True: positive Richtung, False: negative Richtung
	 * @param Vorschub Fahrgeschwindigkeit
	 * @param Länge Strecke um die verfahren wird
	 * @throws EndschalterException Falls während des Verfahrens ein Endschalter erreicht wird
	 */
	public void Verfahren(boolean Richtung, int Vorschub, double Länge) throws EndschalterException{
		if(!Thread.currentThread().isInterrupted() && EndschalterPlus.getState() == PinState.HIGH && EndschalterMinus.getState() == PinState.HIGH) {
			Hauptfenster.Vorschub = Vorschub;
			
			int Schritte = (int) (Länge * lk1311.Schrittmotor.SchrittzahlHalb);
			long nanos = 0;
			
			Schrittmotor.setRichtung(Richtung ? RichtungPlus : !RichtungPlus);
			Schrittmotor.setSchrittweite(false);
			
			for (int i=0; i<Schritte; i++) {
				nanos = System.nanoTime();
				
				if(Hauptfenster.VorschubProzent == 0 || Thread.currentThread().isInterrupted()) {
					break;
				} else if(EndschalterPlus.getState() == PinState.LOW) {
					throw new EndschalterException(Achsname, "positiv");
				} else if(EndschalterMinus.getState() == PinState.LOW) {
					throw new EndschalterException(Achsname, "negativ");
				}

				Schrittmotor.doStep();
				if(Richtung) {
					KoordinateRelativ += 0.0025;
				} else {					
					KoordinateRelativ -= 0.0025;
				}
				sleep((long) (6000.0 / (Vorschub * Hauptfenster.VorschubProzent * lk1311.Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
			}
		} else if(EndschalterPlus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "positiv");
		} else if(EndschalterMinus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "negativ");
		}
	}

	/**
	 * Verfährt mit der Achse im Eilgang mit den angegebenen Parametern.
	 * @param Richtung Richtung in die gefahren werden soll. True: positive Richtung, False: negative Richtung
	 * @param Vorschub Fahrgeschwindigkeit
	 * @param Länge Strecke um die verfahren wird
	 * @throws EndschalterException Falls während des Verfahrens ein Endschalter erreicht wird
	 */
	public void VerfahrenEil(boolean Richtung, int Vorschub, double Länge) throws EndschalterException{
		if(!Thread.currentThread().isInterrupted() && EndschalterPlus.getState() == PinState.HIGH && EndschalterMinus.getState() == PinState.HIGH) {
			Hauptfenster.VorschubEil = Vorschub;
			
			int Schritte = (int) (Länge * lk1311.Schrittmotor.SchrittzahlVoll);
			long nanos = 0;
			
			Schrittmotor.setRichtung(Richtung ? RichtungPlus : !RichtungPlus);
			Schrittmotor.setSchrittweite(true);
			
			for (int i=0; i<Schritte; i++) {
				nanos = System.nanoTime();

				if(Hauptfenster.VorschubEilProzent == 0) {
					break;
				} else if(Thread.currentThread().isInterrupted()) {
					break;
				} else if(EndschalterPlus.getState() == PinState.LOW) {
					throw new EndschalterException(Achsname, "positiv");
				} else if(EndschalterMinus.getState() == PinState.LOW) {
					throw new EndschalterException(Achsname, "negativ");
				}

				Schrittmotor.doStep();
				if(Richtung) {
					KoordinateRelativ += 0.005;
				} else {					
					KoordinateRelativ -= 0.005;
				}
				sleep((long) (6000.0 / (Vorschub * Hauptfenster.VorschubEilProzent * lk1311.Schrittmotor.SchrittzahlVoll) * 1000000000.0) -  System.nanoTime() + nanos);
			}
		} else if(EndschalterPlus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "positiv");
		} else if(EndschalterMinus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "negativ");
		}
	}

	/**
	 * Verfährt mit der Achse mit den angegebenen Parametern ohne Endschalterinterrupts zu berücksichtigen.
	 * @param Richtung Richtung in die gefahren werden soll. True: positive Richtung, False: negative Richtung
	 * @param Schrittweite Schrittweite mit der Verfahren werden soll. True: Vollschritt, False: Halbschritt
	 * @param Vorschub Fahrgeschwindigkeit
	 * @param Länge Strecke um die verfahren wird
	 */
	public void VerfahrenOhneEndschalter(boolean Richtung, boolean Schrittweite, int Vorschub, double Länge) {
		if(!Thread.currentThread().isInterrupted()) {
			Hauptfenster.Vorschub = Vorschub;
			
			int Schritte = (int) (Schrittweite ? (Länge * lk1311.Schrittmotor.SchrittzahlVoll) : (Länge * lk1311.Schrittmotor.SchrittzahlHalb));
			long nanos = 0;
			
			Schrittmotor.setRichtung(Richtung ? RichtungPlus : !RichtungPlus);
			Schrittmotor.setSchrittweite(Schrittweite);
			
			for (int i=0; i<Schritte; i++) {
				nanos = System.nanoTime();

				if(Hauptfenster.VorschubProzent == 0) {
					break;
				} else if(Thread.currentThread().isInterrupted()) {
					break;
				}		

				Schrittmotor.doStep();
				if(Richtung) {
					KoordinateRelativ += (Schrittweite ? 0.005 : 0.0025);
				} else {					
					KoordinateRelativ -= (Schrittweite ? 0.005 : 0.0025);
				}				
				sleep((long) (6000.0 / (Vorschub * Hauptfenster.VorschubProzent * (Schrittweite ? lk1311.Schrittmotor.SchrittzahlVoll : lk1311.Schrittmotor.SchrittzahlHalb)) * 1000000000.0) -  System.nanoTime() + nanos);
			}
		}
	}

	/**
	 * Dreht die Achse mit den angegebenen Paramteren
	 * @param Richtung Richtung in die gedreht werden soll. True: positive Richtung, False: negative Richtung
	 * @param Schrittweite Schrittweite mit der gedreht werden soll. True: Vollschritt, False: Halbschritt
	 * @param Vorschub Drehgeschwindigkeit
	 * @param Winkel Winkel um den gedreht werden soll
	 * @throws EndschalterException Falls während des Drehens ein Endschalter erreicht wird
	 */
	public void Drehen(boolean Richtung, int Vorschub, double Winkel) throws EndschalterException{
		if(!Thread.currentThread().isInterrupted() && EndschalterPlus.getState() == PinState.HIGH && EndschalterMinus.getState() == PinState.HIGH) {
			int Schritte = (int) (Winkel * lk1311.Schrittmotor.SchrittzahlHalb / 360);
			long nanos = 0;
			
			Schrittmotor.setSchrittweite(false);
			Schrittmotor.setRichtung(Richtung ? RichtungPlus : !RichtungPlus);

			for (int i=0; i<Schritte; i++) {
				nanos = System.nanoTime();
				
				if(Hauptfenster.VorschubProzent == 0) {
					break;
				} else if(Thread.currentThread().isInterrupted()) {
					break;
				} else if(EndschalterPlus.getState() == PinState.LOW) {
					throw new EndschalterException(Achsname, "positiv");
				} else if(EndschalterMinus.getState() == PinState.LOW) {
					throw new EndschalterException(Achsname, "negativ");
				}	
				
				Schrittmotor.doStep();
				if(Richtung) {
					KoordinateRelativ += 0.0025;
				} else {					
					KoordinateRelativ -= 0.0025;
				}
				sleep((long) (6000.0 / (Vorschub * Hauptfenster.VorschubProzent * lk1311.Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
			}
		} else if(EndschalterPlus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "positiv");
		} else if(EndschalterMinus.getState() == PinState.LOW) {
			throw new EndschalterException(Achsname, "negativ");
		}
	}

	/**
	 * Dreht die Achse mit den angegebenen Paramteren ohne Endschalterinterrupts zu berücksichtigen.
	 * @param Richtung Richtung in die gedreht werden soll. True: positive Richtung, False: negative Richtung
	 * @param Schrittweite Schrittweite mit der gedreht werden soll. True: Vollschritt, False: Halbschritt
	 * @param Vorschub Drehgeschwindigkeit
	 * @param Winkel Winkel um den gedreht werden soll
	 */
	public void DrehenOhneEndschalter(boolean Richtung, int Vorschub, double Winkel) {
		if(!Thread.currentThread().isInterrupted()) {
			int Schritte = (int) (Winkel * lk1311.Schrittmotor.SchrittzahlHalb / 360);
			long nanos = 0;
			
			Schrittmotor.setSchrittweite(false);
			Schrittmotor.setRichtung(Richtung ? RichtungPlus : !RichtungPlus);

			for (int i=0; i<Schritte; i++) {
				nanos = System.currentTimeMillis();
				
				if(Hauptfenster.VorschubProzent == 0) {
					break;
				} else if(Thread.currentThread().isInterrupted()) {
					break;
				}
				
				Schrittmotor.doStep();
				if(Richtung) {
					KoordinateRelativ += 0.0025;
				} else {					
					KoordinateRelativ -= 0.0025;
				}
				sleep((long) (6000 / (Vorschub * Hauptfenster.VorschubProzent * lk1311.Schrittmotor.SchrittzahlHalb) * 1000000000) -  System.currentTimeMillis() + nanos);
			}
		}
	}

	
	
	/**
	 * Setzt die Entfernung zum absoluten Nullpunkt und verschiebt damit den absoluten Nullpunkt.
	 * @param KoordinateAbsolut neue Entfernung zum absoluten Nullpunkt
	 */
	public void setKoordinateAbsolut(double KoordinateAbsolut) {
		Nullpunkt =+ (KoordinateAbsolut - (Nullpunkt + KoordinateRelativ));
	}

	/**
	 * Gibt die Entfernung zum absoluten Nullpunkt zurück;
	 * @return Entfernung
	 */
	public double getKoordinateAbsolut() {
		return Nullpunkt + KoordinateRelativ;
	}

	/**
	 * Setzt den Nullpunkt auf die aktuelle Stelle.
	 */
	public void setNullpunkt() {
		Nullpunkt += KoordinateRelativ;
		KoordinateRelativ = 0;
	}

	/**
	 * Gibt den Absolutwert des Nullpunkts zurück.
	 * @return Nullpunkt
	 */
	public double getNullpunkt() {
		return Nullpunkt;
	}

	/**
	 * Setzt die Entfernung zum Nullpunkt und verschiebt damit den Nullpunkt.
	 * @param KoordinateRelativ neue Entfernung zum Nullpunkt
	 */
	public void setKoordinateRelativ(double KoordinateRelativ) {
		Nullpunkt += KoordinateRelativ - this.KoordinateRelativ;
		this.KoordinateRelativ = KoordinateRelativ;	
	}

	/**
	 * Gibt die Entfernung zum Nullpunkt zurück.
	 * @return Entfernung
	 */
	public double getKoordinateRelativ() {
		return KoordinateRelativ;
	}

	/**
	 * Setzt die Achslänge.
	 * @param Achslänge
	 */
	public void setAchslänge(double Achslänge) {
		this.Achslänge = Achslänge;
	}

	/**
	 * Gibt die Länge der Achse zurück.
	 * @return Achslänge
	 */
	public double getAchslänge() {
		return Achslänge;
	}

	/**
	 * Setzt den Nullpunkt nach einer Referenzfahrt
	 */
	public void setReferenzNull() {
		Nullpunkt = 0;
		KoordinateRelativ = 0;
	}

	private void sleep(long nanos) {
		long time = System.nanoTime();
		while(System.nanoTime() - time < nanos) {
			try {
				Thread.currentThread();
				Thread.sleep(0,10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}




/**
 * Exception, die beim Betätigen eines Endschalters geworfen wird.
 * Stellt getAchsname() und getEndschalterseite() bereit
 * @author Lukas Klass - 01.04.2015
 *
 */
class EndschalterException extends Exception {
	String Achsname, Endschalterseite;

	/**
	 * Exception, die beim Betätigen eines Endschalters geworfen wird.
	 * @param Achsname Name der Achse an der der Endschalter betätigt wurde
	 * @param Endschalterseite Seite des Endschalters
	 */
	public EndschalterException(String Achsname, String Endschalterseite) {
		super(Achsname + ": Endschalter in " + Endschalterseite + "er Richtung Erreicht");	
		this.Achsname = Achsname;
		this.Endschalterseite = Endschalterseite;
	}

	/**
	 * Gibt den Namen der Achse zurück an dem der Endschalter betätigt wurde.
	 * @return Name der Achse
	 */
	public String getAchsname() {
		return Achsname;
	}

	/**
	 * Gibt die Seite des Endschalters zurück. True: Positive Seite, False: Negative Seite
	 * @return Seite des Endschalters
	 */
	public boolean getEndschalterseite() {
		if(Endschalterseite.equals("positiv")) {
			return true;
		} else {
			return false;
		}
	}
}
