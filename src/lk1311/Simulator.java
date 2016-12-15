package lk1311;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class Simulator {

	//sichtbare Komponenten
	public JPanel contentPane, TurtlePanel, NavigatePanel;
	private JSplitPane splitPaneGroß, splitPaneKlein;
	private Turtle t;
	private JScrollPane CNCStatusScroll;
	private JTextArea CNCStatus;	
	private JCheckBox KoordinatenachsenChk, FahrwegAußenChk;
	private JProgressBar Fortschritt;
	private JLabel lblMastab, lblVorschubgeschwindigkeit;
	private JButton LinksBtn, ObenBtn, RechtsBtn, UntenBtn, ResetZoomBtn;
	private JSlider MaßstabSld, VorschubSld;
	private Hauptfenster Hauptfenster;

	//Subklassen
	public SimulatorEngine SimulatorEngine;
	private ProgressbarUpdate PbUpdate;
	private Thread SimulatorEngineThread;
	private Thread PbUpdateThread;

	//private Variablen
	private double maßstab=1;
	private final double Pixelbreite = 0.24;
	private double ObenLinksX = 0, ObenLinksY = 100, Länge = 160, Breite = 100, Höhe = 20;

	//öffentliche Variablen
	public String[] CodeSplit;
	public String[][] Werkzeugdaten;

	public Simulator(Hauptfenster Hauptfenster) {	
		this.Hauptfenster = Hauptfenster;
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setLayout(null);
		contentPane.setSize(1160, 703);

		splitPaneGroß = new JSplitPane();
		splitPaneGroß.setBackground(Color.GRAY);
		splitPaneGroß.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneGroß.setEnabled(false);
		splitPaneGroß.setBounds(0, 0, 1160, 703);
		splitPaneGroß.setDividerLocation(500);
		contentPane.add(splitPaneGroß);

		splitPaneKlein = new JSplitPane();
		splitPaneKlein.setEnabled(false);
		splitPaneKlein.setBounds(0, 0, contentPane.getWidth() * 2 / 3,contentPane.getHeight() - 3);
		splitPaneKlein.setDividerLocation(700);
		splitPaneGroß.setRightComponent(splitPaneKlein);		

		TurtlePanel = new JPanel();
		FlowLayout TurtlePanelLayout = (FlowLayout) TurtlePanel.getLayout();
		TurtlePanelLayout.setAlignOnBaseline(true);
		TurtlePanelLayout.setAlignment(FlowLayout.LEFT);
		TurtlePanelLayout.setVgap(0);
		TurtlePanelLayout.setHgap(0);
		splitPaneGroß.setLeftComponent(TurtlePanel);	

		CNCStatusScroll = new JScrollPane();
		CNCStatusScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPaneKlein.setLeftComponent(CNCStatusScroll);

		CNCStatus = new JTextArea();
		CNCStatus.setEditable(false);
		CNCStatus.setFont(new Font("Monospaced", Font.PLAIN, 14));
		CNCStatusScroll.setViewportView(CNCStatus);

		t = new Turtle(1158,498);
		t.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent arg0) {
				if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
					Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
					t.setOrigin(arg0.getX(), arg0.getY());
					KomponentenZeichnen();
				}
			}
		});
		t.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				if(SwingUtilities.isLeftMouseButton(arg0)) {
					if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
						Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
						t.setOrigin(arg0.getX(), arg0.getY());
						KomponentenZeichnen();
						SimulationStarten(false);
					}
				}				
			}

			public void mouseReleased(MouseEvent arg0) {
				Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});

		t.setBackground(Color.white);
		t.setForeground(Color.black);
		t.setOrigin(t.getWidth()/2, t.getHeight()/2);
		TurtlePanel.add(t);		

		SimulatorEngine = new SimulatorEngine();
		SimulatorEngineThread = new Thread (SimulatorEngine);
		SimulatorEngine.t = t;
		SimulatorEngine.CNCStatus = CNCStatus;

		NavigatePanel = new JPanel();
		NavigatePanel.setBackground(Color.LIGHT_GRAY);
		splitPaneKlein.setRightComponent(NavigatePanel);
		NavigatePanel.setLayout(null);

		LinksBtn = new JButton("");
		LinksBtn.setIcon(new ImageIcon(Simulator.class.getResource("/resources/Zurück_Aktiv.png")));
		LinksBtn.setBounds(6, 60, 60, 60);
		LinksBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
					Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
					t.setOrigin(t.getOriginX()-20, t.getOriginY());
					KomponentenZeichnen();
					SimulationStarten(false);
				}			
			}
		});
		NavigatePanel.add(LinksBtn);

		ObenBtn = new JButton("");
		ObenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
					Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
					t.setOrigin(t.getOriginX(), t.getOriginY()-20);
					KomponentenZeichnen();
					SimulationStarten(false);
				}				
			}
		});
		ObenBtn.setIcon(new ImageIcon(Simulator.class.getResource("/resources/Oben_Aktiv.png")));
		ObenBtn.setBounds(76, 6, 60, 60);
		NavigatePanel.add(ObenBtn);

		RechtsBtn = new JButton("");
		RechtsBtn.setIcon(new ImageIcon(Simulator.class.getResource("/resources/Vor_Aktiv.png")));
		RechtsBtn.setBounds(146, 60, 60, 60);
		RechtsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
					Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
					t.setOrigin(t.getOriginX()+20, t.getOriginY());
					KomponentenZeichnen();
					SimulationStarten(false);
				}
			}
		});
		NavigatePanel.add(RechtsBtn);

		UntenBtn = new JButton("");
		UntenBtn.setIcon(new ImageIcon(Simulator.class.getResource("/resources/Unten_Aktiv.png")));
		UntenBtn.setBounds(76, 114, 60, 60);
		UntenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
					Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
					t.setOrigin(t.getOriginX(), t.getOriginY()+20);
					KomponentenZeichnen();
					SimulationStarten(false);
				}
			}
		});
		NavigatePanel.add(UntenBtn);

		MaßstabSld = new JSlider();
		MaßstabSld.setSnapToTicks(true);
		MaßstabSld.setPaintTicks(true);
		MaßstabSld.setBounds(241, 25, 200, 21);
		MaßstabSld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {				
				if (MaßstabSld.getValue() == 50.0) {
					maßstab = 1.0;				
				} else if (MaßstabSld.getValue() > 50.0) {
					maßstab = (MaßstabSld.getValue() - 45.0) / 20.0 + 0.75;
				} else if (MaßstabSld.getValue() < 50.0) {
					maßstab = MaßstabSld.getValue()	/ 50.0;			
				}				
				lblMastab.setText("Maßstab: 1:" + maßstab);
				KomponentenZeichnen();
				if (!MaßstabSld.getValueIsAdjusting()) {
					SimulationStarten(false);
				}
			}
		});
		NavigatePanel.add(MaßstabSld);

		VorschubSld = new JSlider();
		VorschubSld.setBounds(241, 75, 200, 21);
		VorschubSld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblVorschubgeschwindigkeit.setText("Vorschubgeschwindigkeit: " + VorschubSld.getValue() + "%");
				t.sleepTime=201 - VorschubSld.getValue() * 2;
			}
		});	
		NavigatePanel.add(VorschubSld);

		KoordinatenachsenChk = new JCheckBox("Koordinatenachsen anzeigen");
		KoordinatenachsenChk.setSelected(true);
		KoordinatenachsenChk.setBounds(241, 110, 200, 18);
		KoordinatenachsenChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});
		NavigatePanel.add(KoordinatenachsenChk);

		FahrwegAußenChk = new JCheckBox("Fahrweg außen anzeigen");
		FahrwegAußenChk.setSelected(true);
		FahrwegAußenChk.setBounds(241, 135, 200, 18);
		FahrwegAußenChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});	
		NavigatePanel.add(FahrwegAußenChk);

		lblVorschubgeschwindigkeit = new JLabel("Vorschubgeschwindigkeit: 50%");
		lblVorschubgeschwindigkeit.setBounds(241, 56, 200, 16);
		NavigatePanel.add(lblVorschubgeschwindigkeit);

		lblMastab = new JLabel("Maßstab: 1:1");
		lblMastab.setBounds(241, 6, 200, 16);
		NavigatePanel.add(lblMastab);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(228, 6, 2, 178);
		NavigatePanel.add(separator);

		Fortschritt = new JProgressBar();
		Fortschritt.setStringPainted(true);
		Fortschritt.setBounds(241, 165, 200, 19);
		NavigatePanel.add(Fortschritt);

		ResetZoomBtn = new JButton("Reset");
		ResetZoomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
					Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
					t.setOrigin(t.getWidth()/2, t.getHeight()/2);
					KomponentenZeichnen();
					SimulationStarten(false);
				}
			}
		});
		ResetZoomBtn.setBounds(65, 65, 80, 50);
		NavigatePanel.add(ResetZoomBtn);

		PbUpdate = new ProgressbarUpdate();
		PbUpdateThread = new Thread(PbUpdate);		

		//KomponentenZeichnen();
		//SimulationStarten(false);
	}

	private void Koordinatenachsen() {	
		if (KoordinatenachsenChk.isSelected()) {
			t.setForeground(Color.black);
			t.moveto(0, t.getHeight()*(-1));
			t.drawto(0, t.getHeight());
			t.moveto(t.getWidth()*(-1), 0);
			t.drawto(t.getWidth(), 0);
		}		
	}

	private void WerkstückZeichnen() {
		t.setForeground(Color.LIGHT_GRAY);
		t.myBufferedGraphics.fillRect((int) Pixel(ObenLinksX) + t.getOriginX(), (int) -Pixel(ObenLinksY) + t.getOriginY(), (int) Pixel(Länge), (int) Pixel(Breite));
	}

	private double Pixel(double Strecke) {
		double pixel = Strecke * maßstab / Pixelbreite;
		return pixel;
	}

	public void KomponentenZeichnen() {
		if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
			t.drawDynamic=false;
			t.clear();
			WerkstückZeichnen();
			Koordinatenachsen();
		}
	}

	public void SimulationStarten(boolean Vorschub) {	
		if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
			t.drawDynamic=Vorschub;
			SimulatorEngine.Pause=false;
			SimulatorEngine.StopBtn = Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1];

			PbUpdate.FortschrittBar = Fortschritt;
			PbUpdateThread = new Thread(PbUpdate);
			PbUpdateThread.setName("PbUpdateThread");
			PbUpdateThread.start();

			SimulatorEngine.Werkzeugdaten = Werkzeugdaten;
			SimulatorEngine.CodeSplit = CodeSplit;
			SimulatorEngine.maßstab = maßstab;	
			SimulatorEngine.FahrwegAußen = FahrwegAußenChk.isSelected();
			SimulatorEngine.PbUpdate = PbUpdate;
			SimulatorEngine.PbUpdateThread = PbUpdateThread;
			SimulatorEngineThread = new Thread (SimulatorEngine);
			SimulatorEngineThread.setName("SimulatorEngineThread");
			SimulatorEngineThread.start();		
		}		
	}

	public void anzeigen() {
		if(Hauptfenster.splitPaneGroß.getLeftComponent() == Hauptfenster.splitPaneKlein) {
			if (Hauptfenster.Programm.Editor.isCodeGeprüft()) {
				String Code = Hauptfenster.Programm.Editor.getText();
				CodeSplit = Code.split(System.getProperty("line.separator"));
				Werkzeugdaten = Hauptfenster.Werkzeugdaten;
				Hauptfenster.splitPaneGroß.setLeftComponent(contentPane);
				Hauptfenster.splitPaneGroß.setDividerLocation(1160);
			} else {
				Hauptfenster.Programm.Editor.Prüfen();
			}
		} else {
			Hauptfenster.splitPaneGroß.setLeftComponent(Hauptfenster.splitPaneKlein);
			Hauptfenster.splitPaneGroß.setDividerLocation(1160);
		}		
	}

	public void Werkstück() {
		WerkstückDialog WerkstückDialog = new WerkstückDialog(Hauptfenster.Fenster, ObenLinksX, ObenLinksY, Länge, Breite, Höhe);
		WerkstückDialog.setVisible(true);
		ObenLinksX = WerkstückDialog.getObenLinksX();
		ObenLinksY = WerkstückDialog.getObenLinksY();
		Länge = WerkstückDialog.getLänge();
		Breite = WerkstückDialog.getBreite();
		Höhe = WerkstückDialog.getHöhe();
		if(ThreadBeenden(PbUpdateThread) && ThreadBeenden(SimulatorEngineThread)) {
			Hauptfenster.Programm.ProgrammMenu.ButtonMenuArray.MenuButton[1].setText("<html>Simulation<br>Starten</html>");
			KomponentenZeichnen();
			SimulationStarten(false);
		}
	}

	private boolean ThreadBeenden(Thread ThreadBeenden) {
		boolean Erfolg = true;
		int k=0;
		while (ThreadBeenden.isAlive()) {
			ThreadBeenden.interrupt();
			k++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			if (k==5) {
				System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
				Erfolg = false;
				break;
			}
		}
		return Erfolg;
	}
}


class WerkstückDialog extends JDialog{
	private JNumberField ObenLinksXNum, ObenLinksYNum, LängeNum, BreiteNum, HöheNum;
	private double ObenLinksX, ObenLinksY, Länge, Breite, Höhe;

	public WerkstückDialog (JFrame parent, double ObenLinksXAlt, double ObenLinksYAlt, double LängeAlt, double BreiteAlt, double HöheAlt) {
		super(parent,"Werkstückdimensionen", true);
		this.ObenLinksX = ObenLinksXAlt;
		this.ObenLinksY = ObenLinksYAlt;
		this.Länge = LängeAlt;
		this.Breite = BreiteAlt;
		this.Höhe = HöheAlt;
		setTitle("Werkstückdimensionen");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(275,300);		
		getContentPane().setLayout(null);

		JLabel lblNewLabel_3 = new JLabel("Koordinaten Kante oben links");
		lblNewLabel_3.setLocation(10, 42);
		lblNewLabel_3.setSize(250, 20);
		getContentPane().add(lblNewLabel_3);

		JLabel lblObenLinksX = new JLabel("X:");
		lblObenLinksX.setHorizontalAlignment(SwingConstants.RIGHT);
		lblObenLinksX.setLocation(10, 73);
		lblObenLinksX.setSize(50, 25);
		getContentPane().add(lblObenLinksX);

		ObenLinksXNum = new JNumberField();
		ObenLinksXNum.setLocation(70, 73);
		ObenLinksXNum.setSize(70, 25);
		ObenLinksXNum.setDouble(ObenLinksX);
		getContentPane().add(ObenLinksXNum);
		ObenLinksXNum.setColumns(10);

		JLabel lblObenLinksY = new JLabel("Y:");
		lblObenLinksY.setHorizontalAlignment(SwingConstants.RIGHT);
		lblObenLinksY.setLocation(130, 73);
		lblObenLinksY.setSize(50, 25);
		getContentPane().add(lblObenLinksY);

		ObenLinksYNum = new JNumberField();
		ObenLinksYNum.setLocation(190, 73);
		ObenLinksYNum.setSize(70, 25);
		ObenLinksYNum.setDouble(ObenLinksY);
		getContentPane().add(ObenLinksYNum);
		ObenLinksYNum.setColumns(10);

		JLabel lblLängeDx = new JLabel("Länge dX:");
		lblLängeDx.setLocation(10, 117);
		lblLängeDx.setSize(50, 25);
		getContentPane().add(lblLängeDx);

		LängeNum = new JNumberField();
		LängeNum.setLocation(70, 117);
		LängeNum.setSize(70, 25);
		LängeNum.setDouble(Länge);
		getContentPane().add(LängeNum);
		LängeNum.setColumns(10);

		JLabel lblBreiteDy = new JLabel("Breite dY:");
		lblBreiteDy.setLocation(10, 148);
		lblBreiteDy.setSize(50, 25);
		getContentPane().add(lblBreiteDy);

		BreiteNum = new JNumberField();
		BreiteNum.setLocation(70, 148);
		BreiteNum.setSize(70, 25);
		BreiteNum.setDouble(Breite);
		getContentPane().add(BreiteNum);
		BreiteNum.setColumns(10);

		JLabel lblHöheDz = new JLabel("Höhe dZ:");
		lblHöheDz.setLocation(10, 179);
		lblHöheDz.setSize(50, 25);
		getContentPane().add(lblHöheDz);

		HöheNum = new JNumberField();
		HöheNum.setLocation(70, 179);
		HöheNum.setSize(70, 25);
		HöheNum.setDouble(Höhe);
		getContentPane().add(HöheNum);
		HöheNum.setColumns(10);

		JButton btnÜbernehmen = new JButton("Übernehmen");
		btnÜbernehmen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ObenLinksXNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					ObenLinksX = ObenLinksXNum.getDouble();
				}
				if(ObenLinksYNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					ObenLinksY = ObenLinksYNum.getDouble();
				}
				if(LängeNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					Länge = LängeNum.getDouble();
				}
				if(BreiteNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					Breite = BreiteNum.getDouble();
				}
				if(HöheNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					Höhe = HöheNum.getDouble();
				}
				dispose();											
			}
		});
		btnÜbernehmen.setBounds(10, 230, 110, 25);
		getContentPane().add(btnÜbernehmen);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnAbbrechen.setBounds(150, 231, 110, 23);
		getContentPane().add(btnAbbrechen);

		JLabel TitelLbl = new JLabel("Werkstückdimensionen:");
		TitelLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TitelLbl.setBounds(10, 11, 374, 20);
		getContentPane().add(TitelLbl);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 104, 250, 2);
		getContentPane().add(separator);
	}

	public double getObenLinksX() {
		return ObenLinksX;
	}

	public double getObenLinksY() {
		return ObenLinksY;
	}

	public double getLänge() {
		return Länge;
	}

	public double getBreite() {
		return Breite;
	}

	public double getHöhe() {
		return Höhe;
	}
}


class ProgressbarUpdate implements Runnable{	
	public JProgressBar FortschrittBar;
	public int Fortschritt = 0;
	
	public void run() {
		Thread.currentThread().setName("PbUpdateThread");
		while (!Thread.interrupted()) {
			if (Fortschritt < 100) {
				FortschrittBar.setValue(Fortschritt);
			} else {
				FortschrittBar.setValue(Fortschritt);
				Thread.currentThread().interrupt();
				break;				
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
}
