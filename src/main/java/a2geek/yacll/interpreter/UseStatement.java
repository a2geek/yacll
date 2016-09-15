package a2geek.yacll.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Include a referenced piece of program code.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class UseStatement implements Statement {
	private String filename;
	
	public UseStatement(String filename) {
		if (filename != null && !filename.endsWith(".yacll")) filename = filename + ".yacll";
		this.filename = filename;
	}

	/**
	 * Include referenced file.
	 * Try filesystem first followed by a resource (ie, system include from JAR file).
	 */
	public Object execute(State state) {
		InputStream inputStream = null;
		try {
			File file = new File(filename);
			if (file.exists()) {
				inputStream = new FileInputStream(file);
			} else {
				inputStream = getClass().getResourceAsStream("/" + filename);
			}
		} catch (Throwable t) {
			throw new Error("Unable to load \"" + filename + "\".", t);
		}
		Interpreter.includeFile(state, inputStream);
		return null;
	}
}
