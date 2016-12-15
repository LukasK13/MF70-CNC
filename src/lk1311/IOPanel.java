package lk1311;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.*;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.system.SystemInfo;

public class IOPanel {
	public GpioPinDigitalOutput[] Output = new GpioPinDigitalOutput[14];
	public GpioPinDigitalInput[] Input = new GpioPinDigitalInput[13];
	public Hauptfenster Hauptfenster;

	public IOPanel(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
		JPanel IOPanel = new JPanel();
		IOPanel.setBackground(Color.LIGHT_GRAY);
		IOPanel.setLayout(null);

		Output[0] = Hauptfenster.Fräsenrelais;
		Output[1] = Hauptfenster.Schrittrelais;
		Output[2] = Hauptfenster.Clock2;
		Output[3] = Hauptfenster.CW2;
		Output[4] = Hauptfenster.HF2;
		Output[5] = Hauptfenster.Clock3;
		Output[6] = Hauptfenster.CW3;
		Output[7] = Hauptfenster.HF3;
		Output[8] = Hauptfenster.Clock1;
		Output[9] = Hauptfenster.CW1;
		Output[10] = Hauptfenster.HF1;		
		Output[11] = Hauptfenster.Clock4;
		Output[12] = Hauptfenster.CW4;
		Output[13] = Hauptfenster.HF4;

		Input[0] = Hauptfenster.EndschalterXPlus;
		Input[1] = Hauptfenster.EndschalterXMinus;
		Input[2] = Hauptfenster.EndschalterYPlus;
		Input[3] = Hauptfenster.EndschalterYMinus;
		Input[4] = Hauptfenster.EndschalterZPlus;
		Input[5] = Hauptfenster.EndschalterZMinus;
		Input[6] = Hauptfenster.EndschalterAPlus;
		Input[7] = Hauptfenster.EndschalterAMinus;
		Input[8] = Hauptfenster.Notaus;
		Input[9] = Hauptfenster.Control;


		JScrollPane InputTableScroll = new JScrollPane();
		InputTableScroll.setBounds(0, 10, 550, 703);
		IOPanel.add(InputTableScroll);
		JTable InputTable = new JTable();
		InputTable.setShowHorizontalLines(true);
		InputTable.setShowVerticalLines(true);
		InputTable.setRowHeight(25);
		InputTable.setFont(new Font("Tahoma", Font.PLAIN, 20));
		InputTable.setRowSelectionAllowed(false);
		InputTable.setBounds(190, 67, 166, 192);
		InputTable.setModel(new DefaultTableModel(
				new Object[][] {
						{"Endschalter X+", null},
						{"Endschalter X-", null},
						{"Endschalter Y+", null},
						{"Endschalter Y-", null},
						{"Endschalter Z+", null},
						{"Endschalter Z-", null},
						{"Endschalter A+", null},
						{"Endschalter A-", null},
						{"Notaus", null},
						{"Control", null},
						{"Vorschub Eilgang", null},
						{"Vorschub", null},
						{"Drehzahl", null},
						{"Chopperspannung", null},
						{"CPU-Temperatur", null},
						{"CPU-Spannung", null},
						{null, null},
						{"Achslänge X", null},
						{"Achslänge Y", null},
						{"Achslänge Z", null},
						{"Version", "MF70CNC v1.0"},
						{"Copyright", "Lukas Klass"},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
						{null, null},
				},
				new String[] {"Pin Name", "Pegel"}
				) {
			boolean[] columnEditables = new boolean[] {
					false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		InputTable.getColumnModel().getColumn(0).setResizable(false);
		InputTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		InputTable.getColumnModel().getColumn(0).setMinWidth(150);
		InputTable.getColumnModel().getColumn(1).setResizable(false);
		InputTable.getColumn("Pin Name").setCellRenderer(new ColoredTableCellRenderer());
		InputTable.getColumn("Pegel").setCellRenderer(new ColoredTableCellRenderer());
		try {
			InputTable.setValueAt(SystemInfo.getCpuTemperature() + " °C", 14, 1);
		} catch (NumberFormatException | IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			InputTable.setValueAt(SystemInfo.getCpuVoltage() + " V", 15, 1);
		} catch (NumberFormatException | IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
		InputTable.setValueAt(Hauptfenster.CNC.getxAchse().getAchslänge() + " mm", 17, 1);
		InputTable.setValueAt(Hauptfenster.CNC.getyAchse().getAchslänge() + " mm", 17, 1);
		InputTable.setValueAt(Hauptfenster.CNC.getzAchse().getAchslänge() + " mm", 17, 1);
		InputTableScroll.setViewportView(InputTable);


		JScrollPane OutputTableScroll = new JScrollPane();
		OutputTableScroll.setBounds(610, 10, 550, 703);
		IOPanel.add(OutputTableScroll);
		JTable OutputTable = new JTable();
		OutputTable.setShowVerticalLines(true);
		OutputTable.setShowHorizontalLines(true);
		OutputTable.setRowHeight(25);
		OutputTable.setFont(new Font("Tahoma", Font.PLAIN, 20));
		OutputTable.setRowSelectionAllowed(false);
		OutputTable.setBounds(190, 67, 166, 192);
		OutputTable.setModel(new DefaultTableModel(
				new Object[][] {
						{"Fräsenrelais", null, null, null},
						{"Schrittmotorrelais", null, null, null},
						{"Clock Z", null, null, null},
						{"Richtung Z", null, null, null},
						{"Schrittweite Z", null, null, null},
						{"Clock Y", null, null, null},
						{"Richtung Y", null, null, null},
						{"Schrittweite Y", null, null, null},
						{"Clock X", null, null, null},
						{"Richtung X", null, null, null},
						{"Schrittweite X", null, null, null},
						{"Clock A", null, null, null},
						{"Richtung A", null, null, null},
						{"Schrittweite A", null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
				},
				new String[] {"Pin Name", "Pegel", "High", "Low"}
				) {
			boolean[] columnEditables = new boolean[] {false, false, true, true};

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		OutputTable.getColumnModel().getColumn(0).setResizable(false);
		OutputTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		OutputTable.getColumnModel().getColumn(0).setMinWidth(150);
		OutputTable.getColumnModel().getColumn(1).setResizable(false);
		OutputTable.getColumn("Pin Name").setCellRenderer(new ColoredTableCellRenderer());
		OutputTable.getColumn("Pegel").setCellRenderer(new ColoredTableCellRenderer());
		OutputTable.getColumn("High").setCellRenderer(new ButtonRenderer());
		OutputTable.getColumn("High").setCellEditor(new ButtonEditorHigh(this, OutputTable));
		OutputTable.getColumn("Low").setCellRenderer(new ButtonRenderer());
		OutputTable.getColumn("Low").setCellEditor(new ButtonEditorLow(this, OutputTable));
		OutputTableScroll.setViewportView(OutputTable);
		
		for(int i=0; i<Output.length; i++) {
			OutputTable.setValueAt((Output[i].getState() == PinState.HIGH) ? "High" : "Low", i, 1);
		}
		
		
		for(int i=0; i<=9; i++) {
			InputTable.setValueAt((Input[i].getState() == PinState.HIGH) ? "High" : "Low", i, 1);		
			Input[i].addListener(new GpioPinListenerDigital() {
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
					InputTable.setValueAt((event.getState() == PinState.HIGH) ? "High" : "Low", Index(Input, (GpioPinDigitalInput) event.getPin()), 1);
				}
			});
		}

		Hauptfenster.ADC.addMCP3008Listener(new MCP3008Listener() {
			public void ValueChanged(MCP3008Event e) {
				if(e.getChannel()==0) {
					InputTable.setValueAt((double) ((int) (e.getValue() / 1023.0 * 330.0)) / 100.0 + "V", 10, 1);
				} else if(e.getChannel()==1) {	
					InputTable.setValueAt((double) ((int) (e.getValue() / 1023.0 * 330.0)) / 100.0 + "V", 11, 1);
				} else if(e.getChannel()==2) {
					InputTable.setValueAt((double) ((int) (e.getValue() / 1023.0 * 330.0)) / 100.0 + "V", 12, 1);
				} else if(e.getChannel()==3) {
					InputTable.setValueAt((double) ((int) (e.getValue() / 1023.0 * 330.0 * 2.0)) / 100.0 + "V", 13, 1);
				}
			}
		});
		Hauptfenster.ADC.SendEvent();

		Hauptfenster.splitPaneGroß.setLeftComponent(IOPanel);
		Hauptfenster.splitPaneGroß.setDividerLocation(1160);

	}

	private int Index(GpioPinDigitalInput[] PinArray, GpioPinDigitalInput Pin) {
		int i=0;
		while(i<PinArray.length && PinArray[i] != Pin) {
			i++;
		}
		return i;
	}
}


class ColoredTableCellRenderer extends DefaultTableCellRenderer {	
	public void setValue( Object value ) {
		if (value instanceof String) {
			if(value.equals("High")) {
				setBackground(Color.GREEN);
			} else if(value.equals("Low")) {
				setBackground(Color.RED);
			} else {
				setBackground(Color.WHITE);
			}
			setText(value.toString());
		} else if(value==null) {
			setBackground(Color.WHITE);
			setText("");
		} else {
			super.setValue( value );
		}
	}
}


class ButtonRenderer extends JButton implements TableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(Color.BLACK);
			setBackground(Color.GRAY);
		}
		setText((value == null) ? "" : value.toString());
		return this;
	}
}


class ButtonEditorHigh extends AbstractCellEditor implements TableCellEditor{
	protected JButton[] ButtonHigh;
	private String ButtonHighLabel;
	public int i;

	public ButtonEditorHigh(IOPanel ioPanel, JTable Table) {
		ButtonHigh = new JButton[27];
		for(i=0; i<ButtonHigh.length;i++) {
			ButtonHigh[i] = new JButton();
			ButtonHigh[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int Index = Index(ButtonHigh, (JButton) e.getSource());
					if(Index < ioPanel.Output.length) {
						Table.setValueAt("High", Index, 1);
						ioPanel.Output[Index].setState(PinState.HIGH);;
					}
				}
			});
		}		
	}

	private int Index(JButton[] ButtonArray, JButton Button) {
		int i=0;
		while(i<ButtonArray.length && ButtonArray[i] != Button) {
			i++;
		}
		return i;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			ButtonHigh[row].setForeground(table.getSelectionForeground());
			ButtonHigh[row].setBackground(table.getSelectionBackground());
		} else {
			ButtonHigh[row].setForeground(Color.BLACK);
			ButtonHigh[row].setBackground(Color.GRAY);
		}
		ButtonHighLabel = (value == null) ? "" : value.toString();
		ButtonHigh[row].setText(ButtonHighLabel);
		return ButtonHigh[row];
	}

	public Object getCellEditorValue() {
		return ButtonHighLabel;
	}
}


class ButtonEditorLow extends AbstractCellEditor implements TableCellEditor{
	protected JButton[] ButtonLow;
	private String ButtonLowLabel;
	public int i;

	public ButtonEditorLow(IOPanel ioPanel, JTable Table) {
		ButtonLow = new JButton[27];
		for(i=0; i<ButtonLow.length;i++) {
			ButtonLow[i] = new JButton();
			ButtonLow[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int Index = Index(ButtonLow, (JButton) e.getSource());
					if(Index < ioPanel.Output.length) {
						Table.setValueAt("Low", Index, 1);
						ioPanel.Output[Index].setState(PinState.LOW);
					}					
				}
			});
		}		
	}

	private int Index(JButton[] ButtonArray, JButton Button) {
		int i=0;
		while(i<ButtonArray.length && ButtonArray[i] != Button) {
			i++;
		}
		return i;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			ButtonLow[row].setForeground(table.getSelectionForeground());
			ButtonLow[row].setBackground(table.getSelectionBackground());
		} else {
			ButtonLow[row].setForeground(Color.BLACK);
			ButtonLow[row].setBackground(Color.GRAY);
		}
		ButtonLowLabel = (value == null) ? "" : value.toString();
		ButtonLow[row].setText(ButtonLowLabel);
		return ButtonLow[row];
	}

	public Object getCellEditorValue() {
		return ButtonLowLabel;
	}
}
