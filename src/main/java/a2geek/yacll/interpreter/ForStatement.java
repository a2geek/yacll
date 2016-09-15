package a2geek.yacll.interpreter;

/**
 * Perform a for statement.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class ForStatement implements Statement {
	private Statement initialize;
	private Statement expression;
	private CompositeStatement block;
	private Statement increment;
	
	public ForStatement(Statement initialize, Statement expression, CompositeStatement block, Statement increment) {
		this.initialize = initialize;
		this.expression = expression;
		this.block = block;
		this.increment = increment;
	}

	public Object execute(State state) {
		initialize.execute(state);
		while (InterpreterHelper.isTrue(expression.execute(state))) {
			block.execute(state);
			increment.execute(state);
		}
		return null;
	}
}
