package a2geek.yacll.interpreter;

import java.util.List;

/**
 * Invoke the given function statement.
 * <p>
 * Note that at this time the State of the function has
 * no awareness of the greater program State.  Only the
 * expressions evaluated in the function call sees the
 * program-level State variables - all else is currently
 * hidden.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class FunctionInvocationStatement implements Statement {
	private String functionName;
	private List<Statement> args; 
	
	public FunctionInvocationStatement(String functionName, List<Statement> args) {
		this.functionName = functionName;
		this.args = args;
	}

	public Object execute(State state) {
		FunctionDefinition function = state.functions.get(functionName);
		if (function == null) throw new Error("Unknown function '" + functionName + "'.");
		State functionState = new State(state);
		if (function.argNames != null) {
			for (int i=0; i<function.argNames.size(); i++) {
				String argName = function.argNames.get(i);
				Object object = args.get(i).execute(state);
				functionState.variables.put(argName, object);
			}
		}
		return function.execute(functionState);
	}
}
