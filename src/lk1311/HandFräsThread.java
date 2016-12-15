package lk1311;

import com.pi4j.io.gpio.*;

/**
 * Thread zur Steuerung der Fräsmaschine
 * @author Lukas Klass - 30.06.2016
 *
 */
public class HandFräsThread extends Thread {
	private Hauptfenster Hauptfenster;
	private String Action = "";
	private boolean interrupted = false;
	private Achse xAchse, yAchse, zAchse, aAchse;
	private GCodeInterpreter GCodeInterpreter;
	private int VorschubVerfahren = 80;

	public HandFräsThread(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;		
		GCodeInterpreter = new GCodeInterpreter(Hauptfenster);
		xAchse = Hauptfenster.CNC.getxAchse();
		yAchse = Hauptfenster.CNC.getyAchse();
		zAchse = Hauptfenster.CNC.getzAchse();
		aAchse = Hauptfenster.CNC.getaAchse();
	}

	public void run() {		
		while(!interrupted) {
			if(Hauptfenster.Notaus.getState() == PinState.HIGH) {
				Thread.currentThread().interrupt();
			}
			if(Hauptfenster.SteuerspannungEin) {
				if(!Action.equals("")) {
					Hauptfenster.Beschäftigt = true;
				}
				switch (Action) {
				case "xReferenz":
					xAchse.Referenz();
					break;
				case "yReferenz":
					try {
						yAchse.ReferenzPlus();
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					yAchse.setReferenzNull();
					yAchse.VerfahrenOhneEndschalter(false, true, 100, 5);
					break;
				case "zReferenz":
					zAchse.Referenz();
					break;
				case "aReferenz":
					aAchse.Referenz();
					break;
				case "alleReferenz":
					xAchse.Referenz();
					try {
						yAchse.ReferenzPlus();
					} catch (EndschalterException e) {
						e.printStackTrace();
					}
					yAchse.setReferenzNull();
					yAchse.VerfahrenOhneEndschalter(false, true, 100, 5);
					zAchse.Referenz();
					break;
				case "xNullen":
					xAchse.setNullpunkt();
					break;
				case "yNullen":
					yAchse.setNullpunkt();
					break;
				case "zNullen":
					zAchse.setNullpunkt();
					break;
				case "alleNullen":
					xAchse.setNullpunkt();
					yAchse.setNullpunkt();
					zAchse.setNullpunkt();
					break;
				case "VerfahrenXPlus":
					if(Hauptfenster.Hand.SchrittweiteVerfahren == 10) {
						try {
							xAchse.Verfahren(true, VorschubVerfahren, 0.01);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 100) {
						try {
							xAchse.Verfahren(true, VorschubVerfahren, 0.1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 1000) {
						try {
							xAchse.Verfahren(true, VorschubVerfahren, 1.0);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}		
					break;
				case "VerfahrenXMinus":
					if(Hauptfenster.Hand.SchrittweiteVerfahren == 10) {
						try {
							xAchse.Verfahren(false, VorschubVerfahren, 0.01);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 100) {
						try {
							xAchse.Verfahren(false, VorschubVerfahren, 0.1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 1000) {
						try {
							xAchse.Verfahren(false, VorschubVerfahren, 1.0);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}	
					break;
				case "VerfahrenYPlus":
					if(Hauptfenster.Hand.SchrittweiteVerfahren == 10) {
						try {
							yAchse.Verfahren(true, VorschubVerfahren, 0.01);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 100) {
						try {
							yAchse.Verfahren(true, VorschubVerfahren, 0.1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 1000) {
						try {
							yAchse.Verfahren(true, VorschubVerfahren, 1.0);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}		
					break;
				case "VerfahrenYMinus":
					if(Hauptfenster.Hand.SchrittweiteVerfahren == 10) {
						try {
							yAchse.Verfahren(false, VorschubVerfahren, 0.01);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 100) {
						try {
							yAchse.Verfahren(false, VorschubVerfahren, 0.1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 1000) {
						try {
							yAchse.Verfahren(false, VorschubVerfahren, 1.0);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}	
					break;
				case "VerfahrenZPlus":
					if(Hauptfenster.Hand.SchrittweiteVerfahren == 10) {
						try {
							zAchse.Verfahren(true, VorschubVerfahren, 0.01);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 100) {
						try {
							zAchse.Verfahren(true, VorschubVerfahren, 0.1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 1000) {
						try {
							zAchse.Verfahren(true, VorschubVerfahren, 1.0);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}		
					break;
				case "VerfahrenZMinus":
					if(Hauptfenster.Hand.SchrittweiteVerfahren == 10) {
						try {
							zAchse.Verfahren(false, VorschubVerfahren, 0.01);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 100) {
						try {
							zAchse.Verfahren(false, VorschubVerfahren, 0.1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 1000) {
						try {
							zAchse.Verfahren(false, VorschubVerfahren, 1.0);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}	
					break;
				case "VerfahrenAPlus":
					if(Hauptfenster.Hand.SchrittweiteVerfahrenA == 1) {
						try {
							aAchse.Drehen(true, VorschubVerfahren, 1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahrenA == 5) {
						try {
							aAchse.Drehen(true, VorschubVerfahren, 5);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}	
					break;
				case "VerfahrenAMinus":
					if(Hauptfenster.Hand.SchrittweiteVerfahrenA == 1) {
						try {
							aAchse.Drehen(false, VorschubVerfahren, 1);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					} else if(Hauptfenster.Hand.SchrittweiteVerfahrenA == 5) {
						try {
							aAchse.Drehen(false, VorschubVerfahren, 5);
						} catch (EndschalterException e) {
							e.printStackTrace();
						}
					}	
					break;
				case "VerfahrenXPlusOhne":
					xAchse.VerfahrenOhneEndschalter(true, false, 50, 0.01);
					break;
				case "VerfahrenXMinusOhne":
					xAchse.VerfahrenOhneEndschalter(false, false, 50, 0.01);
					break;
				case "VerfahrenYPlusOhne":
					yAchse.VerfahrenOhneEndschalter(true, false, 50, 0.01);
					break;
				case "VerfahrenYMinusOhne":
					yAchse.VerfahrenOhneEndschalter(false, false, 50, 0.01);
					break;
				case "VerfahrenZPlusOhne":
					zAchse.VerfahrenOhneEndschalter(true, false, 50, 0.01);
					break;
				case "VerfahrenZMinusOhne":
					zAchse.VerfahrenOhneEndschalter(false, false, 50, 0.01);
					break;
				case "VerfahrenAPlusOhne":
					aAchse.DrehenOhneEndschalter(true, 50, 1);
					break;
				case "VerfahrenAMinusOhne":
					aAchse.DrehenOhneEndschalter(false, 50, 1);
					break;
				case "AntastenXPlus":
					break;
				case "AntastenXMinus":
					break;
				case "AntastenYPlus":
					break;
				case "AntastenYMinus":
					break;
				case "AntastenZPlus":
					break;
				case "AntastenZMinus":
					break;
				case "":
					break;
				default:
					GCodeInterpreter.CodeInterpretieren(Action.split("\n"));
					break;
				}
			} else {
				if(!Action.equals("")) {
					System.out.println("Bitte Steuerspannung einschalten.");
				}				
			}
			interrupted();
			if(!Action.equals("")) {
				Hauptfenster.Beschäftigt = false;
				Action = "";
			}			
		}
	}

	public void setAction(String Action) {
		if(this.Action.equals("")) {
			this.Action = Action;
		}			
	}

	public void stopFräsraum() {
		interrupted = true;
	}
	
}