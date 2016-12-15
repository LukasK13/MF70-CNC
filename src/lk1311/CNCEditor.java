package lk1311;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

public class CNCEditor{
	private Hauptfenster Hauptfenster;
	public JPanel EditorPanel;
	private JTextPane Editor;
	private File Datei = null;
	private boolean gespeichert = false;
	private boolean TextWirdVerändert = false;
	private boolean DateinameVorhanden = false;
	private boolean CodeGeprüft = false;
	private String Clipboard;
	private RückgängigArray[] RückgängigArray = new RückgängigArray[1000];
	private int RückgängigIndex = 0;
	private String RückgängigCode = "";

	public CNCEditor(Hauptfenster Hauptfenster) {
		this.Hauptfenster = Hauptfenster;
		EditorPanel = new JPanel();
		EditorPanel.setBackground(Color.LIGHT_GRAY);
		EditorPanel.setLayout(null);

		for(int i=0; i<RückgängigArray.length; i++) {
			RückgängigArray[i] = new RückgängigArray();
		}

		final StyleContext cont = StyleContext.getDefaultStyleContext();
		final AttributeSet Schwarz = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
		final AttributeSet Rot = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.RED);		
		final AttributeSet Blau = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
		final AttributeSet Grün = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.GREEN);
		final AttributeSet Magenta = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.MAGENTA);
		final AttributeSet Cyan = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.CYAN);
		final AttributeSet Pink = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.PINK);
		final AttributeSet Grau = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.GRAY);
		final AttributeSet Orange = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.ORANGE);

		DefaultStyledDocument doc = new DefaultStyledDocument() {
			public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
				if(str.length()==1) {
					super.insertString(offset, Character.toString(Character.toUpperCase(str.charAt(0))), a);
				} else {
					super.insertString(offset, str, a);
				}				

				String text = getText(0, getLength());
				int start = findPreviousCarriageReturn(text, offset);				

				if(start != 0) {
					if(!String.valueOf(text.charAt(start+1)).matches("N")) {
						super.insertString(start+1, "N" + getLineOfOffset(this, start) + " ", Grau);
					}
				} else {
					if(!String.valueOf(text.charAt(start)).matches("N")) {
						super.insertString(start, "N0 ", Grau);
					}
				}

				text = getText(0, getLength());
				int before = findPreviousNonWordChar(text, offset);
				int after = findNextNonWordChar(text, offset + str.length());
				int iterator = before;

				String[] temp;
				Pattern Buchstaben = Pattern.compile("[a-zA-Z]");

				while (iterator <= after) {			
					if (iterator == after || String.valueOf(text.charAt(iterator)).matches("\\s")) {

						if (text.substring(before, iterator).matches("(\\s)*(G00|G0|G01|G1)")) {
							setCharacterAttributes(before, iterator - before, Blau, false);
						} else if (text.substring(before, iterator).matches("(\\s)*(G02|G2|G03|G3|G04|G4|G05|G5)")) {
							setCharacterAttributes(before, iterator - before, Rot, false);
						} else if (text.substring(before, iterator).matches("(\\s)*(G40|G41|G42)")) {
							setCharacterAttributes(before, iterator - before, Grün, false);
						} else if (text.substring(before, iterator).matches("(\\s)*(G72)")) {
							setCharacterAttributes(before, iterator - before, Magenta, false);
						} else if (text.substring(before, iterator).matches("(\\s)*(G90|G91|G92)")) {
							setCharacterAttributes(before, iterator - before, Cyan, false);
						} else if (text.substring(before, iterator).matches("(\\s)*(M00|M0|M02|M2|M03|M3|M05|M5)")) {
							setCharacterAttributes(before, iterator - before, Pink, false);
						} else if (text.substring(before, iterator).matches("(\\s)*(N)(\\d)+")) {
							setCharacterAttributes(before, iterator - before, Grau, false);
						} else if (text.substring(before, iterator).matches("(\\s)*(//)([a-zA-Z0-9])*")) {
							setCharacterAttributes(before, iterator - before, Orange, false);
						} else if (text.substring(before, iterator).matches("(\\s)*([a-zA-Z]{1})(\\d)+([a-zA-Z]{1})(\\-)?(\\d)+(\\.)*(\\d)*")) {							
							temp = Buchstaben.split(text.substring(before, iterator));
							if(before==0) {
								insertString(before + temp[1].length() + 1, " ", Schwarz);
							} else {
								insertString(before + temp[1].length() + 2, " ", Schwarz);
							}	
						} else if (text.substring(before, iterator).matches("(\\s)*([a-zA-Z]{1})(\\-)?(\\d)+(\\.)*(\\d)*([a-zA-Z]{1})(\\-)?(\\d)+(\\.)*(\\d)*")) {														
							temp = Buchstaben.split(text.substring(before, iterator));
							if(before==0) {
								insertString(before + temp[1].length() + 1, " ", Schwarz);
							} else {
								insertString(before + temp[1].length() + 2, " ", Schwarz);
							}
						} else {
							setCharacterAttributes(before, iterator - before, Schwarz, false);
						}
						before = iterator;
					}
					iterator++;
				}
				gespeichert = false;
			}

			public void remove (int offs, int len) throws BadLocationException {
				super.remove(offs, len);

				String text = getText(0, getLength());
				int before = findPreviousNonWordChar(text, offs);
				int after = findNextNonWordChar(text, offs);
				if (text.substring(before, after).matches("(\\s)*(G00|G0|G01|G1)")) {
					setCharacterAttributes(before, after - before, Blau, false);
				} else if (text.substring(before, after).matches("(\\s)*(G02|G2|G03|G3|G04|G4|G05|G5)")) {
					setCharacterAttributes(before, after - before, Rot, false);
				} else if (text.substring(before, after).matches("(\\s)*(G40|G41|G42)")) {
					setCharacterAttributes(before, after - before, Grün, false);
				} else if (text.substring(before, after).matches("(\\s)*(G72)")) {
					setCharacterAttributes(before, after - before, Magenta, false);
				} else if (text.substring(before, after).matches("(\\s)*(G90|G91|G92)")) {
					setCharacterAttributes(before, after - before, Cyan, false);
				} else if (text.substring(before, after).matches("(\\s)*(M00|M0|M02|M2|M03|M3|M05|M5)")) {
					setCharacterAttributes(before, after - before, Pink, false);
				} else if (text.substring(before, after).matches("(\\s)*(//)([a-zA-Z0-9])*")) {
					setCharacterAttributes(before, after - before, Orange, false);
					/*
				} else if (text.substring(before, after).matches("(\\s)*([a-zA-Z]{1})(\\d)+([a-zA-Z]{1})(\\d)+(\\.)*(\\d)*")) {							
					Pattern p = Pattern.compile("[a-zA-Z]");
					String[] temp = p.split(text.substring(before, after));
					if(before==0) {
						insertString(before + temp[1].length() + 1, " ", Schwarz);
					} else {
						insertString(before + temp[1].length() + 2, " ", Schwarz);
					}	
				} else if (text.substring(before, after).matches("(\\s)*([a-zA-Z]{1})(\\d)+(\\.)*(\\d)*([a-zA-Z]{1})(\\d)+(\\.)*(\\d)*")) {							
					Pattern Buchstaben = Pattern.compile("[a-zA-Z]");
					String[] temp = Buchstaben.split(text.substring(before, after));
					if(before==0) {
						insertString(before + temp[1].length() + 1, " ", Schwarz);
					} else {
						insertString(before + temp[1].length() + 2, " ", Schwarz);
					}*/
				} else {
					setCharacterAttributes(before, after - before, Schwarz, false);
				}	
				gespeichert = false;
			}		
		};

		Editor = new JTextPane(doc);
		Editor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				gespeichert=false;
				CodeGeprüft=false;
				if (!TextWirdVerändert) {
					RückgängigUpdate(arg0);
				}
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				gespeichert=false;
				CodeGeprüft=false;
				if (!TextWirdVerändert) {
					RückgängigUpdate(arg0);
				}
			}
			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}
		});
		Editor.setFont(new Font("Monospaced", Font.PLAIN, 13));

		JScrollPane EditorScroll = new JScrollPane(Editor);
		EditorScroll.setBounds(0, 0, 1157, 499);

		EditorPanel.add(EditorScroll);
	}

	private int findPreviousNonWordChar (String text, int index) {
		while (index > 0) {
			index--;
			if (String.valueOf(text.charAt(index)).matches("\\s")) {
				break;
			}			
		}
		return index;
	}

	private int findNextNonWordChar (String text, int index) {
		while (index < text.length()) {
			if (String.valueOf(text.charAt(index)).matches("\\s")) {
				break;
			}
			index++;
		}
		return index;
	}

	private int findPreviousCarriageReturn (String text, int index) {
		while (index > 0) {
			index--;
			if (Character.toString(text.charAt(index)).equals("\n")) {
				break;
			}			
		}
		return index;
	}

	private int getLineOfOffset(Document doc, int offset) throws BadLocationException {
		//Document doc = comp.getDocument();
		if (offset < 0) {
			throw new BadLocationException("Can't translate offset to line", -1);
		} else if (offset > doc.getLength()) {
			throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
		} else {
			return doc.getDefaultRootElement().getElementIndex(offset) + 1;
		}
	}

	private int getLineStartOffset(JTextComponent comp, int line) throws BadLocationException {
		Element map = comp.getDocument().getDefaultRootElement();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= map.getElementCount()) {
			throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
		} else {
			Element lineElem = map.getElement(line);
			return lineElem.getStartOffset();
		}
	}

	private int getLineEndOffset(JTextComponent comp, int line) throws BadLocationException {
		Element map = comp.getDocument().getDefaultRootElement();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= map.getElementCount()) {
			throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
		} else {
			Element lineElem = map.getElement(line);
			return lineElem.getEndOffset();
		}
	}

	private void RückgängigUpdate(DocumentEvent arg0) {		
		if(RückgängigIndex<0) {
			RückgängigIndex = 0;
			RückgängigArray[RückgängigIndex].setCaretPosition(0);
			RückgängigArray[RückgängigIndex].setCode("");
			RückgängigArray[RückgängigIndex].setLength(0);
		}
		if(RückgängigIndex==998 ) {
			for(int i=0; i<RückgängigArray.length-1; i++) {
				RückgängigArray[i] = RückgängigArray[i+1];
			}
		}
		if (arg0.getType() == DocumentEvent.EventType.INSERT) {
			if((RückgängigArray[RückgängigIndex].getCaretPosition() + RückgängigArray[RückgängigIndex].getLength() == arg0.getOffset()) && (RückgängigArray[RückgängigIndex].getMode()==false)
					&& !(Editor.getText().substring(arg0.getOffset(), arg0.getOffset() + arg0.getLength()).matches("\\s"))) {
				RückgängigArray[RückgängigIndex].setLength(RückgängigArray[RückgängigIndex].getLength() + arg0.getLength());
				RückgängigCode = Editor.getText();
			} else {
				RückgängigArray[RückgängigIndex + 1].setCaretPosition(arg0.getOffset());
				RückgängigArray[RückgängigIndex + 1].setLength(arg0.getLength());
				RückgängigArray[RückgängigIndex + 1].setMode(false);
				RückgängigCode = Editor.getText();
				RückgängigIndex++;
			}
		} else if (arg0.getType() == DocumentEvent.EventType.REMOVE) {
			try {
			if((RückgängigArray[RückgängigIndex].getCaretPosition() - RückgängigArray[RückgängigIndex].getLength() == arg0.getOffset()) && (RückgängigArray[RückgängigIndex].getMode()==true)){
				RückgängigArray[RückgängigIndex].setLength(RückgängigArray[RückgängigIndex].getLength() + arg0.getLength());
				RückgängigArray[RückgängigIndex].setCaretPosition(arg0.getOffset());
				try {
					if(getLineOfOffset(Editor.getDocument(), arg0.getOffset())==1) {
						RückgängigArray[RückgängigIndex].setCode(RückgängigCode.substring(arg0.getOffset(), arg0.getOffset() + arg0.getLength()).concat(RückgängigArray[RückgängigIndex].getCode()));
					} else {
						RückgängigArray[RückgängigIndex].setCode(RückgängigCode.substring(arg0.getOffset() + getLineOfOffset(Editor.getDocument(), arg0.getOffset()) - 1, arg0.getOffset() + arg0.getLength() + getLineOfOffset(Editor.getDocument(), arg0.getOffset()) - 1).concat(RückgängigArray[RückgängigIndex].getCode()));
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				RückgängigCode = Editor.getText();
			} else {
				RückgängigArray[RückgängigIndex + 1].setCaretPosition(arg0.getOffset());
				RückgängigArray[RückgängigIndex + 1].setLength(arg0.getLength());
				try {
					if(getLineOfOffset(Editor.getDocument(), arg0.getOffset())==1) {
						RückgängigArray[RückgängigIndex+1].setCode(RückgängigCode.substring(arg0.getOffset(), arg0.getOffset() + arg0.getLength()));
					} else {
						RückgängigArray[RückgängigIndex+1].setCode(RückgängigCode.substring(arg0.getOffset() + getLineOfOffset(Editor.getDocument(), arg0.getOffset()) - 1, arg0.getOffset() + arg0.getLength() + getLineOfOffset(Editor.getDocument(), arg0.getOffset()) - 1));
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				RückgängigArray[RückgängigIndex + 1].setMode(true);
				RückgängigCode = Editor.getText();
				RückgängigIndex++;
			}
			} catch(StringIndexOutOfBoundsException e) {
				
			}
		}
		try {
			if(getLineOfOffset(Editor.getDocument(), arg0.getOffset())==1) {
				//System.out.println(RückgängigCode.substring(arg0.getOffset(), arg0.getOffset() + arg0.getLength()) + "   " + (arg0.getOffset()) + "   " + (arg0.getOffset() + arg0.getLength()) + "   " + getLineOfOffset(Editor.getDocument(), arg0.getOffset()));
			} else {
				//System.out.println(RückgängigCode.substring(arg0.getOffset() + getLineOfOffset(Editor.getDocument(), arg0.getOffset()) - 1, getLineOfOffset(Editor.getDocument(), arg0.getOffset()) - 1 + arg0.getOffset() + arg0.getLength()));
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if(RückgängigIndex==999 ) {
			RückgängigIndex=998;
		}
	}

	public void Rückgängig() {
		if(RückgängigIndex>=0) {
			if(RückgängigArray[RückgängigIndex].getMode()) {
				try {
					TextWirdVerändert = true;
					Editor.getDocument().insertString(RückgängigArray[RückgängigIndex].getCaretPosition(), RückgängigArray[RückgängigIndex].getCode(), null);
					TextWirdVerändert = false;
					RückgängigIndex--;
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				try {
					TextWirdVerändert = true;
					Editor.getDocument().remove(RückgängigArray[RückgängigIndex].getCaretPosition(), RückgängigArray[RückgängigIndex].getLength());
					TextWirdVerändert = false;
					RückgängigIndex--;
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void Reset() {
		TextWirdVerändert = true;
		Editor.setText("");
		Datei = null;
		DateinameVorhanden = false;
		gespeichert = false;
		TextWirdVerändert = false;
	}

	public void DateiNeu() {
		int rückgabe = JOptionPane.showOptionDialog(null, "Neue Datei erstellen?","Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, new String[]{"Ja", "Nein", "speichern und schließen"}, "Nein");
		if (rückgabe==JOptionPane.YES_OPTION) {		//Bei Ja wird das Programm zurückgesetzt
			Reset();
		}
		else if (rückgabe==JOptionPane.CANCEL_OPTION) {		//Bei Speichern und schließen wird das Programm gespeichert und zurückgesetzt
			if (DateiSpeichern(false) == true) {
				Reset();
			}			
		}
	} //End DateiNeu()

	public boolean DateiSpeichern(boolean SpeichernUnter) {
		boolean erfolg = false;

		if (DateinameVorhanden==false || SpeichernUnter) {
			JFileChooser Browser = new JFileChooser();
			FileNameExtensionFilter Filter = new FileNameExtensionFilter("MF70 Fräsprogramm", "mf70");
			if(System.getProperty("os.name").equals("Windows 7")) {
				Browser.setCurrentDirectory(new File("C:/Users/Lukas/Documents"));
			} else if(System.getProperty("os.name").equals("Linux")) {
				Browser.setCurrentDirectory(new File("/home/pi/Desktop/Lukas")); 
			}
			Browser.setFileFilter(Filter);
			if(Browser.showSaveDialog(EditorPanel) == JFileChooser.APPROVE_OPTION) {
				String Pfad=Browser.getSelectedFile().getPath();
				if (Pfad.endsWith(".mf70") == false) {
					Pfad = Pfad + ".mf70";
				}	
				Datei = new File(Pfad);
			}
		}

		if (Datei != null) {
			if (Datei.exists() == true) {
				Datei.delete();
			}
			try {
				FileWriter writer = new FileWriter(Datei ,true); 
				writer.write("----------MF70 Fräsdaten erstellt mit MF70 CNC by Lukas Klass----------\n\n");
				//writer.write(System.getProperty("line.separator"));

				String Puffer = Editor.getText();
				writer.write(Puffer);

				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			DateinameVorhanden=true;
			gespeichert = true;
			erfolg = true;
		}
		return erfolg;
	}

	public void DateiÖffnen() {
		if (Editor.getDocument().getLength() != 0 && !gespeichert) {
			int rückgabe = JOptionPane.showOptionDialog(null, "Neue Datei öffnen?","Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, new String[]{"Ja", "Nein", "speichern und öffnen"}, "speichern und öffnen");
			if (rückgabe==JOptionPane.YES_OPTION) {
				Reset();
			}
			else if (rückgabe==JOptionPane.NO_OPTION | rückgabe==JOptionPane.CLOSED_OPTION) {
				return;
			}
			else if (rückgabe==JOptionPane.CANCEL_OPTION) {
				if (DateiSpeichern(false) == true) {
					Reset();
				} else {
					return;
				}
			}
		}
		JFileChooser Browser = new JFileChooser();
		if(System.getProperty("os.name").equals("Windows 7")) {
			Browser.setCurrentDirectory(new File("C:/Users/Lukas/Documents"));
		} else if(System.getProperty("os.name").equals("Linux")) {
			Browser.setCurrentDirectory(new File("/home/pi/Desktop/Lukas")); 
		}		
		FileNameExtensionFilter Filter = new FileNameExtensionFilter("MF70 Fräsprogramm", "mf70");
		Browser.setFileFilter(Filter);
		int rueckgabeWert = Browser.showOpenDialog(EditorPanel);
		if(rueckgabeWert == JFileChooser.APPROVE_OPTION)	{
			Reset();
			Datei=Browser.getSelectedFile();			
			try {
				FileReader fr = new FileReader(Datei);
				BufferedReader br = new BufferedReader(fr);
				String temp = br.readLine();		
				temp = br.readLine();	
				String[] tempSplit, tempSplitNumber;
				TextWirdVerändert = true;
				for (int i=0; i<=Datei.length(); i++) {
					temp = br.readLine();
					if (temp != null) {
						try {
							tempSplit = temp.split("\n");
							if(tempSplit[0].startsWith("N")) {
								tempSplitNumber = tempSplit[0].split(" ", 2);
								Editor.getDocument().insertString(Editor.getDocument().getLength(), tempSplitNumber[1] + "\n", null);
							} else {
								Editor.getDocument().insertString(Editor.getDocument().getLength(), tempSplit[0] + "\n", null);
							}

						} catch (Exception ex) {
							ex.printStackTrace();
						}	  	        		
					}
				} 
				TextWirdVerändert = false;
				RückgängigCode = Editor.getText();
				gespeichert = true;
				DateinameVorhanden = true;
				br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}		    
		} 
	}

	public void Kopieren() {
		Clipboard = Editor.getSelectedText();
	}

	public void Einfügen() {
		try {
			Editor.getDocument().insertString(Editor.getCaretPosition(), Clipboard, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void Schriftgröße() {
		SchriftgrößeDialog SchriftgrößeDialog = new SchriftgrößeDialog(Hauptfenster.Fenster,Editor.getFont().getSize());
		SchriftgrößeDialog.setVisible(true);
		Editor.setFont(new Font("Monospaced", Font.PLAIN, SchriftgrößeDialog.getSchriftgröße()));
	}

	public void GeheZuZeile() {
		GeheZuZeileDialog GeheZuZeileDialog = new GeheZuZeileDialog(Hauptfenster.Fenster);
		GeheZuZeileDialog.setVisible(true);
		if(GeheZuZeileDialog.getZeile() >= 0) {
			try {
				Editor.setCaretPosition(getLineStartOffset(Editor, GeheZuZeileDialog.getZeile()));
				Editor.requestFocus();
			} catch (BadLocationException e) {
				System.err.println("Zeile " + GeheZuZeileDialog.getZeile() + " nicht vorhanden");
			}
		}
	}

	public void Suchen() {
		SuchenDialog SuchenDialog = new SuchenDialog(Hauptfenster.Fenster);
		SuchenDialog.setVisible(true);
		if(SuchenDialog.getSuchText() != null) {
			try {
				if(Editor.getText(Editor.getCaretPosition(), Editor.getDocument().getLength() - Editor.getCaretPosition()).indexOf(SuchenDialog.getSuchText()) != -1) {
					Editor.setCaretPosition(Editor.getCaretPosition() + Editor.getText(Editor.getCaretPosition(), Editor.getDocument().getLength() - Editor.getCaretPosition()).indexOf(SuchenDialog.getSuchText()));
				} else {
					System.err.println("Suchtext wurde nicht gefunden");
				}

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			Editor.requestFocus();
		}
	}

	public void Prüfen() {
		ZOffsetDialog ZOffsetDialog = new ZOffsetDialog(Hauptfenster.Fenster);
		ZOffsetDialog.setVisible(true);
		if(ZOffsetDialog.isEingabe()) {
			String Code = Editor.getText();
			String CodeSplit[] = Code.split(System.getProperty("line.separator"));
			lk1311.Hauptfenster.Error.setText("");
			Prüfen Prüfen = new Prüfen(CodeSplit, ZOffsetDialog.getZOffset());
			Thread PrüfenThread = new Thread (Prüfen);
			PrüfenThread.setName("PrüfenThread");
			PrüfenThread.start();

			try {
				PrüfenThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}

			int k=0;
			while (PrüfenThread.isAlive()) {
				PrüfenThread.interrupt();
				k++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				if (k==5) {
					System.err.println("ThreadInterrupt " + k + " fehlgeschlagen");
					break;
				}
			}

			CodeSplit = Prüfen.getCode();

			Editor.setText("");
			for (int i=0;i<CodeSplit.length ;i++ ) {
				try {
					Editor.getDocument().insertString(Editor.getDocument().getLength(), CodeSplit[i] + "\n", null);       
				} catch (Exception ex) {
					ex.printStackTrace();
				}	
			} // end of for	

			if (Prüfen.getFehlerzahl()==0) {
				CodeGeprüft=true;
			}
		}
		lk1311.Hauptfenster.Error.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				if(ce.getDot() != ce.getMark()) {
					try {
						try {
							int start = lk1311.Hauptfenster.Error.getLineStartOffset(lk1311.Hauptfenster.Error.getLineOfOffset(lk1311.Hauptfenster.Error.getCaretPosition()));
							int ende = lk1311.Hauptfenster.Error.getLineEndOffset(lk1311.Hauptfenster.Error.getLineOfOffset(lk1311.Hauptfenster.Error.getCaretPosition()));
							int Fehlerzeile = Integer.parseInt(lk1311.Hauptfenster.Error.getText(start, ende-start).split(" ")[2].substring(1));
							Editor.select(getLineStartOffset(Editor, Fehlerzeile - 1), getLineEndOffset(Editor, Fehlerzeile - 1)-1);
							Editor.requestFocus();
						} catch (NumberFormatException e) {	
							System.err.println(getClass() + "Angeklickte Zeile konnte nicht ermittelt werden.");
						}

					} catch (BadLocationException e) {
						System.err.println(getClass() + "Angeklickte Zeile konnte nicht ermittelt werden.");
					}
				} 
			}
		});		
	}

	public String getText() {
		return Editor.getText();
	}
	
	public boolean isCodeGeprüft() {
		return CodeGeprüft;
	}
	
	public JTextPane getEditor() {
		return Editor;
	}
}

class RückgängigArray {
	private String Code;
	private int CaretPosition;
	private int Length;
	//Insert (true) bedeutet Text wurde gelöscht, Remove (false) bedeutet Text wurde eingefügt
	private boolean Mode;

	public RückgängigArray(String Code, int CaretPosition, int Length, boolean Mode) {
		this.Code = Code;
		this.CaretPosition = CaretPosition;
		this.Length = Length;
		this.Mode = Mode;
	}

	public RückgängigArray() {		
	}

	public void setCode(String Code) {
		this.Code = Code;
	}

	public void setCaretPosition(int CaretPosition) {
		this.CaretPosition = CaretPosition;
	}

	public void setLength(int Length) {
		this.Length = Length;
	}

	public void setMode(boolean Mode) {
		this.Mode = Mode;
	}

	public String getCode() {
		return Code;
	}

	public int getCaretPosition() {
		return CaretPosition;
	}

	public int getLength() {
		return Length;
	}

	public boolean getMode() {
		return Mode;
	}
}


class SchriftgrößeDialog extends JDialog{
	private JNumberField SchriftgrößeNum;
	private int Schriftgröße;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SchriftgrößeDialog (JFrame parent, int SchriftgrößeAlt) {
		super(parent,"Schriftgröße", true);
		this.Schriftgröße = SchriftgrößeAlt;	
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 11));
		setTitle("Schriftgröße");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,160);		
		getContentPane().setLayout(null);

		JButton btnÜbernehmen = new JButton("Übernehmen");
		btnÜbernehmen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(SchriftgrößeNum.getText().matches("\\d+")) {
					Schriftgröße = SchriftgrößeNum.getInt();
					dispose();
				}								
			}
		});
		btnÜbernehmen.setBounds(10, 98, 110, 23);
		getContentPane().add(btnÜbernehmen);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnAbbrechen.setBounds(274, 98, 110, 23);
		getContentPane().add(btnAbbrechen);

		SchriftgrößeNum = new JNumberField();
		SchriftgrößeNum.setInt(Schriftgröße);
		SchriftgrößeNum.setBounds(170, 40, 86, 25);
		getContentPane().add(SchriftgrößeNum);
		SchriftgrößeNum.setColumns(10);

		JComboBox SchriftgrößeBox = new JComboBox();
		SchriftgrößeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				SchriftgrößeNum.setInt(SchriftgrößeBox.getSelectedIndex() + 6);
			}
		});
		SchriftgrößeBox.setModel(new DefaultComboBoxModel(new String[] {"6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"}));
		SchriftgrößeBox.setBounds(10, 40, 150, 25);
		if((Schriftgröße >=6) && (Schriftgröße <= 30)) {
			SchriftgrößeBox.setSelectedIndex(Schriftgröße-6);
		}		
		getContentPane().add(SchriftgrößeBox);

		JLabel TitelLbl = new JLabel("Bitte Schriftgröße Wählen.");
		TitelLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TitelLbl.setBounds(10, 11, 374, 20);
		getContentPane().add(TitelLbl);		
	}

	public int getSchriftgröße() {
		return Schriftgröße;
	}
}


class GeheZuZeileDialog extends JDialog{
	private JNumberField ZeileNum;
	private int Zeile;

	public GeheZuZeileDialog (JFrame parent) {
		super(parent,"Gehe Zu Zeile", true);
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 11));
		setTitle("Gehe Zu Zeile");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,160);		
		getContentPane().setLayout(null);

		JButton btnÜbernehmen = new JButton("OK");
		btnÜbernehmen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ZeileNum.getText().matches("\\d+")) {
					Zeile = ZeileNum.getInt();
					dispose();
				}								
			}
		});
		btnÜbernehmen.setBounds(10, 98, 110, 23);
		getContentPane().add(btnÜbernehmen);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Zeile = -1;
				dispose();
			}
		});
		btnAbbrechen.setBounds(274, 98, 110, 23);
		getContentPane().add(btnAbbrechen);

		ZeileNum = new JNumberField();
		ZeileNum.setBounds(10, 40, 100, 25);
		getContentPane().add(ZeileNum);
		ZeileNum.setColumns(10);
		ZeileNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(ZeileNum.getText().matches("\\d+")) {
					Zeile = ZeileNum.getInt();
					dispose();
				}
			}		
		});

		JLabel TitelLbl = new JLabel("Gehe Zu Zeile:");
		TitelLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TitelLbl.setBounds(10, 11, 374, 20);
		getContentPane().add(TitelLbl);		
	}

	public int getZeile() {
		return Zeile;
	}
}


class SuchenDialog extends JDialog{
	private String SuchText;
	private JTextArea SuchenArea;

	public SuchenDialog (JFrame parent) {
		super(parent,"Suchen", true);
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 11));
		setTitle("Suchen");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,400);		
		getContentPane().setLayout(null);

		JButton btnÜbernehmen = new JButton("Suchen");
		btnÜbernehmen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!SuchenArea.getText().equals("")) {
					SuchText = SuchenArea.getText();
					dispose();	
				}											
			}
		});
		btnÜbernehmen.setBounds(10, 338, 110, 23);
		getContentPane().add(btnÜbernehmen);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SuchText = null;
				dispose();
			}
		});
		btnAbbrechen.setBounds(274, 338, 110, 23);
		getContentPane().add(btnAbbrechen);

		JLabel TitelLbl = new JLabel("Suche Text:");
		TitelLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TitelLbl.setBounds(10, 11, 374, 20);
		getContentPane().add(TitelLbl);		

		JScrollPane SuchenScroll = new JScrollPane();
		SuchenScroll.setBounds(10, 42, 374, 285);
		getContentPane().add(SuchenScroll);

		SuchenArea = new JTextArea();
		SuchenScroll.setViewportView(SuchenArea);
	}

	public String getSuchText() {
		return SuchText;
	}
}


class ZOffsetDialog extends JDialog{
	private JNumberField ZOffsetNum;
	private boolean Eingabe=false;
	private double ZOffset;

	public ZOffsetDialog (JFrame parent) {
		super(parent,"Z-Offset", true);
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 11));
		setTitle("Z-Offset");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,160);		
		getContentPane().setLayout(null);

		JButton btnÜbernehmen = new JButton("OK");
		btnÜbernehmen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ZOffsetNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					ZOffset = ZOffsetNum.getDouble();
					Eingabe=true;
					dispose();
				}								
			}
		});
		btnÜbernehmen.setBounds(10, 98, 110, 23);
		getContentPane().add(btnÜbernehmen);

		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Eingabe=false;
				dispose();
			}
		});
		btnAbbrechen.setBounds(274, 98, 110, 23);
		getContentPane().add(btnAbbrechen);

		ZOffsetNum = new JNumberField();
		ZOffsetNum.setBounds(10, 40, 100, 25);
		getContentPane().add(ZOffsetNum);
		ZOffsetNum.setColumns(10);
		ZOffsetNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(ZOffsetNum.getText().matches("(\\d)+(\\.)*(\\d)*")) {
					ZOffset = ZOffsetNum.getDouble();
					Eingabe=true;
					dispose();
				}
			}		
		});

		JLabel TitelLbl = new JLabel("Z-Offset:");
		TitelLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		TitelLbl.setBounds(10, 11, 374, 20);
		getContentPane().add(TitelLbl);		
	}

	public double getZOffset() {
		return ZOffset;
	}

	public boolean isEingabe() {
		return Eingabe;
	}
}
