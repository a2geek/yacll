package a2geek.yacll;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import a2geek.yacll.interpreter.Interpreter;

public class Editor extends JFrame {
	private JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;

	private JMenu fileMenu = null;

	private JMenu helpMenu = null;

	private JMenuItem exitMenuItem = null;

	private JMenuItem aboutMenuItem = null;

	private JMenuItem runMenuItem = null;

	private JTextArea codeTextArea = null;

	private JScrollPane jScrollPane = null;

	private JMenuItem treeMenuItem = null;

	private JMenu editMenu = null;

	private JMenu programMenu = null;

	private JMenuItem copyMenuItem = null;

	private JMenuItem cutMenuItem = null;

	private JMenuItem pasteMenuItem = null;

	/**
	 * This is the default constructor
	 */
	public Editor() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJJMenuBar());
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle(Main.applicationTitle);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getEditMenu());
			jJMenuBar.add(getProgramMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_X);
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_A);
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Editor.this,
							Main.aboutMessage, 
							"About YACLL",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getRunMenuItem() {
		if (runMenuItem == null) {
			runMenuItem = new JMenuItem();
			runMenuItem.setText("Run");
			runMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_R);
			runMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
					Event.CTRL_MASK, true));
			runMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String code = codeTextArea.getText();
					Interpreter.run(Editor.this, code);
				}
			});
		}
		return runMenuItem;
	}

	/**
	 * This method initializes codeTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getCodeTextArea() {
		if (codeTextArea == null) {
			codeTextArea = new JTextArea();
			Font font = new Font("Courier", Font.PLAIN, 12);
			codeTextArea.setFont(font);
			codeTextArea.setTabSize(4);
		}
		return codeTextArea;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getCodeTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes treeMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getTreeMenuItem() {
		if (treeMenuItem == null) {
			treeMenuItem = new JMenuItem();
			treeMenuItem.setText("AST Tree");
			treeMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_T);
			treeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
					Event.CTRL_MASK, true));
			treeMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Interpreter.showTree(Editor.this, "Tree View", codeTextArea.getText());
				}
			});
		}
		return treeMenuItem;
	}

	/**
	 * This method initializes editMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
	}

	/**
	 * This method initializes programMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getProgramMenu() {
		if (programMenu == null) {
			programMenu = new JMenu();
			programMenu.setText("Program");
			programMenu.setMnemonic(java.awt.event.KeyEvent.VK_P);
			programMenu.add(getRunMenuItem());
			programMenu.add(getTreeMenuItem());
		}
		return programMenu;
	}

	/**
	 * This method initializes copyMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));

			copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					codeTextArea.copy();
				}
			});
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes cutMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));

			cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					codeTextArea.cut();
				}
			});
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes pasteMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));

			pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					codeTextArea.paste();
				}
			});
		}
		return pasteMenuItem;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		Editor application = new Editor();
		application.show();
	}

}
