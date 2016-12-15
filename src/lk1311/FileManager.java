package lk1311;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;



import java.util.*;
import java.util.List;
import java.io.*;

class FileManager {

	/** Title of the application */
	public static final String APP_TITLE = "FileManager";

	/** Provides nice icons and names for files. */
	private FileSystemView fileSystemView;

	/** currently selected File. */
	private File currentFile;

	/** Main GUI container */
	private JPanel gui;

	/** File-system tree. Built Lazily */
	private JTree tree;
	private DefaultTreeModel treeModel;

	/** Directory listing */
	private JTable table;
	/** Table model for File[]. */
	private FileTableModel fileTableModel;
	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;
	private int rowIconPadding = 6;
	private JButton deleteFile;
	/* File details. */
	private JLabel fileName;
	private JTextField path;
	private JLabel date;
	private JLabel size;

	public Container getGui() {
		if (gui==null) {
			gui = new JPanel(new BorderLayout(3,3));

			fileSystemView = FileSystemView.getFileSystemView();

			JPanel detailView = new JPanel(new BorderLayout(3,3));

			table = new JTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setAutoCreateRowSorter(true);
			table.setShowVerticalLines(false);

			listSelectionListener = new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent lse) {
					int row = table.getSelectionModel().getLeadSelectionIndex();
					setFileDetails( ((FileTableModel)table.getModel()).getFile(row) );
				}
			};
			table.getSelectionModel().addListSelectionListener(listSelectionListener);
			JScrollPane tableScroll = new JScrollPane(table);
			Dimension d = tableScroll.getPreferredSize();
			tableScroll.setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
			detailView.add(tableScroll, BorderLayout.CENTER);

			// the File tree
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);

			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
					showChildren(node);
					setFileDetails((File)node.getUserObject());
				}
			};

			// show the file system roots.
			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add( node );
				//showChildren(node);
				//
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);
				for (File file : files) {
					if (file.isDirectory()) {
						node.add(new DefaultMutableTreeNode(file));
					}
				}
				//
			}

			tree = new JTree(treeModel);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);

			// as per trashgod tip
			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(
					200,
					(int)preferredSize.getHeight());
			treeScroll.setPreferredSize( widePreferred );

			// details for a File
			JPanel fileMainDetails = new JPanel(new BorderLayout(4,2));
			fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

			JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
			fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

			JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
			fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

			fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
			fileName = new JLabel();
			fileDetailsValues.add(fileName);
			fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
			path = new JTextField(5);
			path.setEditable(false);
			fileDetailsValues.add(path);
			fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
			date = new JLabel();
			fileDetailsValues.add(date);
			fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
			size = new JLabel();
			fileDetailsValues.add(size);

			int count = fileDetailsLabels.getComponentCount();
			for (int ii=0; ii<count; ii++) {
				fileDetailsLabels.getComponent(ii).setEnabled(false);
			}

			JToolBar toolBar = new JToolBar();
			// mnemonics stop working in a floated toolbar
			toolBar.setFloatable(false);

			JButton renameFile = new JButton("Rename");
			renameFile.setMnemonic('r');
			renameFile.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae) {
					renameFile();
				}
			});
			toolBar.add(renameFile);

			deleteFile = new JButton("Delete");
			deleteFile.setMnemonic('d');
			deleteFile.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae) {
					deleteFile();
				}
			});
			toolBar.add(deleteFile);

			JPanel fileView = new JPanel(new BorderLayout(3,3));

			fileView.add(toolBar,BorderLayout.NORTH);
			fileView.add(fileMainDetails,BorderLayout.CENTER);

			detailView.add(fileView, BorderLayout.SOUTH);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,treeScroll,detailView);
			splitPane.setDividerLocation(300);
			gui.add(splitPane, BorderLayout.CENTER);

			JPanel simpleOutput = new JPanel(new BorderLayout(3,3));

			gui.add(simpleOutput, BorderLayout.SOUTH);

		}
		return gui;
	}

	public void showRootFile() {
		// ensure the main files are displayed
		tree.setSelectionInterval(0,0);	
	}

	private TreePath findTreePath(File find) {
		for (int ii=0; ii<tree.getRowCount(); ii++) {
			TreePath treePath = tree.getPathForRow(ii);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
			File nodeFile = (File)node.getUserObject();

			if (nodeFile==find) {
				return treePath;
			}
		}
		// not found!
		return null;
	}

	private void renameFile() {
		if (currentFile==null) {
			showErrorMessage("No file selected to rename.","Select File");
			return;
		}

		String renameTo = JOptionPane.showInputDialog(gui, "New Name");
		if (renameTo!=null) {
			try {
				boolean directory = currentFile.isDirectory();
				TreePath parentPath = findTreePath(currentFile.getParentFile());
				DefaultMutableTreeNode parentNode =	(DefaultMutableTreeNode)parentPath.getLastPathComponent();

				boolean renamed = currentFile.renameTo(new File(currentFile.getParentFile(), renameTo));
				if (renamed) {
					if (directory) {
						// rename the node..

						// delete the current node..
						TreePath currentPath = findTreePath(currentFile);
						if(currentPath!=null) {	
							DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)currentPath.getLastPathComponent();
							treeModel.removeNodeFromParent(currentNode);
						}
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" +	currentFile + "' could not be renamed.";
					showErrorMessage(msg,"Rename Failed");
				}
			} catch(Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	private void deleteFile() {
		if (currentFile==null) {
			showErrorMessage("No file selected for deletion.","Select File");
			return;
		}

		int result = JOptionPane.showConfirmDialog(
				gui,
				"Are you sure you want to delete this file?",
				"Delete File",
				JOptionPane.ERROR_MESSAGE
				);
		if (result==JOptionPane.OK_OPTION) {
			try {
				TreePath parentPath = findTreePath(currentFile.getParentFile());
				DefaultMutableTreeNode parentNode =	(DefaultMutableTreeNode)parentPath.getLastPathComponent();

				boolean directory = currentFile.isDirectory();
				boolean deleted = currentFile.delete();
				if (deleted) {
					if (directory) {
						// delete the node..
						TreePath currentPath = findTreePath(currentFile);
						if(currentPath!=null) {
							DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)currentPath.getLastPathComponent();
							treeModel.removeNodeFromParent(currentNode);
						}               
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + currentFile + "' could not be deleted.";
					showErrorMessage(msg,"Delete Failed");
				}
			} catch(Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(gui,errorMessage,errorTitle,JOptionPane.ERROR_MESSAGE);
	}

	private void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(gui,t.toString(),t.getMessage(),JOptionPane.ERROR_MESSAGE);
		gui.repaint();
	}

	/** Update the table on the EDT */
	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileTableModel==null) {
					fileTableModel = new FileTableModel();
					table.setModel(fileTableModel);
				}
				table.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileTableModel.setFiles(files);
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
				if (!cellSizesSet) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					// size adjustment to better account for icons
					table.setRowHeight( icon.getIconHeight()+rowIconPadding );

					setColumnWidth(0,-1);
					setColumnWidth(3,60);
					table.getColumnModel().getColumn(3).setMaxWidth(120);
					setColumnWidth(4,-1);

					cellSizesSet = true;
				}
			}
		});
	}

	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		if (width<0) {
			// use the preferred width of the header..
			JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
			Dimension preferred = label.getPreferredSize();
			// altered 10->14 as per camickr comment.
			width = (int)preferred.getWidth()+14;
		}
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}

	/** Add the files that are contained within the directory of this node.
    Thanks to Hovercraft Full Of Eels. */
	private void showChildren(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); //!!
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
							}
						}
					}
					setTableData(files);
				}
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {
				tree.setEnabled(true);
			}
		};
		worker.execute();
	}

	/** Update the File details view with the details of this File. */
	private void setFileDetails(File file) {
		currentFile = file;
		Icon icon = fileSystemView.getSystemIcon(file);
		fileName.setIcon(icon);
		fileName.setText(fileSystemView.getSystemDisplayName(file));
		path.setText(file.getPath());
		date.setText(new Date(file.lastModified()).toString());
		size.setText(file.length() + " bytes");

		gui.repaint();
	}
}

/** A TableModel to hold File[]. */
class FileTableModel extends AbstractTableModel {

	private File[] files;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private String[] columns = {
			"Icon",
			"File",
			"Path/name",
			"Size",
			"Last Modified",
	};

	FileTableModel() {
		this(new File[0]);
	}

	FileTableModel(File[] files) {
		this.files = files;
	}

	public Object getValueAt(int row, int column) {
		File file = files[row];
		switch (column) {
		case 0:
			return fileSystemView.getSystemIcon(file);
		case 1:
			return fileSystemView.getSystemDisplayName(file);
		case 2:
			return file.getPath();
		case 3:
			return file.length();
		case 4:
			return file.lastModified();
		case 5:
			return file.canRead();
		case 6:
			return file.canWrite();
		case 7:
			return file.canExecute();
		case 8:
			return file.isDirectory();
		case 9:
			return file.isFile();
		default:
			System.err.println("Logic Error");
		}
		return "";
	}

	public int getColumnCount() {
		return columns.length;
	}

	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return ImageIcon.class;
		case 3:
			return Long.class;
		case 4:
			return Date.class;
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return Boolean.class;
		}
		return String.class;
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	public int getRowCount() {
		return files.length;
	}

	public File getFile(int row) {
		return files[row];
	}

	public void setFiles(File[] files) {
		this.files = files;
		fireTableDataChanged();
	}
}

/** A TreeCellRenderer for a File. */
class FileTreeCellRenderer extends DefaultTreeCellRenderer {

	private FileSystemView fileSystemView;

	private JLabel label;

	FileTreeCellRenderer() {
		label = new JLabel();
		label.setOpaque(true);
		fileSystemView = FileSystemView.getFileSystemView();
	}

	@Override
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		File file = (File)node.getUserObject();
		label.setIcon(fileSystemView.getSystemIcon(file));
		label.setText(fileSystemView.getSystemDisplayName(file));
		label.setToolTipText(file.getPath());

		if (selected) {
			label.setBackground(backgroundSelectionColor);
			label.setForeground(textSelectionColor);
		} else {
			label.setBackground(backgroundNonSelectionColor);
			label.setForeground(textNonSelectionColor);
		}

		return label;
	}
}
