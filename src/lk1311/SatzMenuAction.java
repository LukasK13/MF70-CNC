package lk1311;

import javax.swing.JButton;

class FräsenStarten implements MenuFunc {
	private Hauptfenster Hauptfenster;
	
	public FräsenStarten(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Satz.SatzFräsThread.setAction(Hauptfenster.Satz.Editor.getText());
	}
}

class FräsenStoppen implements MenuFunc {
	private Hauptfenster Hauptfenster;
	
	public FräsenStoppen(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.Satz.SatzFräsThread.interrupt();
		Hauptfenster.CNC.getSpindel().setState(false);
	}
}