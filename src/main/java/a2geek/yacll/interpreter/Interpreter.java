package a2geek.yacll.interpreter;

import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import antlr.debug.misc.ASTFrame;

import a2geek.yacll.antlr.YacllGenerator;
import a2geek.yacll.antlr.YacllLexer;
import a2geek.yacll.antlr.YacllParser;

public class Interpreter {
	private static int launchCounter = 1;
	
	/**
	 * Run the interpreter given the Statement to execute.
	 * This method manages the windows and final completion message.
	 * @return JFrame of the window launched
	 */
	@SuppressWarnings("serial")
	private static JFrame run(JFrame parent, Program program, String title, boolean closeOnExit) {
		final State state = new State();
		JFrame window = new JFrame(title);
				window.setContentPane(new JPanel() {
					public void paint(Graphics g) {
						g.drawImage(state.image, 0, 0, null);
					}
				}
			);
		window.setResizable(false);
		if (closeOnExit) window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setSize(State.WIDTH + window.getInsets().left + window.getInsets().right,
					   State.HEIGHT + window.getInsets().top + window.getInsets().bottom);
		window.setVisible(true);
		state.window = window;
		// This should refresh the screen every 1/2 second...
		Runnable runnable = new Runnable() {
			public void run() {
				state.window.repaint();
				try {
					Thread.sleep(500);
				} catch (InterruptedException ignored) {}
			}
		};
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.start();
		// Run the program
		program.execute(state);
		// All done!
		StringBuffer message = new StringBuffer();
		message.append("Variables:\n");
		for (String variable : state.variables.keySet()) {
			message.append(variable);
			message.append(" = ");
			message.append(state.variables.get(variable));
			message.append("\n");
		}
		JOptionPane.showMessageDialog(parent,
				message.toString(), "FINISHED! " + title,
				JOptionPane.INFORMATION_MESSAGE);
		return window;
	}

	/**
	 * Internal method to load a program from the given InputStream.
	 * Any errors are reported in dialog messages.
	 */
	private static JFrame run(JFrame parent, InputStream inputStream, String title, boolean closeOnExit) {
		try {
			YacllLexer lexer = new YacllLexer(inputStream);
			YacllParser parser = new YacllParser(lexer);
			parser.program();
			List<String> errors = new ArrayList<String>();
			errors.addAll(lexer.errors);
			errors.addAll(parser.errors);
			if (errors.size() > 0) {
				StringBuffer message = new StringBuffer();
				for (String error : errors) {
					message.append(error);
					message.append("\n");
				}
				JOptionPane.showMessageDialog(parent, message.toString(),
						"Compile error", JOptionPane.ERROR_MESSAGE);
			} else {
				YacllGenerator generator = new YacllGenerator();
				Program program = generator.program(parser.getAST());
				return Interpreter.run(parent, program, title, closeOnExit);
			}
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(parent, t.getLocalizedMessage(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}
		return null;	// no window to track
	}

	/**
	 * Internal method to include program code from the given InputStream.
	 * Modifies the internal state to include to given functions.
	 * Any errors are reported in dialog messages.
	 * @see UseStatement
	 */
	static void includeFile(State state, InputStream inputStream) {
		try {
			YacllLexer lexer = new YacllLexer(inputStream);
			YacllParser parser = new YacllParser(lexer);
			parser.includedProgram();
			List<String> errors = new ArrayList<String>();
			errors.addAll(lexer.errors);
			errors.addAll(parser.errors);
			if (errors.size() > 0) {
				StringBuffer message = new StringBuffer();
				for (String error : errors) {
					message.append(error);
					message.append("\n");
				}
				JOptionPane.showMessageDialog(state.window, message.toString(),
						"Compile error", JOptionPane.ERROR_MESSAGE);
			} else {
				YacllGenerator generator = new YacllGenerator();
				Program program = generator.program(parser.getAST());
				program.execute(state);		// NOTE: Real output is modified state!!
			}
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(state.window, t.getLocalizedMessage(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}
	}

	/**
	 * Launch the interpreter given the string code.  This is used from the IDE.
	 */
	public static JFrame run(JFrame parent, String code) {
		return run(parent, new ByteArrayInputStream(code.getBytes()), "Launch #" + launchCounter++, false);
	}

	/**
	 * Launch the interpreter given the File.  This is used from the command line.
	 */
	public static JFrame run(JFrame parent, File file) {
		try {
			return run(parent, new FileInputStream(file), file.getName(), true);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(parent, e.getLocalizedMessage(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;	// no window to track
		}
	}

	/**
	 * Display the AST tree for the given input stream.
	 */
	private static void showTree(JFrame parent, String title, InputStream inputStream) {
		try {
			YacllLexer lexer = new YacllLexer(inputStream);
			YacllParser parser = new YacllParser(lexer);
			parser.program();
			ASTFrame window = new ASTFrame(title, parser.getAST());
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.setVisible(true);
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(parent, t.getLocalizedMessage(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}
	}

	/**
	 * Display the AST tree for the given source file.
	 */
	public static void showTree(JFrame parent, File file) {
		try {
			showTree(parent, file.getName(), new FileInputStream(file));
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(parent, t.getLocalizedMessage(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			t.printStackTrace();
		}
	}

	/**
	 * Display the AST tree for the given source file.
	 */
	public static void showTree(JFrame parent, String title, String code) {
		showTree(parent, title, new ByteArrayInputStream(code.getBytes()));
	}
}
