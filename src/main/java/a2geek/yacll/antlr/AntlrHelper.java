package a2geek.yacll.antlr;

import antlr.RecognitionException;

/**
 * A minor collection of helper methods for ANTLR productions.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class AntlrHelper {
	public static String format(RecognitionException ex) {
		StringBuffer message = new StringBuffer();
		if (ex.getFilename() != null) {
			message.append("In ");
			message.append(ex.getFilename());
			message.append(" ");
		}
		message.append("Line ");
		message.append(ex.getLine());
		message.append(", column ");
		message.append(ex.getColumn());
		message.append(": ");
		message.append(ex.getMessage());
		return message.toString();
	}
}
