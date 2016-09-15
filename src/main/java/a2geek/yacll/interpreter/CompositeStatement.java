package a2geek.yacll.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Serves as a container for multiple statements.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class CompositeStatement implements Statement {
	private List<Statement> statements = new ArrayList<Statement>();

	public Object execute(State state) {
		Object returnValue = null;
		for (Statement command : statements) {
			returnValue = command.execute(state);
		}
		return returnValue;
	}

	public void addStatement(Statement command) {
		statements.add(command);
	}
}
