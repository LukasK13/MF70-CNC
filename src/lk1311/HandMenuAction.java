package lk1311;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.pi4j.io.gpio.*;

class HandMenuBeenden implements MenuFunc {
	private Hauptfenster Hauptfenster;

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		Hauptfenster.shutdown();
	}	

	public HandMenuBeenden(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}
}


class HandMenuAchswahlXYZ implements MenuFunc {
	private Hauptfenster Hauptfenster;

	public HandMenuAchswahlXYZ(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(ButtonNummer == 1) {
			if(MenuButton[1].getBackground() == Color.YELLOW) {
				MenuButton[1].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AchseVerfahren = -1;
			} else {
				MenuButton[1].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AchseVerfahren = 0;
			}
			MenuButton[2].setBackground(Color.LIGHT_GRAY);
			MenuButton[3].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 2) {
			if(MenuButton[2].getBackground() == Color.YELLOW) {
				MenuButton[2].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AchseVerfahren = -1;
			} else {
				MenuButton[2].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AchseVerfahren = 1;
			}
			MenuButton[1].setBackground(Color.LIGHT_GRAY);
			MenuButton[3].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 3) {
			if(MenuButton[3].getBackground() == Color.YELLOW) {
				MenuButton[3].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AchseVerfahren = -1;
			} else {
				MenuButton[3].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AchseVerfahren = 2;
			}
			MenuButton[1].setBackground(Color.LIGHT_GRAY);
			MenuButton[2].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 4) {
			if(MenuButton[4].getBackground() == Color.YELLOW) {
				MenuButton[4].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.SchrittweiteVerfahren = 10;
			} else {
				MenuButton[4].setBackground(Color.YELLOW);
				Hauptfenster.Hand.SchrittweiteVerfahren = 100;
			}
			MenuButton[5].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 5) {
			if(MenuButton[5].getBackground() == Color.YELLOW) {
				MenuButton[5].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.SchrittweiteVerfahren = 10;
			} else {
				MenuButton[5].setBackground(Color.YELLOW);
				Hauptfenster.Hand.SchrittweiteVerfahren = 1000;
			}
			MenuButton[4].setBackground(Color.LIGHT_GRAY);
		}
	}	
}


class HandMenuAchswahlA implements MenuFunc {
	private Hauptfenster Hauptfenster;

	public HandMenuAchswahlA(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}
	
	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(ButtonNummer == 1) {
			if(MenuButton[1].getBackground() == Color.YELLOW) {
				MenuButton[1].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AchseVerfahren = -1;
			} else {
				MenuButton[1].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AchseVerfahren = 3;
			}
		} else if(ButtonNummer == 2) {
			if(MenuButton[2].getBackground() == Color.YELLOW) {
				MenuButton[2].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.SchrittweiteVerfahrenA = 0;
			} else {
				MenuButton[2].setBackground(Color.YELLOW);
				Hauptfenster.Hand.SchrittweiteVerfahrenA = 1;
			}
			MenuButton[3].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 3) {
			if(MenuButton[3].getBackground() == Color.YELLOW) {
				MenuButton[3].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.SchrittweiteVerfahrenA = 0;
			} else {
				MenuButton[3].setBackground(Color.YELLOW);
				Hauptfenster.Hand.SchrittweiteVerfahrenA = 5;
			}
			MenuButton[2].setBackground(Color.LIGHT_GRAY);
		}
	}	
}


class EndschalterIgnorieren implements MenuFunc {
	private Hauptfenster Hauptfenster;

	public EndschalterIgnorieren(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(Hauptfenster.Hand.EndschalterIgnorieren) {
			Hauptfenster.Hand.EndschalterIgnorieren = false;
			MenuButton[ButtonNummer].setBackground(Color.LIGHT_GRAY);
		} else {
			Hauptfenster.Hand.EndschalterIgnorieren = true;
			MenuButton[ButtonNummer].setBackground(Color.yellow);
		}
	}
}


class HandMenuAchswahlListener implements MenuFunc {
	@SuppressWarnings("unused")
	private Hauptfenster Hauptfenster;
	private KeyDispatcher KeyDispatcher;
	private MouseDispatcher MouseDispatcher;
	private KeyboardFocusManager KeyboardManager;

	public HandMenuAchswahlListener(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
		KeyboardManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		KeyDispatcher = new KeyDispatcher(Hauptfenster);
		MouseDispatcher = new MouseDispatcher(Hauptfenster);
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(ButtonNummer == 0) {
			//Menu verlassen
			KeyboardManager.removeKeyEventDispatcher(KeyDispatcher);
			Toolkit.getDefaultToolkit().removeAWTEventListener(MouseDispatcher);
		} else if(ButtonNummer == 1) {
			//Menu anzeigen
			KeyboardManager.addKeyEventDispatcher(KeyDispatcher);
			Toolkit.getDefaultToolkit().addAWTEventListener(MouseDispatcher, AWTEvent.MOUSE_EVENT_MASK);
			Toolkit.getDefaultToolkit().addAWTEventListener(MouseDispatcher, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		}
	}	
}


class MouseDispatcher implements AWTEventListener {
	private Hauptfenster Hauptfenster;

	public MouseDispatcher(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void eventDispatched(AWTEvent event) {
		if(event instanceof MouseEvent){
			MouseEvent evt = (MouseEvent)event;
			if(Hauptfenster.Hand.HandMenu.ButtonMenuArray.getButtonMenu() == Hauptfenster.Hand.HandMenuAchswahlXYZ) {
				if((evt.getID() == MouseEvent.MOUSE_PRESSED) && (evt.getButton() == 6)) {        	
					if(Hauptfenster.Hand.SchrittweiteVerfahren == 100) {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[5].doClick();
					} else if(Hauptfenster.Hand.SchrittweiteVerfahren == 1000) {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[5].doClick();
					} else {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[4].doClick();
					}
				} else if((evt.getID() == MouseEvent.MOUSE_PRESSED) && (evt.getButton() == 7)) {        	
					if(Hauptfenster.Hand.AchseVerfahren == 0) {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[2].doClick();
					} else if(Hauptfenster.Hand.AchseVerfahren == 1) {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[3].doClick();
					} else {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[1].doClick();
					}
				}
			} else if(Hauptfenster.Hand.HandMenu.ButtonMenuArray.getButtonMenu() == Hauptfenster.Hand.HandMenuAchswahlA) {
				if((evt.getID() == MouseEvent.MOUSE_PRESSED) && (evt.getButton() == 6)) {
					if(Hauptfenster.Hand.SchrittweiteVerfahrenA == 0) {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[2].doClick();
					} else if(Hauptfenster.Hand.SchrittweiteVerfahrenA == 1) {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[3].doClick();
					} else if(Hauptfenster.Hand.SchrittweiteVerfahrenA == 5) {
						Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[3].doClick();
					}
				} else if((evt.getID() == MouseEvent.MOUSE_PRESSED) && (evt.getButton() == 7)) {
					Hauptfenster.Hand.HandMenu.ButtonMenuArray.MenuButton[1].doClick();
				}
			}
		}
		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent evt = (MouseWheelEvent) event;
			if(evt.getWheelRotation() > 0) {
				if(Hauptfenster.Hand.AchseVerfahren == 0) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenXPlus");
				} else if(Hauptfenster.Hand.AchseVerfahren == 2) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenZPlus");
				} else if(Hauptfenster.Hand.AchseVerfahren == 1) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenYPlus");
				} else if(Hauptfenster.Hand.AchseVerfahren == 3) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenAPlus");
				}
			} else {
				if(Hauptfenster.Hand.AchseVerfahren == 0) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenXMinus");
				} else if(Hauptfenster.Hand.AchseVerfahren == 2) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenZMinus");
				} else if(Hauptfenster.Hand.AchseVerfahren == 1) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenYMinus");
				} else if(Hauptfenster.Hand.AchseVerfahren == 3) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenAMinus");
				}
			}
		}
	}

}


class KeyDispatcher implements KeyEventDispatcher {
	private Hauptfenster Hauptfenster;

	public KeyDispatcher(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public boolean dispatchKeyEvent(KeyEvent arg0) {
		if(arg0.getID() == KeyEvent.KEY_PRESSED) {
			if(Hauptfenster.Hand.EndschalterIgnorieren) {
				if(arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenXPlusOhne");
				} else if(arg0.getKeyCode() == KeyEvent.VK_LEFT) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenXMinusOhne");
				} else if(arg0.getKeyCode() == KeyEvent.VK_DOWN) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenZPlusOhne");
				} else if(arg0.getKeyCode() == KeyEvent.VK_UP) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenZMinusOhne");
				} else if(arg0.getKeyCode() == KeyEvent.VK_PAGE_UP) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenYPlusOhne");
				} else if(arg0.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenYMinusOhne");
				} else if(arg0.getKeyCode() == KeyEvent.VK_PLUS) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenAPlusOhne");
				} else if(arg0.getKeyCode() == KeyEvent.VK_MINUS) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenAMinusOhne");
				}
			} else {
				if(arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenXPlus");
				} else if(arg0.getKeyCode() == KeyEvent.VK_LEFT) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenXMinus");
				} else if(arg0.getKeyCode() == KeyEvent.VK_DOWN) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenZPlus");
				} else if(arg0.getKeyCode() == KeyEvent.VK_UP) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenZMinus");
				} else if(arg0.getKeyCode() == KeyEvent.VK_PAGE_UP) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenYPlus");
				} else if(arg0.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenYMinus");
				} else if(arg0.getKeyCode() == KeyEvent.VK_PLUS) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenAPlus");
				} else if(arg0.getKeyCode() == KeyEvent.VK_MINUS) {
					Hauptfenster.Hand.HandFräsThread.setAction("VerfahrenAMinus");
				}
			}
		}
		return false;
	}
}


class HandMenuAntasten implements MenuFunc {
	private Hauptfenster Hauptfenster;
	
	public HandMenuAntasten(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}
	
	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(ButtonNummer == 1) {
			if(MenuButton[1].getBackground() == Color.YELLOW) {
				MenuButton[1].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AntastenAchse = 0;
			} else {
				MenuButton[1].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AntastenAchse = 1;
			}
			MenuButton[2].setBackground(Color.LIGHT_GRAY);
			MenuButton[3].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 2) {
			if(MenuButton[2].getBackground() == Color.YELLOW) {
				MenuButton[2].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AntastenAchse = 0;
			} else {
				MenuButton[2].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AntastenAchse = 2;
			}
			MenuButton[1].setBackground(Color.LIGHT_GRAY);
			MenuButton[3].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 3) {
			if(MenuButton[3].getBackground() == Color.YELLOW) {
				MenuButton[3].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AntastenAchse = 0;
			} else {
				MenuButton[3].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AntastenAchse = 3;
			}
			MenuButton[1].setBackground(Color.LIGHT_GRAY);
			MenuButton[2].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 4) {
			if(MenuButton[4].getBackground() == Color.YELLOW) {
				MenuButton[4].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AntastenRichtung = 0;
			} else {
				MenuButton[4].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AntastenRichtung = 1;
			}
			MenuButton[5].setBackground(Color.LIGHT_GRAY);
		} else if(ButtonNummer == 5) {
			if(MenuButton[5].getBackground() == Color.YELLOW) {
				MenuButton[5].setBackground(Color.LIGHT_GRAY);
				Hauptfenster.Hand.AntastenRichtung = 0;
			} else {
				MenuButton[5].setBackground(Color.YELLOW);
				Hauptfenster.Hand.AntastenRichtung = 2;
			}
			MenuButton[4].setBackground(Color.LIGHT_GRAY);
		}
	}
}


class IO implements MenuFunc {
	private Hauptfenster Hauptfenster;
	public GpioPinDigitalOutput[] Output = new GpioPinDigitalOutput[14];
	public GpioPinDigitalInput[] Input = new GpioPinDigitalInput[13];

	public IO(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(Hauptfenster.splitPaneGroß.getLeftComponent() == Hauptfenster.splitPaneKlein) {
			new IOPanel(Hauptfenster);
			MenuButton[4].setBackground(Color.YELLOW);
		} else {
			Hauptfenster.repaint();
			MenuButton[4].setBackground(Color.LIGHT_GRAY);
		}
	}	
}


class Werkzeugdaten implements MenuFunc {
	private Hauptfenster Hauptfenster;
	private Werkzeugtabelle Werkzeugtabelle = null;

	public Werkzeugdaten(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {		
		if(Werkzeugtabelle == null) {
			Werkzeugtabelle = new Werkzeugtabelle(Hauptfenster);
		}
		if(Hauptfenster.splitPaneGroß.getLeftComponent() == Hauptfenster.splitPaneKlein) {
			Hauptfenster.splitPaneGroß.setLeftComponent(Werkzeugtabelle.WerkzeugPanel);
			Hauptfenster.splitPaneGroß.setDividerLocation(1160);
			MenuButton[5].setBackground(Color.YELLOW);
		} else {
			Hauptfenster.Werkzeugdaten = Werkzeugtabelle.getWerkzeugdaten();
			Werkzeugtabelle.Speichern();
			Hauptfenster.repaint();
			MenuButton[5].setBackground(Color.LIGHT_GRAY);
		}
	}
}


class Referenz implements MenuFunc {
	private int Achse;
	private Hauptfenster Hauptfenster;

	public Referenz(Hauptfenster Hauptfenster, int Achse) {
		this.Hauptfenster = Hauptfenster;
		this.Achse = Achse;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(Achse == 0) {
			Hauptfenster.Hand.HandFräsThread.setAction("xReferenz");
		} else if(Achse == 1) {
			Hauptfenster.Hand.HandFräsThread.setAction("yReferenz");
		} else if(Achse == 2) {
			Hauptfenster.Hand.HandFräsThread.setAction("zReferenz");
		} else if(Achse == 3) {
			Hauptfenster.Hand.HandFräsThread.setAction("aReferenz");
		} else if(Achse == 4) {
			Hauptfenster.Hand.HandFräsThread.setAction("alleReferenz");
		}
	}
}


class AchseNullen implements MenuFunc {
	private int Achse;
	private Hauptfenster Hauptfenster;

	public AchseNullen(Hauptfenster Hauptfenster, int Achse) {
		this.Hauptfenster = Hauptfenster;
		this.Achse = Achse;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		if(Achse == 0) {
			Hauptfenster.Hand.HandFräsThread.setAction("xNullen");
		} else if(Achse == 1) {
			Hauptfenster.Hand.HandFräsThread.setAction("yNullen");
		} else if(Achse == 2) {
			Hauptfenster.Hand.HandFräsThread.setAction("zNullen");
		} else if(Achse == 4) {
			Hauptfenster.Hand.HandFräsThread.setAction("alleNullen");
		}
	}
}


class Werteingabe implements MenuFunc {
	private Hauptfenster Hauptfenster;

	public Werteingabe(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
	}

	public void ToDo(JButton[] MenuButton, int ButtonNummer) {
		NullDialog NullDialog = new NullDialog(Hauptfenster.Fenster, Hauptfenster);
		NullDialog.setVisible(true);
	}	
}


class NullDialog extends JDialog{
	public NullDialog(JFrame parent, Hauptfenster Hauptfenster) {
		super(parent,"Koordinateneingabe", true);
		setBackground(new Color(240, 240, 240));
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 11));
		setTitle("Koordinateneingabe");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(376,176);		
		getContentPane().setLayout(null);

		JNumberField WertNum = new JNumberField();
		WertNum.setBounds(10, 40, 100, 25);
		getContentPane().add(WertNum);
		WertNum.setColumns(10);

		JLabel TitelLbl = new JLabel("Neuer Wert:");
		TitelLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TitelLbl.setBounds(10, 11, 374, 20);
		getContentPane().add(TitelLbl);		

		JButton XBtn = new JButton("X setzen");
		XBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(WertNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					Hauptfenster.CNC.getxAchse().setKoordinateRelativ(WertNum.getDouble());
					dispose();
				}								
			}
		});
		XBtn.setBounds(10, 76, 90, 23);
		getContentPane().add(XBtn);

		JButton YBtn = new JButton("Y setzen");
		YBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(WertNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					Hauptfenster.CNC.getyAchse().setKoordinateRelativ(WertNum.getDouble());
					dispose();
				}								
			}
		});
		YBtn.setBounds(100, 76, 90, 23);
		getContentPane().add(YBtn);

		JButton ZBtn = new JButton("Z setzen");
		ZBtn.setBounds(190, 76, 90, 23);
		ZBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(WertNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					Hauptfenster.CNC.getzAchse().setKoordinateRelativ(WertNum.getDouble());
					dispose();
				}								
			}
		});
		getContentPane().add(ZBtn);

		JButton ABtn = new JButton("A setzen");
		ABtn.setBounds(280, 76, 90, 23);
		ABtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(WertNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					Hauptfenster.CNC.getaAchse().setKoordinateRelativ(WertNum.getDouble());
					dispose();
				}								
			}
		});
		getContentPane().add(ABtn);

		JButton AbbrechenBtn = new JButton("Abbrechen");
		AbbrechenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		AbbrechenBtn.setBounds(10, 114, 110, 23);
		getContentPane().add(AbbrechenBtn);
	}
}