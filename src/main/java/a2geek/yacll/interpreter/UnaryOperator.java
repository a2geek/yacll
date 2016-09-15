package a2geek.yacll.interpreter;

/**
 * Perform unary operations available in our interpreter.
 * Currently assume all are Double responses!!
 * 
 * @author a2geek@users.noreply.github.com
 */
public class UnaryOperator implements Statement {
	private Operation operation;
	private Statement arg;
	
	public UnaryOperator(Operation operation, Statement arg) {
		this.operation = operation;
		this.arg = arg;
	}
	
	public Object execute(State state) {
		Double val = (Double)arg.execute(state);
		switch (operation) {
			case MINUS:	return -val;
			default:	throw new InternalError("Unknown unary operation: " + operation);
		}
	}
}
