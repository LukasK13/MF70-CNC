package lk1311;

import com.pi4j.io.gpio.*;

/**
 * Thread zur Steuerung der Fräsmaschine
 * @author Lukas Klass - 30.06.2016
 *
 */
public class SatzFräsThread extends Thread {
	private Hauptfenster Hauptfenster;
	private String Action = "";
	private boolean interrupted = false;
	private GCodeInterpreter GCodeInterpreter;

	public SatzFräsThread(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;		
		GCodeInterpreter = new GCodeInterpreter(Hauptfenster);
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
				GCodeInterpreter.CodeInterpretieren(Action.split("\n"));
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