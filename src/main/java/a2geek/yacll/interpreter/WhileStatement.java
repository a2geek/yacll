package a2geek.yacll.interpreter;

/**
 * Perform a while statement.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class WhileStatement implements Statement {
	private Statement expression;
	private CompositeStatement block;
	
	public WhileStatement(Statement expression, CompositeStatement block) {
		this.expression = expression;
		this.block = block;
	}

	public Object execute(State state) {
		while (InterpreterHelper.isTrue(expression.execute(state))) {
			block.execute(state);
		}
		return null;
	}
}
