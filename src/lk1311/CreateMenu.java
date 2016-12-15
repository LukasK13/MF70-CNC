package lk1311;

import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

/**
 * Erstellt Buttons zur Verwendung in einem zusammenhängenden Menü
 * @author Lukas Klass - 11.03.2015
 *
 */
public class CreateMenu {
	private JButton[] MenuButton;
	public ButtonMenuArray ButtonMenuArray;
	
	/**
	 * Erstellt ein Button-Menü auf dem übergebenen Panel
	 * @param MenuPanel Panel auf dem das Button-Menü angezeigt werden soll
	 */
	public CreateMenu(JPanel MenuPanel, Hauptfenster Hauptfenster) {
		MenuButton = new JButton[8];			
		ButtonMenuArray = new ButtonMenuArray(MenuButton);	
		ActionListener MenuListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == MenuButton[0]) {
					if(ButtonMenuArray.Menu.MenuUp != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuUp);
					}
				} else if (e.getSource() == ButtonMenuArray.MenuButton[1]) {
					if(ButtonMenuArray.Menu.MenuNext[1] != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuNext[1]);
					} else {
						ButtonMenuArray.Action(1);
					}				
				} else if (e.getSource() == ButtonMenuArray.MenuButton[2]) {
					if(ButtonMenuArray.Menu.MenuNext[2] != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuNext[2]);
					} else {
						ButtonMenuArray.Action(2);
					}
				} else if (e.getSource() == ButtonMenuArray.MenuButton[3]) {
					if(ButtonMenuArray.Menu.MenuNext[3] != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuNext[3]);
					} else {
						ButtonMenuArray.Action(3);
					}
				} else if (e.getSource() == ButtonMenuArray.MenuButton[4]) {
					if(ButtonMenuArray.Menu.MenuNext[4] != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuNext[4]);
					} else {
						ButtonMenuArray.Action(4);
					}
				} else if (e.getSource() == ButtonMenuArray.MenuButton[5]) {
					if(ButtonMenuArray.Menu.MenuNext[5] != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuNext[5]);
					} else {
						ButtonMenuArray.Action(5);
					}
				} else if (e.getSource() == ButtonMenuArray.MenuButton[6]) {
					if(ButtonMenuArray.Menu.MenuPrev != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuPrev);

					} else if(ButtonMenuArray.Menu.MenuNext[6] != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuNext[6]);
					} else {
						ButtonMenuArray.Action(6);
					}
				} else if (e.getSource() == ButtonMenuArray.MenuButton[7]) {
					if(ButtonMenuArray.Menu.MenuDown != null) {
						ButtonMenuArray.setButtonMenu(ButtonMenuArray.Menu.MenuDown);
					}
				}
			}
		};

		for(int i=0; i<MenuButton.length; i++) {
			MenuButton[i] = new JButton("");
			MenuButton[i].setBounds(1, i * 90, 107, 88);			
			MenuButton[i].addActionListener(MenuListener);
			MenuPanel.add(MenuButton[i]);
		}
	}
}

/**
 * Interface das zur Erstellung einer Menüfunktion implementiert werden muss
 * @author Lukas Klass - 11.03.2015
 *
 */
interface MenuFunc {
	/**
	 * Diese Funktion wird nach dem anklicken des entsprechenden Buttons ausgeführt, sofern kein weiterer Menüpointer gesetzt wurde.
	 * @param MenuButton Array, das sämtliche Menü-Buttons beinhaltet
	 * @param ButtonNummer Nummer des Buttons im Array, der angeklickt wurde
	 */
	public void ToDo(JButton[] MenuButton, int ButtonNummer);
}

/**
 * Erstellt das zu einem Menü-Button-Array gehörige Menü
 * @author Lukas Klass - 11.03.2015
 *
 */
class Menu {
	public MenuItem[] Items = new MenuItem[8];
	public Menu MenuUp = null;
	public Menu MenuDown = null;
	public Menu MenuPrev = null;
	public MenuFunc Anzeigen = null;
	public MenuFunc Verlassen = null;
	public Menu[] MenuNext = new Menu[] {null, null, null, null, null, null, null, null};
	
	private int i = 0;
	
	/**
	 * Erstellt ein Menü mit den folgenden Pointern
	 * @param MenuUp Menü, das beim klicken auf den "Nach Oben"-Button angezeigt werden soll
	 * @param MenuDown Menü, das beim klicken auf den "Nach Unten"-Button angezeigt werden soll
	 * @param MenuPrev Menü, das beim klicken auf den "Nach Vorne"-Button angezeigt werden soll
	 */
	public Menu(Menu MenuUp, Menu MenuDown, Menu MenuPrev) {
		this.MenuUp = MenuUp;
		this.MenuDown = MenuDown;
		this.MenuPrev = MenuPrev;
	}

	/**
	 * Erstellt ein Menü mit den folgenden Pointern
	 * @param MenuUp Menü, das beim klicken auf den "Nach Oben"-Button angezeigt werden soll
	 * @param MenuDown Menü, das beim klicken auf den "Nach Unten"-Button angezeigt werden soll
	 * @param MenuPrev Menü, das beim klicken auf den "Nach Vorne"-Button angezeigt werden soll
	 * @param Anzeigen Funktion, die beim anzeigen des Menüs ausgeführt werden soll
	 * @param Verlassen Funktion, die beim verlassen des Menüs ausgeführt werden soll
	 */
	public Menu(Menu MenuUp, Menu MenuDown, Menu MenuPrev, MenuFunc Anzeigen, MenuFunc Verlassen) {
		this.MenuUp = MenuUp;
		this.MenuDown = MenuDown;
		this.MenuPrev = MenuPrev;
		this.Anzeigen = Anzeigen;
		this.Verlassen = Verlassen;
	}

	/**
	 * Fügt einen weiteren Menüeintrag zum Menü an der ersten freien Stelle hinzu
	 * @param MenuItem Menüeintrag der hinzugefügt werden soll
	 */
	public void addItem(MenuItem MenuItem) {
		if(i<Items.length) {
			Items[i] = MenuItem;
			i++;
		} else {
			System.err.println(this.getClass() + " ItemList out of Bounds of exception");
		}
	}
}

/**
 * Erstellt ein Button-Menü-Array
 * @author Lukas Klass - 11.03.2015
 *
 */
class ButtonMenuArray {
	public JButton[] MenuButton;
	public Menu Menu;
	
	/**
	 * Führt die im Menüeintrag das jeweilligen Buttons hinterlegte Methode aus
	 * @param ButtonNummer Nummer des Buttons der angeklickt wurde
	 */
	public void Action(int ButtonNummer) {
		if (Menu.Items[ButtonNummer].MenuFunc != null) {
			Menu.Items[ButtonNummer].MenuFunc.ToDo(MenuButton, ButtonNummer);
		}		
	}
	
	/**
	 * Aktualisiert das Button-Menü nach der Änderung des anzuzeigenden Menüs
	 */
	public void Refresh() {
		for(int i=0; i<MenuButton.length; i++) {
			MenuButton[i].setText(Menu.Items[i].Name);
			MenuButton[i].setIcon(Menu.Items[i].MenuIcon);
			MenuButton[i].setBackground(Color.LIGHT_GRAY);
			MenuButton[i].repaint();
		}
	}
	
	/**
	 * Erstellt ein Button-Menü-Array aus einem JButton-Array
	 * @param MenuButton JButton-Array aus dem das Button-Menü-Array entstehen soll
	 */
	public ButtonMenuArray(JButton[] MenuButton) {
		this.MenuButton = MenuButton;
	}

	/**
	 * Definiert das anzuzeigende Menü
	 * @param ButtonMenu anzuzeigendes Menü
	 */
	public void setButtonMenu(Menu ButtonMenu) {
		if(this.Menu != null) {
			if(this.Menu.Verlassen != null) {
				this.Menu.Verlassen.ToDo(null, 0);
			}
		}		
		this.Menu = ButtonMenu;
		if(ButtonMenu.Anzeigen != null) {
			ButtonMenu.Anzeigen.ToDo(null, 1);
		}
		Refresh();
	}

	/**
	 * Gibt das angezeigte Menü aus
	 * @return angezeigtes Menü
	 */
	public Menu getButtonMenu() {
		return Menu;
	}
}

/**
 * Erstellt einen neuen Menüeintrag für ein Button-Menü
 * @author Lukas Klass - 11.03.2015
 *
 */
class MenuItem {
	public String Name;
	public ImageIcon MenuIcon = null;
	public MenuFunc MenuFunc;
	
	/**
	 * Erstellt einen neuen Menüeintrag für ein Button-Menü
	 * @param Name Name des Menüeintrags
	 * @param MenuIcon Icon des Menüeintrags
	 * @param MenuFunc Methode, die am anklicken ausgeführt werden soll
	 */
	public MenuItem(String Name, ImageIcon MenuIcon, MenuFunc MenuFunc) {
		this.Name = Name;
		this.MenuIcon = MenuIcon;
		this.MenuFunc = MenuFunc;
	}
}
