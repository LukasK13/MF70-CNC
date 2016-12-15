package lk1311;

public class GCodeInterpreter {
	private Hauptfenster Hauptfenster;

	public GCodeInterpreter(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void CodeInterpretieren(String[] CodeSplit) {
		int AnzahlSchleifenDurchgänge = 0;
		String[] Split = {"","","","","",""};	
		
		for (int i=0; i<CodeSplit.length; i++) {
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			
			for (int j=0; j<Split.length; j++) {	//String Split leeren
				Split[j] = "";
			}

			String[] temp = CodeSplit[i].split(" ");
	
			for (int j=0; j<temp.length; j++) {
				Split[j] = temp[j];
			}

			if (Split[1].equals("G0") | Split[1].equals("G00")) {
				//G00 X
				if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.LinearinterpolationEilgang(Double.parseDouble(Split[2].substring(1)), 0, 0);
				}

				//G00 Y
				else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.LinearinterpolationEilgang(0, Double.parseDouble(Split[2].substring(1)), 0);
				}

				//G00 Z
				if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.LinearinterpolationEilgang(0, 0, Double.parseDouble(Split[2].substring(1)));
				}

				//G00 A
				if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.Drehen(Double.parseDouble(Split[2].substring(1)));
				}

				//G00 X Y
				else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
						&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
					Hauptfenster.CNC.LinearinterpolationEilgang(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)), 0);
				}

				//G00 X Z
				else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
						&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {							
					Hauptfenster.CNC.LinearinterpolationEilgang(Double.parseDouble(Split[2].substring(1)), 0, Double.parseDouble(Split[2].substring(1)));
				}

				//G00 Y Z
				else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
						&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
					Hauptfenster.CNC.LinearinterpolationEilgang(0, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)));
				} 

				//G00 X Y Z
				else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
						&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("Z") | Split[4].startsWith("z")) && istDouble(Split[4].substring(1))) {
					Hauptfenster.CNC.LinearinterpolationEilgang(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)));
				}
			}
			
			else if (Split[1].equals("G1") | Split[1].equals("G01")) {
				//G01 X
				if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.Linearinterpolation(Double.parseDouble(Split[2].substring(1)), 0, 0);
				}

				//G01 Y
				else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.Linearinterpolation(0, Double.parseDouble(Split[2].substring(1)), 0);
				}

				//G01 Z
				if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.Linearinterpolation(0, 0, Double.parseDouble(Split[2].substring(1)));
				}

				//G01 A
				if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
					Hauptfenster.CNC.Drehen(Double.parseDouble(Split[2].substring(1)));
				}

				//G01 X Y
				else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
						&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
					Hauptfenster.CNC.Linearinterpolation(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)), 0);
				}

				//G01 X Z
				else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
						&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {							
					Hauptfenster.CNC.Linearinterpolation(Double.parseDouble(Split[2].substring(1)), 0, Double.parseDouble(Split[2].substring(1)));
				}

				//G01 Y Z
				else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z"))
						&& istDouble(Split[3].substring(1)) && Split[4].equals("")) {
					Hauptfenster.CNC.Linearinterpolation(0, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)));
				} 

				//G01 X Y Z
				else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y"))
						&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("Z") | Split[4].startsWith("z")) && istDouble(Split[4].substring(1))) {
					Hauptfenster.CNC.Linearinterpolation(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[2].substring(1)));
				}
			}

			else if (Split[1].equals("G2") | Split[1].equals("G02")) {
				if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
						&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
						&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
						&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
					Hauptfenster.CNC.Kreisinterpolation(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), Double.parseDouble(Split[5].substring(1)), true);
				} 
			}		

			else if (Split[1].equals("G3") | Split[1].equals("G03")) {
				if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
						&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
						&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
						&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
					Hauptfenster.CNC.Kreisinterpolation(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), Double.parseDouble(Split[5].substring(1)), false);
				} 
			}		

			else if (Split[1].equals("G4") | Split[1].equals("G04")) {
				if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
						&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
					Hauptfenster.CNC.Kreisinterpolation(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), true);
				} 
			}

			else if (Split[1].equals("G5") | Split[1].equals("G05")) {
				if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
						&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
					Hauptfenster.CNC.Kreisinterpolation(Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), false);
				} 
			}

			else if (Split[1].equals("G40")) {
				Hauptfenster.Korrektur=0;
			}

			else if (Split[1].equals("G41")) {
				Hauptfenster.Korrektur=1;
			}

			else if (Split[1].equals("G42")) {
				Hauptfenster.Korrektur=2;
			}

			else if (Split[1].equals("G90")) {
				Hauptfenster.Maß=0;
			}

			else if (Split[1].equals("G91")) {
				Hauptfenster.Maß=1;
			}

			else if (Split[1].equals("G72")) {
				if ((Split[2].startsWith("N") | Split[2].startsWith("n")) && istInteger(Split[2].substring(1)) && (Split[3].startsWith("W") | Split[3].startsWith("w")) && istInteger(Split[3].substring(1))) {
					if(Integer.parseInt(Split[2].substring(1)) < i) {
						if(AnzahlSchleifenDurchgänge < Integer.parseInt(Split[3].substring(1))) {
							i = Integer.parseInt(Split[2].substring(1));
							AnzahlSchleifenDurchgänge++;
						} else {
							AnzahlSchleifenDurchgänge = 0;
						}
					}				
				}
			}

			//Pause
			else if (Split[1].equals("M00") | Split[1].equals("M0")) {

			}

			//Abbruch
			else if (Split[1].equals("M02") | Split[1].equals("M2")) {

			}

			//Spindel ein
			else if (Split[1].equals("M03") | Split[1].equals("M3")) {
				Hauptfenster.CNC.getSpindel().setState(true);
			}

			//Spindel aus
			else if (Split[1].equals("M05") | Split[1].equals("M5")) {
				Hauptfenster.CNC.getSpindel().setState(false);
			}

			//Drehzahl
			else if ((Split[1].startsWith("S") | Split[1].startsWith("s")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
				if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 20000) {
					Hauptfenster.CNC.getSpindel().setDrehzahl(Integer.parseInt(Split[1].substring(1)));
				} 
			}

			//Vorschub
			else if ((Split[1].startsWith("F") | Split[1].startsWith("S")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
				Hauptfenster.Vorschub=Integer.parseInt(Split[1].substring(1));
			}

			//Drehzahl & Vorschub
			else if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istInteger(Split[2].substring(1))) {
				if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
					Hauptfenster.Vorschub=Integer.parseInt(Split[1].substring(1));
					Hauptfenster.CNC.getSpindel().setDrehzahl(Integer.parseInt(Split[2].substring(1)));
				} 
			}

			//Werkzeugwechsel
			else if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
				if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 27) {
					Hauptfenster.Tool=Integer.parseInt(Split[1].substring(1));
					System.out.println("Bitte Werkzeug " + Integer.parseInt(Split[1].substring(1)) + " einspannen.");
				} 
			}

			//Parameterausgabe
			lk1311.Hauptfenster.Error.setText("");
			if (Hauptfenster.Korrektur==0) {
				System.out.println("Werkzeugkorrektur: keine (G40)");
			} else if (Hauptfenster.Korrektur==1) {
				System.out.println("Werkzeugkorrektur: links (G41)");
			} else if (Hauptfenster.Korrektur==2) {
				System.out.println("Werkzeugkorrektur: rechts (G42)");
			}

			if (Hauptfenster.Maß==0) {
				System.out.println("Maß: Absolutmaß (G90)");
			} else if (Hauptfenster.Maß==1) {
				System.out.println("Maß: Relativmaß (G91)");
			}
		}
	}

	private boolean istDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean istInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
}
