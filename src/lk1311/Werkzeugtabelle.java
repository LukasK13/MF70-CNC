package lk1311;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.*;

public class Werkzeugtabelle {
	private JTable WerkzeugTable;
	private Hauptfenster Hauptfenster;
	public JPanel WerkzeugPanel;
	public String[][] Werkzeugdaten;

	public Werkzeugtabelle(Hauptfenster Hauptfenster) {
		Werkzeugdaten = Hauptfenster.Werkzeugdaten;
		this.Hauptfenster = Hauptfenster;
		
		WerkzeugPanel = new JPanel();
		WerkzeugPanel.setBackground(Color.LIGHT_GRAY);
		WerkzeugPanel.setLayout(null);

		JScrollPane WerkzeugTableScroll = new JScrollPane();
		WerkzeugTableScroll.setBounds(0, 0, 1160, 703);
		WerkzeugPanel.add(WerkzeugTableScroll);

		WerkzeugTable = new JTable();
		//WerkzeugTable.setShowHorizontalLines(true);
		WerkzeugTable.setShowVerticalLines(true);
		WerkzeugTable.setRowHeight(25);
		WerkzeugTable.setFont(new Font("Tahoma", Font.PLAIN, 20));
		WerkzeugTable.setRowSelectionAllowed(false);
		WerkzeugTable.setModel(new DefaultTableModel(
				Werkzeugdaten,
				new String[] {"Werkzeugnummer", "Name", "Länge", "Durchmesser"}
				) {
			boolean[] columnEditables = new boolean[] {false, true, true, true};

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		//WerkzeugTable.getColumn("Werkzeugnummer").setCellRenderer(new ColoredTableCellRenderer());
		//WerkzeugTable.getColumn("Name").setCellRenderer(new ColoredTableCellRenderer());
		//WerkzeugTable.getColumn("Länge").setCellRenderer(new ColoredTableCellRenderer());
		//WerkzeugTable.getColumn("Durchmesser").setCellRenderer(new ColoredTableCellRenderer());
		WerkzeugTableScroll.setViewportView(WerkzeugTable);
	}

	public void Speichern() {
		try {
			FileWriter writer = null;
			writer = new FileWriter(Hauptfenster.WerkzeugdatenPfad ,false); 
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write("----------MF70 Werkzeugdaten erstellt mit MF70 CNC by Lukas Klass----------");
			bw.newLine();
			bw.newLine();
			for (int i=0; i<Werkzeugdaten.length; i++) {
				bw.write(Werkzeugdaten[i][0] + "\t" + Werkzeugdaten[i][1] + "\t" + Werkzeugdaten[i][2] + "\t" + Werkzeugdaten[i][3]);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[][] getWerkzeugdaten() {
		for (int i=0; i<Werkzeugdaten.length; i++) {
			for (int j=0; j<Werkzeugdaten[0].length; j++) {
				if(WerkzeugTable.getValueAt(i,j) != null) {
					Werkzeugdaten[i][j]=WerkzeugTable.getValueAt(i,j).toString();
				} else {
					Werkzeugdaten[i][j]="";
				}
			}
		}
		return Werkzeugdaten;
	}
}
