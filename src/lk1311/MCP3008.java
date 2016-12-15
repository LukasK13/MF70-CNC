package lk1311;

import java.util.*;

import com.pi4j.io.gpio.*;

/**
 * Stellt Methoden für das Messen von Spannungen mit einem MCP3008 via SPI bereit.
 * @author Lukas Klass - 09.03.2015
 * @version 1.0
 */
class MCP3008Standard {
	private GpioPinDigitalOutput CS;
	private GpioPinDigitalOutput SCLK;
	private GpioPinDigitalOutput MOSI;
	private GpioPinDigitalInput MISO;

	/**
	 * Erstellt eine Neues Instanz eines MCP3008
	 * @param CS Chip Select Port
	 * @param SCLK Clock Port
	 * @param MOSI Master Out Slave In Port
	 * @param MISO Master In Slave Out Port
	 */
	public MCP3008Standard(GpioPinDigitalOutput CS, GpioPinDigitalOutput SCLK, GpioPinDigitalOutput MOSI, GpioPinDigitalInput MISO) {
		this.CS = CS;
		this.SCLK = SCLK;
		this.MOSI = MOSI;
		this.MISO = MISO;
	}

	/**
	 * getValueInt
	 * Misst die Spannung die an einem Kanal anliegt
	 * @param Kanal Nummer des Kanals dessen Spannung eingelesen werden soll
	 * @return Gibt die gemessene Spannung binär (10 Bit)zurück	
	 */
	public int getValueInt(int Kanal) {
		int ADC_Wert = 0;

		CS.setState(PinState.HIGH);	//MCP3008 mit fallender Flanke wählen
		CS.setState(PinState.LOW);
		SCLK.setState(PinState.LOW);	

		byte Senden = (byte) (Kanal | 0b00011000);	//Kanalwahl: 0 0 0 Start(1) Modus(Einzel/Differenz) Kanal_2 Kanal_1 Kanal_0

		for(int i=0; i<5; i++) {		
			if((Senden & 0x10) != 0) {
				MOSI.setState(PinState.HIGH);
			} else {
				MOSI.setState(PinState.LOW);
			}
			SCLK.setState(PinState.HIGH);
			SCLK.setState(PinState.LOW);
			Senden <<= 1;
		}

		for(int i=0; i<11; i++) {
			SCLK.setState(PinState.HIGH);
			SCLK.setState(PinState.LOW);
			ADC_Wert <<= 1;
			if(MISO.isHigh()) {
				ADC_Wert |= 0x01;
			}
		}
		return ADC_Wert;
	}

	/**
	 * getValueDouble
	 * Misst die Spannung die an einem Kanal anliegt
	 * @param Kanal Nummer des Kanals dessen Spannung eingelesen werden soll
	 * @return Gibt die gemessene Spannung als Fließkommazahl zurück	
	 */
	public double getValueDouble(int Kanal) {
		double Spannung = (double) getValueInt(Kanal) / 1024.0 * 3.3;
		return Spannung;
	}
}

/**
 * Standardmodell für einen MCP3008-Listener
 * @author Lukas Klass - 16.03.2015
 *
 */
interface MCP3008Listener extends EventListener{
	public void ValueChanged(MCP3008Event e);
}

/**
 * Event, das bei einer Spannungsänderung größer als Toleranz entsteht
 * @author Lukas Klass - 16.03.2015
 *
 */
class MCP3008Event extends EventObject {
	private int Kanal;
	private int Value;

	/**
	 * Erzeugt ein neues Event
	 * @param source Objekt, das das Event ausgelöst hat
	 * @param Kanal Kanal des MCP3008 an dem die Spannung geändert wurde
	 * @param Value Neuer Wert der Spannung
	 */
	public MCP3008Event(Object source, int Kanal, int Value) {
		super(source);
		this.Kanal = Kanal;
		this.Value = Value;
	}

	/**
	 * Gibt den dem Event zugeordneten neuen Spannungswert zurück
	 * @return Neuer Spannungswert
	 */
	public int getValue() {
		return Value;
	}

	/**
	 * Gibt den dem Event zugeordneten Kanal zurück
	 * @return Kanl an dem die Spannung geändert wurde
	 */
	public int getChannel() {
		return Kanal;
	}
}

/**
 * Stellt Methoden für das Messen von Spannungen mit einem MCP3008 via SPI und Listenern bereit.
 * @author Lukas Klass - 16.03.2015
 * @version 1.0
 */
class MCP3008 implements Runnable {
	private GpioPinDigitalOutput CS;
	private GpioPinDigitalOutput SCLK;
	private GpioPinDigitalOutput MOSI;
	private GpioPinDigitalInput MISO;

	private List<MCP3008Listener> listeners;
	private boolean terminate = false;
	private boolean SendEvent = false;
	private int[] Values = new int[8];
	private int Toleranz = 5;

	/**
	 * Erstellt eine Neues Instanz eines MCP3008 mit Listener
	 * @param CS Chip Select Port
	 * @param SCLK Clock Port
	 * @param MOSI Master Out Slave In Port
	 * @param MISO Master In Slave Out Port
	 */
	public MCP3008(GpioPinDigitalOutput CS, GpioPinDigitalOutput SCLK, GpioPinDigitalOutput MOSI, GpioPinDigitalInput MISO) {
		super();
		this.CS = CS;
		this.SCLK = SCLK;
		this.MOSI = MOSI;
		this.MISO = MISO;

		listeners = new ArrayList<MCP3008Listener>();

		Thread MCP3008Thread = new Thread(this);
		MCP3008Thread.setName("MCP3008 Thread");
		MCP3008Thread.start();
	}

	/**
	 * getValueInt
	 * Misst die Spannung die an einem Kanal anliegt
	 * @param Kanal Nummer des Kanals dessen Spannung eingelesen werden soll
	 * @return Gibt die gemessene Spannung binär (10 Bit)zurück	
	 */
	public int getValueInt(int Kanal) {
		int ADC_Wert = 0;

		CS.setState(PinState.HIGH);	//MCP3008 mit fallender Flanke wählen
		CS.setState(PinState.LOW);
		SCLK.setState(PinState.LOW);	

		byte Senden = (byte) (Kanal | 0b00011000);	//Kanalwahl: 0 0 0 Start(1) Modus(Einzel/Differenz) Kanal_2 Kanal_1 Kanal_0

		for(int i=0; i<5; i++) {		
			if((Senden & 0x10) != 0) {
				MOSI.setState(PinState.HIGH);
			} else {
				MOSI.setState(PinState.LOW);
			}
			SCLK.setState(PinState.HIGH);
			SCLK.setState(PinState.LOW);
			Senden <<= 1;
		}

		for(int i=0; i<11; i++) {
			SCLK.setState(PinState.HIGH);
			SCLK.setState(PinState.LOW);
			ADC_Wert <<= 1;
			if(MISO.isHigh()) {
				ADC_Wert |= 0x01;
			}
		}
		return ADC_Wert;
	}

	/**
	 * getValueDouble
	 * Misst die Spannung die an einem Kanal anliegt
	 * @param Kanal Nummer des Kanals dessen Spannung eingelesen werden soll
	 * @return Gibt die gemessene Spannung als Fließkommazahl zurück	
	 */
	public double getValueDouble(int Kanal) {
		double Spannung = (double) getValueInt(Kanal) / 1024.0 * 3.3;
		return Spannung;
	}

	public void run() {
		terminate = false;
		int temp;

		while (!terminate) {     
			for(int i=0; i<8; i++) {
				temp = getValueInt(i);
				if ((Math.abs(temp - Values[i]) > Toleranz) || SendEvent) {
					for(int j = 0, size = listeners.size(); j < size; j++) {
						listeners.get(j).ValueChanged(new MCP3008Event(this, i, temp));
					}
					Values[i] = temp;
				}
			}	
			SendEvent = false;
			try {
				Thread.currentThread();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}

	/**
	 * Registriert einen neuen MCP3008-Listener
	 * @param listener MCP3008-Listener der hinzugefügt werden soll
	 */
	public void addMCP3008Listener(MCP3008Listener listener) {
		listeners.add(listener);
	}

	/**
	 * Entfernt einen registrierten MCP3008-Listener
	 * @param listener MCP3008-Listener der entfernt werden soll
	 */
	public void removeMCP3008Listener(MCP3008Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * Stoppt den ListenerThread
	 */
	public void stop() {
		terminate = true;
	}

	public void SendEvent() {
		SendEvent = true;
	}
}
