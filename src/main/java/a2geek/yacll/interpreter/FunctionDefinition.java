package a2geek.yacll.interpreter;

import java.util.List;

/**
 * Holds the definition for the current function.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class FunctionDefinition implements Statement {
	public String functionName;
	public List<String> argNames; 
	private CompositeStatement block;
	
	public FunctionDefinition(String functionName, List<String> argNames, CompositeStatement block) {
		this.functionName = functionName;
		this.argNames = argNames;
		this.block = block;
	}

	public Object execute(State state) {
		return block.execute(state);
	}
}
