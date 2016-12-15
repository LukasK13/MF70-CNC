package lk1311;

/**
 * Berechnet die Fahrweg der Fräsmaschine
 * @author Lukas Klass - 30.06.2016
 *
 */
public class CNC {
	private Hauptfenster Hauptfenster;
	private Achse xAchse=null, yAchse=null, zAchse=null, aAchse=null;
	private Spindel Spindel=null;
	
	/**
	 * Erzeugt eine neue Instanz des CNC-Algorithmus
	 * @param Hauptfenster aufrufendes Hauptfenster
	 */
	public CNC(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
		xAchse = new Achse(Hauptfenster.Clock1, Hauptfenster.CW1, Hauptfenster.HF1, Hauptfenster.EndschalterXPlus, Hauptfenster.EndschalterXMinus, false, "Z-Achse", Hauptfenster);		
		yAchse = new Achse(Hauptfenster.Clock3, Hauptfenster.CW3, Hauptfenster.HF3, Hauptfenster.EndschalterYPlus, Hauptfenster.EndschalterYMinus, false, "Y-Achse", Hauptfenster);
		zAchse = new Achse(Hauptfenster.Clock2, Hauptfenster.CW2, Hauptfenster.HF2, Hauptfenster.EndschalterZPlus, Hauptfenster.EndschalterZMinus, false, "X-Achse", Hauptfenster);
		aAchse = new Achse(Hauptfenster.Clock4, Hauptfenster.CW4, Hauptfenster.HF4, Hauptfenster.EndschalterAPlus, Hauptfenster.EndschalterAMinus, false, "A-Achse", Hauptfenster);
		
		Spindel = new Spindel(Hauptfenster, Hauptfenster.SpindelOut);
	}
	
	/**
	 * Berechnet eine Verbindungsgerade von der aktuellen Position zum Endpunkt unter Berücksichtigung der Werkzeugkorrektur
	 * @param EndeX x-Koordinate des Endpunkts
	 * @param EndeY y-Koordinate des Endpunkts
	 * @param EndeZ z-Koordinate des Endpunkts
	 */
	public void Linearinterpolation(double EndeX, double EndeY, double EndeZ) {
		Vektor Richtung = null;
		Vektor Startpunkt = null;
		Vektor Endpunkt = null;
		if(Hauptfenster.Maß == 0) {
			Richtung = new Vektor(EndeX - xAchse.getKoordinateRelativ(), EndeY - yAchse.getKoordinateRelativ(), EndeZ - zAchse.getKoordinateRelativ());
			if(Hauptfenster.Korrektur == 0) {
				Startpunkt = new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ());
				Endpunkt = new Vektor(EndeX, EndeY, EndeZ);
			} else if(Hauptfenster.Korrektur == 1) {
				Startpunkt = KorrekturpunktLinks(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktLinks(new Vektor(EndeX, EndeY, EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, false);
			} else if(Hauptfenster.Korrektur == 2) {
				Startpunkt = KorrekturpunktRechts(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktRechts(new Vektor(EndeX, EndeY, EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, false);
			}
			Gerade(Startpunkt, Endpunkt, false);
		} else {
			Richtung = new Vektor(EndeX, EndeY, EndeZ);
			if(Hauptfenster.Korrektur == 0) {
				Startpunkt = new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ());
				Endpunkt = new Vektor(xAchse.getKoordinateRelativ() + EndeX, yAchse.getKoordinateRelativ() + EndeY, zAchse.getKoordinateRelativ() + EndeZ);
			} else if(Hauptfenster.Korrektur == 1) {
				Startpunkt = KorrekturpunktLinks(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktLinks(new Vektor(xAchse.getKoordinateRelativ() + EndeX, yAchse.getKoordinateRelativ() + EndeY, zAchse.getKoordinateRelativ() + EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, false);
			} else if(Hauptfenster.Korrektur == 2) {
				Startpunkt = KorrekturpunktRechts(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktRechts(new Vektor(xAchse.getKoordinateRelativ() + EndeX, yAchse.getKoordinateRelativ() + EndeY, zAchse.getKoordinateRelativ() + EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, false);
			}
			Gerade(Startpunkt, Endpunkt, false);
		}
	}
	
	/**
	 * Berechnet eine Verbindungsgerade von der aktuellen Position zum Endpunkt unter Berücksichtigung der Werkzeugkorrektur im Eilgang
	 * @param EndeX x-Koordinate des Endpunkts
	 * @param EndeY y-Koordinate des Endpunkts
	 * @param EndeZ z-Koordinate des Endpunkts
	 */
	public void LinearinterpolationEilgang(double EndeX, double EndeY, double EndeZ) {
		Vektor Richtung = null;
		Vektor Startpunkt = null;
		Vektor Endpunkt = null;
		if(Hauptfenster.Maß == 0) {
			Richtung = new Vektor(EndeX - xAchse.getKoordinateRelativ(), EndeY - yAchse.getKoordinateRelativ(), EndeZ - zAchse.getKoordinateRelativ());
			if(Hauptfenster.Korrektur == 0) {
				Startpunkt = new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ());
				Endpunkt = new Vektor(EndeX, EndeY, EndeZ);
			} else if(Hauptfenster.Korrektur == 1) {
				Startpunkt = KorrekturpunktLinks(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktLinks(new Vektor(EndeX, EndeY, EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, true);
			} else if(Hauptfenster.Korrektur == 2) {
				Startpunkt = KorrekturpunktRechts(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktRechts(new Vektor(EndeX, EndeY, EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, true);
			}
			Gerade(Startpunkt, Endpunkt, true);
		} else {
			Richtung = new Vektor(EndeX, EndeY, EndeZ);
			if(Hauptfenster.Korrektur == 0) {
				Startpunkt = new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ());
				Endpunkt = new Vektor(xAchse.getKoordinateRelativ() + EndeX, yAchse.getKoordinateRelativ() + EndeY, zAchse.getKoordinateRelativ() + EndeZ);
			} else if(Hauptfenster.Korrektur == 1) {
				Startpunkt = KorrekturpunktLinks(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktLinks(new Vektor(xAchse.getKoordinateRelativ() + EndeX, yAchse.getKoordinateRelativ() + EndeY, zAchse.getKoordinateRelativ() + EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, true);
			} else if(Hauptfenster.Korrektur == 2) {
				Startpunkt = KorrekturpunktRechts(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Richtung);
				Endpunkt = KorrekturpunktRechts(new Vektor(xAchse.getKoordinateRelativ() + EndeX, yAchse.getKoordinateRelativ() + EndeY, zAchse.getKoordinateRelativ() + EndeZ), Richtung);
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), Startpunkt, true);
			}
			Gerade(Startpunkt, Endpunkt, true);
		}
	}
	
	/**
	 * Verfährt auf gerader Strecke vom Startpunkt zum Endpunkt
	 * @param Startpunkt Startpunkt in Vektorform
	 * @param Endpunkt Endpunkt in Vektorform
	 * @param Eilgang Eilgang (True: Eilgang, False: normale Geschwindigkeit)
	 */
	private void Gerade(Vektor Startpunkt, Vektor Endpunkt, boolean Eilgang) {
		Vektor Richtung = new Vektor(Endpunkt.getX() - Startpunkt.getX(), Endpunkt.getY() - Startpunkt.getY(), Endpunkt.getZ() - Startpunkt.getZ());
		double Maximum = Richtung.max();
		
		xAchse.setRichtung((Richtung.getX() >= 0) ? true : false);
		xAchse.setSchrittweite(Eilgang);
		
		yAchse.setRichtung((Richtung.getY() >= 0) ? true : false);
		yAchse.setSchrittweite(Eilgang);
		
		zAchse.setRichtung((Richtung.getZ() >= 0) ? true : false);
		zAchse.setSchrittweite(Eilgang);
		
		double xAlt = xAchse.getKoordinateRelativ();
		double yAlt = yAchse.getKoordinateRelativ();
		double zAlt = zAchse.getKoordinateRelativ();
		
		double Schrittlänge;
		if(Eilgang) {
			Schrittlänge = 0.005;
		} else {
			Schrittlänge = 0.0025;
		}
		
		for(double i=0; i<Maximum; i += Schrittlänge) {
			double Prozent = i / Maximum;
			long nanos = 0;
						
			if(Richtung.getX() > 0) {
				nanos = System.nanoTime();
				while(xAchse.getKoordinateRelativ() + Schrittlänge <= (xAlt + Richtung.getX() * Prozent)) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					try {
						xAchse.doMultipleStep(true, Eilgang);
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					sleep((long) (6000.0 / (Hauptfenster.Vorschub * Hauptfenster.VorschubProzent * Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
					nanos = System.nanoTime();
				}
			} else if(Richtung.getX() < 0) {
				nanos = System.nanoTime();
				while(xAchse.getKoordinateRelativ() - Schrittlänge >= (xAlt + Richtung.getX() * Prozent)) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					try {
						xAchse.doMultipleStep(false, Eilgang);
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					sleep((long) (6000.0 / (Hauptfenster.Vorschub * Hauptfenster.VorschubProzent * Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
					nanos = System.nanoTime();
				}
			}	
			
			if(Richtung.getY() > 0) {
				nanos = System.nanoTime();
				while(yAchse.getKoordinateRelativ() + Schrittlänge <= (yAlt + Richtung.getY() * Prozent)) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					try {
						yAchse.doMultipleStep(true, Eilgang);
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					sleep((long) (6000.0 / (Hauptfenster.Vorschub * Hauptfenster.VorschubProzent * Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
					nanos = System.nanoTime();
				}
			} else if(Richtung.getY() < 0) {
				nanos = System.nanoTime();
				while(yAchse.getKoordinateRelativ() - Schrittlänge >= (yAlt + Richtung.getY() * Prozent)) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					try {
						yAchse.doMultipleStep(false, Eilgang);
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					sleep((long) (6000.0 / (Hauptfenster.Vorschub * Hauptfenster.VorschubProzent * Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
					nanos = System.nanoTime();
				}
			}
			
			if(Richtung.getZ() > 0) {
				nanos = System.nanoTime();
				while(zAchse.getKoordinateRelativ() + Schrittlänge <= (zAlt + Richtung.getZ() * Prozent)) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					try {
						zAchse.doMultipleStep(true, Eilgang);
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					sleep((long) (6000.0 / (Hauptfenster.Vorschub * Hauptfenster.VorschubProzent * Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
					nanos = System.nanoTime();
				}
			} else if(Richtung.getZ() < 0) {
				nanos = System.nanoTime();
				while(zAchse.getKoordinateRelativ() - Schrittlänge >= (zAlt + Richtung.getZ() * Prozent)) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					try {
						zAchse.doMultipleStep(false, Eilgang);
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					sleep((long) (6000.0 / (Hauptfenster.Vorschub * Hauptfenster.VorschubProzent * Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
					nanos = System.nanoTime();
				}
			}
		}
	}
	
	/**
	 * Berechnet einen Kreisbogen von der aktuellen Position um den Mittelpunkt zum Endpunkt
	 * @param EndeX x-Koordinate des Endpunkts
	 * @param EndeY y-Koordinate des Endpunkts
	 * @param MitteX x-Koordinate des Mittelpunkts
	 * @param MitteY y-Koordinate des Mittelpunkts
	 * @param Uhrzeigersinn Richtung (True: im Uhrzeigersinn, False: gegen den Uhrzeigersinn)
	 */
	public void Kreisinterpolation(double EndeX, double EndeY, double MitteX, double MitteY, boolean Uhrzeigersinn) {
		/*
		 * Koordinatenquadranten
		 * Nullpunkt = kreismittelpunkt
		 * 
		 * 		  |
		 *   (1)  |	 (1)
		 * -------|-------
		 *   (2)  |  (2)
		 *		  |  
		 */
		double StartX = 0;
		double StartY = 0;
		double Startwinkel = arctan((xAchse.getKoordinateRelativ()-MitteX),(yAchse.getKoordinateRelativ()-MitteY));
		double Endwinkel = arctan((EndeX-MitteX),(EndeY-MitteY));
		double Radius = Math.sqrt((EndeX-MitteX)*(EndeX-MitteX) + (EndeY-MitteY)*(EndeY-MitteY));
		
		if(Uhrzeigersinn) {
			if(Hauptfenster.Korrektur == 0) {
				StartX = xAchse.getKoordinateRelativ();
				StartY = yAchse.getKoordinateRelativ();
			} else if(Hauptfenster.Korrektur == 1) {
				Radius += Double.parseDouble(Hauptfenster.Werkzeugdaten[Hauptfenster.Tool - 1][3]) / 2.0;
				StartX = Math.cos(Startwinkel)*Radius;
				StartY = Math.sin(Startwinkel)*Radius;
				EndeX = Math.cos(Endwinkel)*Radius;
				EndeY = Math.sin(Endwinkel)*Radius;
			} else {
				Radius -= Double.parseDouble(Hauptfenster.Werkzeugdaten[Hauptfenster.Tool - 1][3]) / 2.0;
				StartX = Math.cos(Startwinkel)*Radius;
				StartY = Math.sin(Startwinkel)*Radius;
				EndeX = Math.cos(Endwinkel)*Radius;
				EndeY = Math.sin(Endwinkel)*Radius;
			}
		} else {
			if(Hauptfenster.Korrektur == 0) {
				StartX = xAchse.getKoordinateRelativ();
				StartY = yAchse.getKoordinateRelativ();
			} else if(Hauptfenster.Korrektur == 1) {
				Radius -= Double.parseDouble(Hauptfenster.Werkzeugdaten[Hauptfenster.Tool - 1][3]) / 2.0;
				StartX = Math.cos(Startwinkel)*Radius;
				StartY = Math.sin(Startwinkel)*Radius;
				EndeX = Math.cos(Endwinkel)*Radius;
				EndeY = Math.sin(Endwinkel)*Radius;
			} else {
				Radius += Double.parseDouble(Hauptfenster.Werkzeugdaten[Hauptfenster.Tool - 1][3]) / 2.0;
				StartX = Math.cos(Startwinkel)*Radius;
				StartY = Math.sin(Startwinkel)*Radius;
				EndeX = Math.cos(Endwinkel)*Radius;
				EndeY = Math.sin(Endwinkel)*Radius;
			}
		}
		
		if(Hauptfenster.Korrektur != 0) {
			Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(StartX, StartY, zAchse.getKoordinateRelativ()), false);
		}
		
		//TODO Kreisalgorithmus
		
		int Startquadrant = 0;
		int Endquadrant = 0;

		Startquadrant = Quadrant(StartX, StartY, MitteX, MitteY);
		Endquadrant = Quadrant(EndeX, EndeY, MitteX, MitteY);

		if (Uhrzeigersinn) { 
			if (Startquadrant == 1 && Endquadrant == 1) {
				if (StartX < EndeX) {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				} else {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, true);
					Quadrant2Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, true);
					Quadrant1Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				}   			
			} else if (Startquadrant == 2 && Endquadrant == 2) {
				if (StartX < EndeX) {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, true);
					Quadrant1Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, true);
					Quadrant2Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				} else {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, true);
				}    			
			} else if (Startquadrant == 1 && Endquadrant == 2) {
				Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, true);
				Quadrant2Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
			} else if (Startquadrant == 2 && Endquadrant == 1) {
				Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, true);
				Quadrant1Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, true);
			}    		
		} else {    		
			if (Startquadrant == 1 && Endquadrant == 1) {
				if (StartX < EndeX) {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, false);
					Quadrant2Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, false);
					Quadrant1Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				} else {
					Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				}   			
			} else if (Startquadrant == 2 && Endquadrant == 2) {
				if (StartX < EndeX) {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				} else {
					Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, false);
					Quadrant1Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, false);
					Quadrant2Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
				}			
			} else if (Startquadrant == 1 && Endquadrant == 2) {
				Quadrant1Zeichnen(StartX, StartY, MitteX, MitteY, MitteX - Radius, MitteY, Radius, false);
				Quadrant2Zeichnen(MitteX - Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
			} else if (Startquadrant == 2 && Endquadrant == 1) {
				Quadrant2Zeichnen(StartX, StartY, MitteX, MitteY, MitteX + Radius, MitteY, Radius, false);
				Quadrant1Zeichnen(MitteX + Radius, MitteY, MitteX, MitteY, EndeX, EndeY, Radius, false);
			}
		}
	}
	
	/**
	 * Berechnet den Quadrant des Kreispunktes
	 * @param PunktX x-Koordinate des Kreispunktes
	 * @param PunktY y-Koordinate des Kreispunktes
	 * @param MitteX x-Koordinate des Kreismittelpunktes
	 * @param MitteY y-Koordinate des Kreismittelpunktes
	 * @return Quadrant des Kreispunktes
	 */
	private int Quadrant(double PunktX, double PunktY, double MitteX, double MitteY) {
		if (PunktY>=MitteY) {
			return 1;
		} else {
			return 2;
		}
	}

	/**
	 * Führt die Kreisbahn in der oberen Halbebene durch
	 * @param StartX x-Koordinate des Startpunkts
	 * @param StartY y-Koordinate des Startpunkts 
	 * @param MitteX x-Koordinate des Mittelpunkts
	 * @param MitteY y-Koordinate des Mittelpunkts
	 * @param EndeX x-Koordinate des Endpunkts
	 * @param EndeY y-Koordinate des Endpunkts
	 * @param Radius Radius des Kreises
	 * @param Uhrzeigersinn Richtung (True: im Uhrzeigersinn, False: gegen den Uhrzeigersinn)
	 */
	private void Quadrant1Zeichnen(double StartX, double StartY, double MitteX, double MitteY, double EndeX, double EndeY, double Radius, boolean Uhrzeigersinn) {

		Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(StartX, StartY, zAchse.getKoordinateRelativ()), false);
		if (StartX <= EndeX) {
			for (double i = StartX - MitteX; i <= EndeX - MitteX; i+=0.0025) {
				if(Thread.currentThread().isInterrupted()) {
					break;
				}
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(i + MitteX, MitteY + Math.sqrt(Radius * Radius - i * i), zAchse.getKoordinateRelativ()), false);
			}   	
		} else {
			for (double i = StartX - MitteX; i >= EndeX - MitteX; i-=0.0025) {
				if(Thread.currentThread().isInterrupted()) {
					break;
				}
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(i + MitteX, MitteY + Math.sqrt(Radius * Radius - i * i), zAchse.getKoordinateRelativ()), false);
			}   	
		}
		Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(EndeX, EndeY, zAchse.getKoordinateRelativ()), false);
	}

	/**
	 * Führt die Kreisbahn in der unteren Halbebene durch
	 * @param StartX x-Koordinate des Startpunkts
	 * @param StartY y-Koordinate des Startpunkts 
	 * @param MitteX x-Koordinate des Mittelpunkts
	 * @param MitteY y-Koordinate des Mittelpunkts
	 * @param EndeX x-Koordinate des Endpunkts
	 * @param EndeY y-Koordinate des Endpunkts
	 * @param Radius Radius des Kreises
	 * @param Uhrzeigersinn Richtung (True: im Uhrzeigersinn, False: gegen den Uhrzeigersinn)
	 */
	private void Quadrant2Zeichnen(double StartX, double StartY, double MitteX, double MitteY, double EndeX, double EndeY, double Radius, boolean Uhrzeigersinn) {
		Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(StartX, StartY, zAchse.getKoordinateRelativ()), false);
		if (StartX <= EndeX) {
			for (double i = StartX - MitteX; i <= EndeX - MitteX; i+=0.0025) {
				if(Thread.currentThread().isInterrupted()) {
					break;
				}
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(i + MitteX, MitteY - Math.sqrt(Radius * Radius - i * i), zAchse.getKoordinateRelativ()), false);
			}   	
		} else {
			for (double i = StartX - MitteX; i >= EndeX - MitteX; i-=0.0025) {
				if(Thread.currentThread().isInterrupted()) {
					break;
				} 
				Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(i + MitteX, MitteY - Math.sqrt(Radius * Radius - i * i), zAchse.getKoordinateRelativ()), false);
			}   	
		}
		Gerade(new Vektor(xAchse.getKoordinateRelativ(), yAchse.getKoordinateRelativ(), zAchse.getKoordinateRelativ()), new Vektor(EndeX, EndeY, zAchse.getKoordinateRelativ()), false);
	}

	/**
	 * Berechnet den Arcustangens auf dem Einheitskreis
	 * @param x x-Koordinate des Punktes
	 * @param y y-Koordinate des Punktes
	 * @return Winkel des Punktes im Bogenmaß
	 */
	private double arctan(double x, double y) {
		double Winkel = 0;
		if(x >= 0 && y >= 0) { //1.Quadrant
			Winkel = Math.atan(y/x);
		} else if(x < 0 && y >= 0) { //2.Quadrant
			Winkel = Math.PI + Math.atan(y/x);
		} else if(x < 0 && y < 0) { //3.Quadrant
			Winkel = Math.PI + Math.atan(y/x);
		} else if(x >= 0 && y < 0) { //4.Quadrant
			Winkel = 2.0*Math.PI + Math.atan(y/x);
		}
		return Winkel;
	}

	/**
	 * Berechnet einen Kreisbogen von der aktuellen Position um den Mittelpunkt mit dem angegebenen Winkel
	 * @param MitteX x-Koordinate des Mittelpunkts
	 * @param MitteY y-Koordinate des Mittelpunkts
	 * @param Winkel Winkel den der Kreisbogen einschließt
	 * @param Uhrzeigersinn Richtung (True: im Uhrzeigersinn, False: gegen den Uhrzeigersinn)
	 */
	public void Kreisinterpolation(double MitteX, double MitteY, double Winkel, boolean Uhrzeigersinn) {
		double StartX = xAchse.getKoordinateRelativ();
		double StartY = yAchse.getKoordinateRelativ();
		double Radius = Math.sqrt((StartX-MitteX)*(StartX-MitteX) + (StartY-MitteY)*(StartY-MitteY));
		double Startwinkel=arctan((StartX-MitteX),(StartY-MitteY));
		
		double Endwinkel;
		if(Uhrzeigersinn) {
			Endwinkel = Startwinkel - Winkel;
		} else {
			Endwinkel = Startwinkel + Winkel;
		}
		
		if(Endwinkel >= 2.0*Math.PI) {
			Endwinkel -= 2.0*Math.PI;
		} else if (Endwinkel >= -2.0*Math.PI) {
			Endwinkel += 2.0*Math.PI;
		}
		
		double EndeX = Math.cos(Endwinkel)*Radius;
		double EndeY = Math.sin(Endwinkel)*Radius;
		
		Kreisinterpolation(EndeX, EndeY, MitteX, MitteY, Uhrzeigersinn);
	}
	
	/**
	 * Dreht die A-Achse zum angegebenen Winkel
	 * @param EndeA Endwinkel
	 */
	public void Drehen(double EndeA) {
		double Länge;
		if(Hauptfenster.Maß == 0) {
			Länge = EndeA - aAchse.getKoordinateRelativ();
		} else {
			Länge = EndeA;
		}
		
		aAchse.setRichtung((Länge >= 0) ? true : false);
		aAchse.setSchrittweite(false);
		
		int Schrittzahl = (int) Math.abs(Länge / 0.9);
		long nanos = 0;
		
		for(int i=0; i < Schrittzahl; i++) {			
			nanos = System.nanoTime();
			if(Thread.currentThread().isInterrupted()) {
				break;
			}
			try {
				aAchse.doMultipleStep((Länge >= 0) ? true : false, false);
			} catch (EndschalterException e) {
				e.printStackTrace();
			}
			sleep((long) (6000.0 / (Hauptfenster.Vorschub * Hauptfenster.VorschubProzent * Schrittmotor.SchrittzahlHalb) * 1000000000.0) -  System.nanoTime() + nanos);
		}
	}

	/**
	 * Berechnet den Korrekturpunkt auf der rechten Seiten
	 * @param Punkt Punkt der korrigiert werden soll in Vektorform
	 * @param Fräsrichtung Richtung in die gefräst wird
	 * @return korrigierter Punkt in Vektorform
	 */
	private Vektor KorrekturpunktRechts(Vektor Punkt, Vektor Fräsrichtung) {
		Vektor Korrekturpunkt = new Vektor();
		Vektor Korrekturrichtung = new Vektor(Fräsrichtung.getY(), (-1) * Fräsrichtung.getX(), 0);
		if(Vektor.Kreuzprodukt(Fräsrichtung, Korrekturrichtung).getZ() > 0) {
			Korrekturrichtung.invertieren();
		}
		Korrekturrichtung.skalieren(Double.parseDouble(Hauptfenster.Werkzeugdaten[Hauptfenster.Tool - 1][3]) / 2.0);
		Korrekturpunkt.setX(Punkt.getX() + Korrekturrichtung.getX());
		Korrekturpunkt.setY(Punkt.getY() + Korrekturrichtung.getY());
		Korrekturpunkt.setZ(Punkt.getZ() + Korrekturrichtung.getZ());
		return Korrekturpunkt;
	}
	
	/**
	 * Berechnet den Korrekturpunkt auf der linken Seiten
	 * @param Punkt Punkt der korrigiert werden soll in Vektorform
	 * @param Fräsrichtung Richtung in die gefräst wird
	 * @return korrigierter Punkt in Vektorform
	 */
	private Vektor KorrekturpunktLinks(Vektor Punkt, Vektor Fräsrichtung) {
		Vektor Korrekturpunkt = new Vektor();
		Vektor Korrekturrichtung = new Vektor((-1) * Fräsrichtung.getY(), Fräsrichtung.getX(), 0);
		if(Vektor.Kreuzprodukt(Fräsrichtung, Korrekturrichtung).getZ() < 0) {
			Korrekturrichtung.invertieren();
		}
		Korrekturrichtung.skalieren(Double.parseDouble(Hauptfenster.Werkzeugdaten[Hauptfenster.Tool - 1][3]) / 2.0);
		Korrekturpunkt.setX(Punkt.getX() + Korrekturrichtung.getX());
		Korrekturpunkt.setY(Punkt.getY() + Korrekturrichtung.getY());
		Korrekturpunkt.setZ(Punkt.getZ() + Korrekturrichtung.getZ());
		return Korrekturpunkt;
	}
	
	/**
	 * Unterbricht das Programm um die angegebene Zeit
	 * @param nanos Dauer der Unterbrechnung in Nanosekunden
	 */
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

	/**
	 * Gibt die Spindelinstanz zurück
	 * @return Spindel
	 */
	public Spindel getSpindel() {
		return Spindel;
	}
	
	/**
	 * Gibt die Instanz der X-Achse zurück
	 * @return X-Achse
	 */
	public Achse getxAchse() {
		return xAchse;
	}
	
	/**
	 * Gibt die Instanz der Y-Achse zurück
	 * @return Y-Achse
	 */
	public Achse getyAchse() {
		return yAchse;
	}
	
	/**
	 * Gibt die Instanz der Z-Achse zurück
	 * @return Z-Achse
	 */
	public Achse getzAchse() {
		return zAchse;
	}
	
	/**
	 * Gibt die Instanz der A-Achse zurück
	 * @return A-Achse
	 */
	public Achse getaAchse() {
		return aAchse;
	}
}


class Vektor {
	private double X=0.0, Y=0.0, Z=0.0;
	
	public Vektor() {
		
	}
	
	public Vektor(double X, double Y, double Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}
	
	public double getX() {
		return X;
	}
	
	public void setX(double X) {
		this.X = X;
	}
	
	public double getY() {
		return Y;
	}
	
	public void setY(double Y) {
		this.Y = Y;
	}
	
	public double getZ() {
		return Z;
	}
	
	public void setZ(double Z) {
		this.Z = Z;
	}

	public void invertieren() {
		X = -X;
		Y = -Y;
		Z = -Z;
	}
	
	public void skalieren(double Länge) {
		multiplizieren(Länge / Länge());
	}
	
	public void normieren() {
		multiplizieren(1 / Länge());
	}
	
	public double Länge() {
		return Math.sqrt(X*X + Y*Y + Z*Z);
	}
	
	public double max() {
		double max = 0;
		max = ((Math.abs(X) >= Math.abs(Y)) ? Math.abs(X) : Math.abs(Y));
		max = ((Math.abs(Z) >= max) ? Math.abs(Z) : max);
		return max;
	}
	
	public void multiplizieren(double Faktor) {
		X *= Faktor;
		Y *= Faktor;
		Z *= Faktor;
	}
	
	public boolean istgleich(Vektor Vektor) {
		boolean Ergebnis = true;
		if(X != Vektor.getX()) {
			Ergebnis = false;
		}
		if(Y != Vektor.getY()) {
			Ergebnis = false;
		}
		if(Z != Vektor.getZ()) {
			Ergebnis = false;
		}
		return Ergebnis;
	}
	
	public static Vektor Kreuzprodukt(Vektor a, Vektor b) {
		Vektor Kreuzprodukt = new Vektor();
		Kreuzprodukt.setX(a.getY() * b.getZ() - a.getZ() * b.getY());
		Kreuzprodukt.setY(a.getZ() * b.getX() - a.getX() * b.getZ());
		Kreuzprodukt.setZ(a.getX() * b.getY() - a.getY() * b.getX());
		return Kreuzprodukt;
	}

	public static double Skalarprodukt(Vektor a, Vektor b) {
		return (a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ());
	}
}






