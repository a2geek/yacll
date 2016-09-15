package a2geek.yacll.interpreter;

/**
 * Perform a return.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class ReturnStatement implements Statement {
	private Statement expression;
	
	public ReturnStatement(Statement expression) {
		this.expression = expression;
	}

	public Object execute(State state) {
		return expression.execute(state);
	}
}
