package a2geek.yacll.interpreter;

/**
 * Contains some basic helper methods.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class InterpreterHelper {
	public static boolean isTrue(Object object) {
		if (object instanceof Boolean) {
			return ((Boolean)object).booleanValue();
		} else if (object instanceof Number) {
			return ((Number)object).doubleValue() != 0;
		} else {
			return object != null;
		}
	}
	public static boolean isFalse(Object object) {
		return !isTrue(object);
	}
}
