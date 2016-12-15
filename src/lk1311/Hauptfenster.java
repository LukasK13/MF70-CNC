package lk1311;
//TODO Zoom-Funktion in Turtle integrieren
//TODO Koorekturbahn in Simulator anzeigen
//TODO Kontextmenü nach 3s öffnen
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;

public class Hauptfenster {
	
	//Hauptkomponenten
	public JFrame Fenster;
	public JSplitPane splitPaneGroß, splitPaneKlein;
	private JPanel FensterPnl;

	//Komponenten der Statusleiste
	private JPanel PanelStatus;
	private Segment7Anzeige xSegment, ySegment, zSegment, aSegment;
	private JNumberField XNum, YNum, ZNum, ANum;
	private JLabel lblDx, lblDy, lblDz, lblDa, lblx, lbly, lblz, lbla, VorschubLbl, DrehzahlLbl;
	private JButton HandBtn, ProgrammBtn, SatzBtn;
	private JScrollPane Error_Scroll;	
	private JProgressBar VorschubBar, VorschubEilBar, DrehzahlBar;
	private JPopupMenu popupMenu;
	
	//öffentliche Variablen
	public static JTextArea Error;
	public ImageIcon Oben_Aktiv, Oben_Inaktiv, Unten_Aktiv, Unten_Inaktiv, Zurück_Aktiv;	
	public boolean Beschäftigt=false;	
	public String[][] Werkzeugdaten = new String[27][4];
	public String WerkzeugdatenPfad;
	public MCP3008 ADC;
	public CNC CNC;
	
	public int VorschubProzent, VorschubEilProzent;
	public int Vorschub=100, VorschubEil = 150;
	public boolean SteuerspannungEin=false;
	/**
	 * 0 keine Radiuskorrektur
	 * 1 Radiuskorrektur links
	 * 2 Radiuskorrektur rechts
	 */
	public int Korrektur=0;
	/**
	 * 0 Absolutmaß
	 * 1 Relativmaß
	 */
	public int Maß=0;
	public int Tool=1;

	//externe Komponenten
	public Hand Hand;
	public Programm Programm;
	public Satz Satz;
	public Update Update;

	//I/O
	private GpioController gpio = GpioFactory.getInstance();  	
	private MCP23017GpioProvider MCP23017Provider = null;	
	private MCP4725GpioProvider MCP4725GpioProvider;
	public GpioPinDigitalOutput CE0;
	public GpioPinDigitalInput MISO;
	public GpioPinDigitalOutput MOSI;
	public GpioPinDigitalOutput SCLK;
	public GpioPinDigitalInput EndschalterXPlus;
	public GpioPinDigitalInput EndschalterXMinus;
	public GpioPinDigitalInput EndschalterYPlus;
	public GpioPinDigitalInput EndschalterYMinus;
	public GpioPinDigitalInput EndschalterZPlus;
	public GpioPinDigitalInput EndschalterZMinus;
	public GpioPinDigitalInput EndschalterAPlus;
	public GpioPinDigitalInput EndschalterAMinus;
	public GpioPinDigitalOutput Clock1;
	public GpioPinDigitalOutput Clock2;	
	public GpioPinDigitalOutput Clock3;
	public GpioPinDigitalOutput Clock4;
	public GpioPinDigitalOutput HF1;
	public GpioPinDigitalOutput HF2;	
	public GpioPinDigitalOutput HF3;
	public GpioPinDigitalOutput HF4;
	public GpioPinDigitalOutput CW1;
	public GpioPinDigitalOutput CW2;	
	public GpioPinDigitalOutput CW3;
	public GpioPinDigitalOutput CW4;
	public GpioPinDigitalInput Control;
	public GpioPinDigitalOutput Schrittrelais;
	public GpioPinDigitalOutput Fräsenrelais;
	public GpioPinDigitalInput Notaus;
	public GpioPinAnalogOutput SpindelOut;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Hauptfenster Hauptfenster = new Hauptfenster();
					Hauptfenster.Fenster.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Programmstart, Initialisierungsfunktionen werden aufgerufen
	 */
	public Hauptfenster() {
		Initialisieren_IO();
		Initialisieren_Frame();		
		Initialisieren_Umgebungsvariablen();
		Initialisieren_Module();
		WerkzeugdatenEinlesen();
		Initialisieren_StatusPanel();
		Initialisieren_ADC_Listener();
		Hand.setVisible(true);
		if(Notaus.getState() == PinState.HIGH) {
			System.out.println("Bitte Notaus lösen.");
		}
	}

	/**
	 * Definition des Hauptfensters
	 */
	private void Initialisieren_Frame() {	
		Fenster = new JFrame();
		Fenster.setBackground(Color.WHITE);
		Fenster.setTitle("MF70 CNC");
		Fenster.setIconImage(Toolkit.getDefaultToolkit().getImage(Hauptfenster.class.getResource("/resources/Logo.jpg")));
		Fenster.setSize(1280, 720);
		Fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FensterPnl = new JPanel();
		FensterPnl.setBackground(Color.DARK_GRAY);
		Fenster.setContentPane(FensterPnl);
		FensterPnl.setLayout(null);
		Fenster.setUndecorated(true);

		splitPaneGroß = new JSplitPane();
		splitPaneGroß.setContinuousLayout(false);
		splitPaneGroß.setEnabled(false);
		splitPaneGroß.setBackground(new Color(214, 217, 223));
		splitPaneGroß.setBounds(0, 0, 1280, 720);
		splitPaneGroß.setDividerLocation(1160);
		FensterPnl.add(splitPaneGroß);

		splitPaneKlein = new JSplitPane();
		splitPaneKlein.setEnabled(false);
		splitPaneKlein.setContinuousLayout(false);
		splitPaneKlein.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneKlein.setDividerLocation(500);
		splitPaneGroß.setLeftComponent(splitPaneKlein);
	}
	
	/**
	 * Umgebungsvariablen werden eingelesen
	 */
	private void Initialisieren_Umgebungsvariablen() {
		Oben_Aktiv=new ImageIcon(Hauptfenster.class.getResource("/resources/Oben_Aktiv.png"));
		Oben_Inaktiv=new ImageIcon(Hauptfenster.class.getResource("/resources/Oben_Inaktiv.png"));
		Unten_Aktiv=new ImageIcon(Hauptfenster.class.getResource("/resources/Unten_Aktiv.png"));
		Unten_Inaktiv=new ImageIcon(Hauptfenster.class.getResource("/resources/Unten_Inaktiv.png"));
		Zurück_Aktiv=new ImageIcon(Hauptfenster.class.getResource("/resources/Zurück_Aktiv.png"));
		
		for (int i=0; i<Werkzeugdaten.length; i++) {
			for (int j=0; j<Werkzeugdaten[0].length; j++) {
				Werkzeugdaten[i][j] = new String("");
			}
		}

		if(System.getProperty("os.name").equals("Windows 7")) {
			WerkzeugdatenPfad = new String("C:/Users/Lukas/Documents/Werkzeugdaten.mf70w"); 
		} else if(System.getProperty("os.name").equals("Linux")) {
			WerkzeugdatenPfad = new String("/home/Lukas/Werkzeugdaten.mf70w"); 
		}
	}
	
	/**
	 * Hand-, Programm- und Satzmodul werden geladen
	 */
	private void Initialisieren_Module() {
		CNC = new CNC(this);
		Hand = new Hand(this);
		Programm = new Programm(this);
		Satz = new Satz(this);
	}
	
	/**
	 * liest die Werkzeugdaten ein
	 */
	private void WerkzeugdatenEinlesen() {
		try {
			FileReader fr = new FileReader(WerkzeugdatenPfad);
			BufferedReader br = new BufferedReader(fr);
			String temp = br.readLine();
			temp = br.readLine();
			int i=0;
			while(((temp = br.readLine()) != null) && (i<Werkzeugdaten.length)) {
				String[] tempSplit = temp.split("\t");
				for(int j=0; j<Werkzeugdaten[0].length; j++) {
					if(j<tempSplit.length) {
						if(tempSplit[j] != null) {
							Werkzeugdaten[i][j] = tempSplit[j];
						}
					}					
				}
				i++;
			}
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}		    
	}
	
	/**
	 * Das Status-Panel wird geladen und der Aktualisierungsthread gestartet
	 */
	private void Initialisieren_StatusPanel() {
		PanelStatus = new JPanel();
		PanelStatus.setForeground(new Color(0, 0, 205));
		PanelStatus.setBackground(Color.LIGHT_GRAY);
		PanelStatus.setLayout(null);
		splitPaneKlein.setRightComponent(PanelStatus);	

		xSegment = new Segment7Anzeige();
		xSegment.setBounds(81, 15, 177, 36);
		xSegment.setValue(-1234.56);
		xSegment.setForeground(new Color(0, 0, 205));
		xSegment.setBackground(Color.WHITE);
		PanelStatus.add(xSegment);

		ySegment = new Segment7Anzeige();
		ySegment.setBounds(81, 62, 177, 36);
		ySegment.setValue(-1234.56);
		ySegment.setForeground(new Color(0, 0, 205));
		ySegment.setBackground(Color.WHITE);
		PanelStatus.add(ySegment);

		zSegment = new Segment7Anzeige();
		zSegment.setBounds(81, 109, 177, 36);
		zSegment.setValue(-1234.56);
		zSegment.setForeground(new Color(0, 0, 205));
		zSegment.setBackground(Color.WHITE);
		PanelStatus.add(zSegment);

		aSegment = new Segment7Anzeige();
		aSegment.setBounds(81, 156, 177, 36);
		aSegment.setValue(-1234.56);
		aSegment.setForeground(new Color(0, 0, 205));
		aSegment.setBackground(Color.WHITE);
		PanelStatus.add(aSegment);

		lblDx = new JLabel("dX");
		lblDx.setBackground(Color.LIGHT_GRAY);
		lblDx.setFont(new Font("SansSerif", Font.PLAIN, 36));
		lblDx.setBounds(25, 15, 50, 36);
		PanelStatus.add(lblDx);

		lblDy = new JLabel("dY");
		lblDy.setFont(new Font("SansSerif", Font.PLAIN, 36));
		lblDy.setBounds(25, 62, 50, 36);
		PanelStatus.add(lblDy);

		lblDz = new JLabel("dZ");
		lblDz.setFont(new Font("SansSerif", Font.PLAIN, 36));
		lblDz.setBounds(25, 109, 50, 36);
		PanelStatus.add(lblDz);

		lblDa = new JLabel("dA");
		lblDa.setFont(new Font("SansSerif", Font.PLAIN, 36));
		lblDa.setBounds(25, 156, 50, 36);
		PanelStatus.add(lblDa);

		lblx = new JLabel("X");
		lblx.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblx.setBounds(320, 15, 30, 36);
		PanelStatus.add(lblx);

		lbly = new JLabel("Y");
		lbly.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lbly.setBounds(320, 62, 30, 36);
		PanelStatus.add(lbly);

		lblz = new JLabel("Z");
		lblz.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblz.setBounds(320, 109, 30, 36);
		PanelStatus.add(lblz);

		lbla = new JLabel("A");
		lbla.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lbla.setBounds(320, 156, 30, 36);
		PanelStatus.add(lbla);

		XNum = new JNumberField();
		XNum.setEditable(false);
		XNum.setFont(new Font("SansSerif", Font.PLAIN, 20));
		XNum.setDouble(-1234.56);
		XNum.setBounds(362, 15, 122, 36);
		PanelStatus.add(XNum);
		XNum.setColumns(10);

		YNum = new JNumberField();
		YNum.setEditable(false);
		YNum.setFont(new Font("SansSerif", Font.PLAIN, 20));
		YNum.setDouble(-1234.56);
		YNum.setBounds(362, 62, 122, 36);
		PanelStatus.add(YNum);
		YNum.setColumns(10);

		ZNum = new JNumberField();
		ZNum.setEditable(false);
		ZNum.setDouble(-1234.56);
		ZNum.setFont(new Font("SansSerif", Font.PLAIN, 20));
		ZNum.setBounds(362, 109, 122, 36);
		PanelStatus.add(ZNum);
		ZNum.setColumns(10);

		ANum = new JNumberField();
		ANum.setFont(new Font("SansSerif", Font.PLAIN, 20));
		ANum.setEditable(false);
		ANum.setDouble(-1234.56);
		ANum.setBounds(362, 156, 122, 36);
		PanelStatus.add(ANum);
		ANum.setColumns(10);

		VorschubLbl = new JLabel();
		VorschubLbl.setBackground(Color.WHITE);
		VorschubLbl.setFont(new Font("SansSerif", Font.PLAIN, 17));
		VorschubLbl.setBounds(554, 166, 134, 20);
		PanelStatus.add(VorschubLbl);

		DrehzahlLbl = new JLabel();
		DrehzahlLbl.setFont(new Font("SansSerif", Font.PLAIN, 17));
		DrehzahlLbl.setBounds(554, 136, 134, 20);
		PanelStatus.add(DrehzahlLbl);

		HandBtn = new JButton("");
		HandBtn.setBackground(Color.white);
		HandBtn.setIcon(new ImageIcon(Hauptfenster.class.getResource("/resources/Hand.png")));
		HandBtn.setBounds(870, 136, 80, 50);
		HandBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Betriebsart(0);
			}
		});
		PanelStatus.add(HandBtn);

		ProgrammBtn = new JButton("");
		ProgrammBtn.setBackground(Color.gray);
		ProgrammBtn.setIcon(new ImageIcon(Hauptfenster.class.getResource("/resources/Programm.png")));
		ProgrammBtn.setBounds(962, 136, 80, 50);
		ProgrammBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Betriebsart(1);
			}
		});
		PanelStatus.add(ProgrammBtn);

		SatzBtn = new JButton("");
		SatzBtn.setBackground(Color.gray);
		SatzBtn.setIcon(new ImageIcon(Hauptfenster.class.getResource("/resources/Satzfolge.png")));
		SatzBtn.setBounds(1054, 136, 80, 50);
		SatzBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Betriebsart(2);
			}
		});
		PanelStatus.add(SatzBtn);

		Error_Scroll = new JScrollPane();
		Error_Scroll.setBounds(554, 15, 580, 111);
		PanelStatus.add(Error_Scroll);

		Error = new JTextArea();
		Error.setEditable(false);
		Error.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 525) {
					popupMenu.show(Error, 20, 20);
				}
			}
		});
		Error_Scroll.setViewportView(Error);

		popupMenu = new JPopupMenu();
		addPopup(Error, popupMenu);

		JMenuItem mntmLschen = new JMenuItem("Löschen");
		mntmLschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Error.setText("");
			}
		});
		mntmLschen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/resources/Delete.png")));
		popupMenu.add(mntmLschen);

		PrintStream ErrorStream = new PrintStream(System.out) {
			public void print(String s) {
				try {
					Error.getDocument().insertString(Error.getDocument().getLength(), s + "\n", StyleConstant.RED);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}		     
		};
		System.setErr(ErrorStream);

		PrintStream OutStream = new PrintStream(System.out) {
			public void print(String s) {
				try {
					Error.getDocument().insertString(Error.getDocument().getLength(), s + "\n", StyleConstant.BLACK);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}		     
		};
		System.setOut(OutStream);

		VorschubBar = new JProgressBar();
		VorschubBar.setBounds(700, 168, 140, 20);
		PanelStatus.add(VorschubBar);

		DrehzahlBar = new JProgressBar();
		DrehzahlBar.setBounds(700, 136, 140, 20);
		PanelStatus.add(DrehzahlBar);	

		VorschubEilBar = new JProgressBar();
		VorschubEilBar.setBounds(700, 163, 140, 10);
		PanelStatus.add(VorschubEilBar);

		JLabel TimeLbl = new JLabel("16:15");
		TimeLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		TimeLbl.setFont(new Font("SansSerif", Font.PLAIN, 17));
		TimeLbl.setBounds(1096, 0, 55, 16);
		PanelStatus.add(TimeLbl);
		new Thread(	new Runnable() {
			public void run() {
				Thread.currentThread().setName("Uhr");
				Calendar Kalender = Calendar.getInstance();
				while(true) {
					TimeLbl.setText(Kalender.get(Calendar.HOUR_OF_DAY) + ":" + Kalender.get(Calendar.MINUTE));
					TimeLbl.repaint();
					try {
						Thread.currentThread();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					TimeLbl.setText(Kalender.get(Calendar.HOUR_OF_DAY) + " " + Kalender.get(Calendar.MINUTE));
					TimeLbl.repaint();
					try {
						Thread.currentThread();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();	
		
		Update = new Update(this, xSegment, ySegment, zSegment, aSegment, XNum, YNum, ZNum, ANum);
		Update.start();
	}
	
	/**
	 * ADC-Listener wird erstellt, Deklaration der Aktionen
	 */
	private void Initialisieren_ADC_Listener() {
		ADC = new MCP3008(CE0, SCLK, MOSI, MISO);
		ADC.addMCP3008Listener(new MCP3008Listener() {
			public void ValueChanged(MCP3008Event e) {
				if(e.getChannel()==0) {
					VorschubEilProzent = (int) (e.getValue() / 1023.0 * 100.0);
					VorschubEilBar.setValue(VorschubEilProzent);
				} else if(e.getChannel()==1) {	
					VorschubProzent = (int) (e.getValue() / 1023.0 * 200.0);
					VorschubBar.setValue(VorschubProzent / 2); 
					VorschubLbl.setText("F: " + (int) (Vorschub * VorschubProzent / 100.0) + " mm/min");
				} else if(e.getChannel()==2) {
					CNC.getSpindel().setDrehzahlProzent((int) (e.getValue() / 1023.0 * 200.0));
					DrehzahlBar.setValue((int) CNC.getSpindel().getDrehzahlProzent() / 2); 
					DrehzahlLbl.setText("S: " + (int) (CNC.getSpindel().getDrehzahl() * CNC.getSpindel().getDrehzahlProzent() / 100.0) + " U/min");
				} 
			}
		});
	}

	/**
	 * Sämtliche IO-Ports werden konfiguriert und beschaltet
	 */
	private void Initialisieren_IO() {
		try {
			MCP23017Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			MCP4725GpioProvider = new MCP4725GpioProvider(I2CBus.BUS_1, com.pi4j.gpio.extension.mcp.MCP4725GpioProvider.MCP4725_ADDRESS_1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SpindelOut = gpio.provisionAnalogOutputPin(MCP4725GpioProvider, MCP4725Pin.OUTPUT);
		SpindelOut.setValue(1.0);

		HF1 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_A7, PinState.LOW);
		HF2 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_B2, PinState.LOW);
		HF3 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_B7, PinState.LOW);
		HF4 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_B5, PinState.LOW);

		CW1 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_B1, PinState.LOW);
		CW2 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_B0, PinState.LOW);
		CW3 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_B6, PinState.LOW);
		CW4 = gpio.provisionDigitalOutputPin(MCP23017Provider, MCP23017Pin.GPIO_B4, PinState.LOW);

		Control = gpio.provisionDigitalInputPin(MCP23017Provider, MCP23017Pin.GPIO_B3);
		Control.setPullResistance(PinPullResistance.OFF);

		CE0 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10, PinState.LOW);
		CE0.setShutdownOptions(true, PinState.LOW);
		MISO = gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, PinPullResistance.PULL_DOWN);
		MOSI = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, PinState.LOW);
		MOSI.setShutdownOptions(true, PinState.LOW);
		SCLK = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, PinState.HIGH);
		SCLK.setShutdownOptions(true, PinState.LOW);

		EndschalterXPlus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_UP);
		EndschalterXMinus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_UP);
		EndschalterYPlus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_23, PinPullResistance.PULL_UP);
		EndschalterYMinus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_UP);
		EndschalterZPlus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_UP);
		EndschalterZMinus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_UP);
		EndschalterAPlus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_UP);
		EndschalterAMinus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_UP);

		Clock1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
		Clock1.setShutdownOptions(true, PinState.LOW);
		Clock2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
		Clock2.setShutdownOptions(true, PinState.LOW);
		Clock3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
		Clock3.setShutdownOptions(true, PinState.LOW);
		Clock4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
		Clock4.setShutdownOptions(true, PinState.LOW);

		Schrittrelais = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
		Schrittrelais.setShutdownOptions(true, PinState.LOW);
		Fräsenrelais = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, PinState.LOW);
		Fräsenrelais.setShutdownOptions(true, PinState.LOW);

		Notaus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);	
	}

	/**
	 * Wechsel der Betriebsart (anderes Modul wird angezeigt)
	 * @param ButtonNummer Index des angeglickten Buttons
	 */
	private void Betriebsart(int ButtonNummer) {
		/**
		 * 0 Hand
		 * 1 Programm
		 * 2 Satz
		 */
		if (!Beschäftigt) {
			if(ButtonNummer==0) {
				if(ProgrammBtn.getBackground() == Color.white) {
					Programm.setVisible(false);
					Hand.setVisible(true);
					HandBtn.setBackground(Color.white);
					ProgrammBtn.setBackground(Color.gray);
				} else if(SatzBtn.getBackground() == Color.white) {
					Satz.setVisible(false);
					Hand.setVisible(true);
					HandBtn.setBackground(Color.white);
					SatzBtn.setBackground(Color.gray);
				}
			} else if(ButtonNummer==1) {
				if(HandBtn.getBackground() == Color.white) {
					Hand.setVisible(false);
					Programm.setVisible(true);
					ProgrammBtn.setBackground(Color.white);
					HandBtn.setBackground(Color.gray);
				} else if(SatzBtn.getBackground() == Color.white) {
					Satz.setVisible(false);
					Programm.setVisible(true);
					ProgrammBtn.setBackground(Color.white);
					SatzBtn.setBackground(Color.gray);
				}
			} else if(ButtonNummer==2) {
				if(HandBtn.getBackground() == Color.white) {
					Hand.setVisible(false);
					Satz.setVisible(true);
					SatzBtn.setBackground(Color.white);
					HandBtn.setBackground(Color.gray);
				} else if(ProgrammBtn.getBackground() == Color.white) {
					Programm.setVisible(false);
					Satz.setVisible(true);
					SatzBtn.setBackground(Color.white);
					ProgrammBtn.setBackground(Color.gray);
				}
			}
		}		
	}

	/**
	 * fügt ein PopUp-Menü hinzu
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * Zeichnet das Status-Panel neu
	 */
	public void repaint() {
		splitPaneGroß.setLeftComponent(splitPaneKlein);
		splitPaneGroß.setDividerLocation(1160);
		xSegment.repaint();
		ySegment.repaint();
		zSegment.repaint();
		aSegment.repaint();
	}

	/**
	 * Stellt das entsprechende Modul in den Vordergrund
	 * @param contentPane Fenster des Moduls
	 * @param buttonPanel Menü des Moduls
	 */
	public void setContentPane(JPanel contentPane, JPanel buttonPanel) {
		splitPaneGroß.setRightComponent(buttonPanel);
		splitPaneKlein.setLeftComponent(contentPane);
		splitPaneGroß.setDividerLocation(1160);
		splitPaneKlein.setDividerLocation(500);
	}

	/**
	 * Programmende
	 */
	public void shutdown() {
		if(Notaus.getState() == PinState.HIGH) {
			HF1.setState(PinState.LOW);
			HF2.setState(PinState.LOW);
			HF3.setState(PinState.LOW);
			HF4.setState(PinState.LOW);
			CW1.setState(PinState.LOW);
			CW2.setState(PinState.LOW);
			CW3.setState(PinState.LOW);
			CW4.setState(PinState.LOW);
			Schrittrelais.setState(PinState.LOW);
			Fräsenrelais.setState(PinState.LOW);
			MCP4725GpioProvider.shutdown();
			MCP23017Provider.shutdown();		
			gpio.shutdown();	
			try {
				Runtime.getRuntime().exec("sudo shutdown -h now");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		} else {
			System.out.println("Bitte Notaus betätigen");
		}		
	}
}
