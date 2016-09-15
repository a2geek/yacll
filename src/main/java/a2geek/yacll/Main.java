package a2geek.yacll;

import java.io.File;

import javax.swing.JOptionPane;

import a2geek.yacll.interpreter.Interpreter;

/**
 * Primary launch vehicle for YACLL.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class Main {
	public static final String implementationTitle;
	public static final String implementationVersion;
	public static final String applicationTitle;
	public static final String aboutMessage;
	private static final String HELP_MESSAGE = "YACLL Help:\n\n"
		+ "-h, -?   This help message\n"
		+ "-x pgm   Execute pgm\n"
		+ "-t pgm   View tree structure of pgm\n\n"
		+ "Double-clicking on the JAR will launch the 'IDE'.";

	static {
		Package pkg = Main.class.getPackage();
		String implTitle = pkg.getImplementationTitle();
		if (implTitle == null) {
			implTitle = "YACLL";
		}
		implementationTitle = implTitle;
		String implVersion = pkg.getImplementationVersion();
		if (implVersion == null) {
			implVersion = "PROTOTYPE";
		}
		implementationVersion = implVersion;
		applicationTitle = String.format("%s IDE :: Version %s", implementationTitle, implementationVersion);
		aboutMessage = String.format("YACLL\n"
				+ "Version %s\n"
				+ "Copyright (c) 2006\n\n"
				+ "Please see https://github.com/a2geek/yacll", implementationVersion);
	}
	
	/**
	 * Handle command-line arguments.
	 */
	public static void main(String[] args) {
		// Ensure we're running under Java 1.5
		double version = Double.parseDouble(System.getProperty("java.specification.version"));
		if (version < 1.5) {
			JOptionPane.showMessageDialog(null,
					"I'm sorry, but YACLL requires Java 5.0 or later!", "Unable to launch!",
					JOptionPane.ERROR_MESSAGE);
		}
		// Continue on...
		int i = 0;
		while (i < args.length) {
			String arg = args[i];
			if ("-?".equals(arg) || "-h".equals(arg)) {
				System.out.println(HELP_MESSAGE);
				System.exit(0);
			} else if ("-x".equals(arg)) {
				i++;
				Interpreter.run(null, new File(args[i]));
			} else if ("-t".equals(arg)) {
				i++;
				Interpreter.showTree(null, new File(args[i]));
			}
		}
		if (args.length == 0) {
			Editor.main(null);
		}
	}
}
