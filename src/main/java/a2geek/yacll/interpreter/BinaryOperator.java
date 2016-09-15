package a2geek.yacll.interpreter;

/**
 * Perform binary operators available in our interpreter.
 * Currently assume all are Numeric responses!!
 * 
 * @author a2geek@users.noreply.github.com
 */
public class BinaryOperator implements Statement {
	private Operation operation;
	private Statement arg1;
	private Statement arg2;
	
	public BinaryOperator(Operation operation, Statement arg1, Statement arg2) {
		this.operation = operation;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	public Object execute(State state) {
		double val1 = ((Number)arg1.execute(state)).doubleValue();
		double val2 = ((Number)arg2.execute(state)).doubleValue();
		switch (operation) {
			case LT:	return val1 < val2;
			case GT:	return val1 > val2;
			case LE:	return val1 <= val2;
			case GE:	return val1 >= val2;
			case EQ:	return val1 == val2;
			case NE:	return val1 != val2;
			case ADD:	return val1 + val2;
			case SUB:	return val1 - val2;
			case MUL:	return val1 * val2;
			case DIV:	return val1 / val2;
			default:	throw new InternalError("Unknown binary operation: " + operation);
		}
	}
}
