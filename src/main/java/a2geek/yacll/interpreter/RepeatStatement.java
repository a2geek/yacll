package a2geek.yacll.interpreter;

/**
 * Perform a repeat statement.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class RepeatStatement implements Statement {
	private CompositeStatement block;
	private Statement expression;
	
	public RepeatStatement(CompositeStatement block, Statement expression) {
		this.expression = expression;
		this.block = block;
	}

	public Object execute(State state) {
		do {
			block.execute(state);
		} while (InterpreterHelper.isFalse(expression.execute(state)));
		return null;
	}
}
