package a2geek.yacll.interpreter;

/**
 * Handles variable references.  Basically pulls the value from State
 * and returns it.
 *  
 * @author a2geek@users.noreply.github.com
 */
public class VariableReference implements Statement {
	private String name;
	
	public VariableReference(String name) {
		this.name = name;
	}
	
	public Object execute(State state) {
		Object value = state.variables.get(name);
		return (value == null) ? 0.0 : value;
	}
}
