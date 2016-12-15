package lk1311;

public class Update extends Thread{

	private Hauptfenster Hauptfenster = null;
	private Segment7Anzeige xSegment = null, ySegment, zSegment, aSegment;
	private JNumberField XNum, YNum, ZNum, ANum;

	public Update(Hauptfenster Hauptfenster, Segment7Anzeige xSegmentNeu, Segment7Anzeige ySegmentNeu, Segment7Anzeige zSegmentNeu, Segment7Anzeige aSegmentNeu,
			JNumberField XNumNeu, JNumberField YNumNeu, JNumberField ZNumNeu, JNumberField ANumNeu) {
		this.Hauptfenster = Hauptfenster;
		xSegment = xSegmentNeu;
		ySegment = ySegmentNeu;
		zSegment = zSegmentNeu;
		aSegment = aSegmentNeu;
		XNum = XNumNeu;
		YNum = YNumNeu;
		ZNum = ZNumNeu;
		ANum = ANumNeu;
	}

	public void run() {
		setName("UpdateThread");
		while(!Thread.interrupted()) {
			try {
				xSegment.setValue(Round(Hauptfenster.CNC.getxAchse().getKoordinateRelativ()));
				ySegment.setValue(Round(Hauptfenster.CNC.getyAchse().getKoordinateRelativ()));
				zSegment.setValue(Round(Hauptfenster.CNC.getzAchse().getKoordinateRelativ()));
				aSegment.setValue(Round(Hauptfenster.CNC.getaAchse().getKoordinateRelativ()));
				XNum.setDouble(Round(Hauptfenster.CNC.getxAchse().getKoordinateAbsolut()));
				YNum.setDouble(Round(Hauptfenster.CNC.getyAchse().getKoordinateAbsolut()));
				ZNum.setDouble(Round(Hauptfenster.CNC.getzAchse().getKoordinateAbsolut()));
				ANum.setDouble(Round(Hauptfenster.CNC.getaAchse().getKoordinateAbsolut()));
			} catch(NullPointerException e) {
			}

			try {
				Thread.currentThread();
				Thread.sleep(180);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private double Round(double Number) {
		return (Math.round(Number * 100.0)) / 100.0;
	}
}
