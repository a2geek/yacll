package a2geek.yacll.interpreter;

/**
 * Basic interpreter command interface.
 * 
 * @author a2geek@users.noreply.github.com
 */
public interface Statement {
	/**
	 * Perform this Statement with the current interpreter State.
	 * @return any value this command produces
	 */
	public Object execute(State state);
}
