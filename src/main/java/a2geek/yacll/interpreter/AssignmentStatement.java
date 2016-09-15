package a2geek.yacll.interpreter;

/**
 * Perform an assignment.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class AssignmentStatement implements Statement {
	private String variable;
	private Statement expression;
	
	public AssignmentStatement(String variable, Statement expression) {
		this.variable = variable;
		this.expression = expression;
	}

	public Object execute(State state) {
		state.variables.put(variable, expression.execute(state));
		return state.variables.get(variable);
	}
}
