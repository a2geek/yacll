package a2geek.yacll.interpreter;

/**
 * Contains constant values such as a number or a string.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class ConstantValue implements Statement {
	private Object constant;
	
	public ConstantValue(Double d) {
		this.constant = d;
	}
	public ConstantValue(String s) {
		this.constant = s;
	}
	
	public Object execute(State state) {
		return constant;
	}
}
