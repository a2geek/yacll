package a2geek.yacll.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serves as the master container for all functions and all statements.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class Program implements Statement {
	private Map<String,FunctionDefinition> functions = new HashMap<String,FunctionDefinition>();
	private List<UseStatement> useStatements = new ArrayList<UseStatement>();
	private List<Statement> statements = new ArrayList<Statement>();

	public Object execute(State state) {
		Object returnValue = null;
		for (UseStatement useStatement : useStatements) {
			useStatement.execute(state);
		}
		state.functions.putAll(this.functions);		// local functions should over-ride included ones
		for (Statement command : statements) {
			returnValue = command.execute(state);
		}
		return returnValue;
	}

	public void addFunction(FunctionDefinition function) {
		functions.put(function.functionName, function);
	}
	public void addUseStatement(UseStatement useStatement) {
		useStatements.add(useStatement);
	}
	public void addStatement(Statement command) {
		statements.add(command);
	}
}
