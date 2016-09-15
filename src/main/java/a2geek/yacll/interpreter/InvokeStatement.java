package a2geek.yacll.interpreter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Invoke a Java method.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class InvokeStatement implements Statement {
	private Statement method;
	private List<Statement> argExpressions;
	
	public InvokeStatement(Statement method, List<Statement> argExpressions) {
		this.method = method;
		this.argExpressions = argExpressions;
	}

	/**
	 * Execute the invoke statement.
	 * This method gathers up the argument values and then runs invoke.
	 */
	public Object execute(State state) {
		List<Object> argValues = new ArrayList<Object>();
		String methodPath = (String)method.execute(state);
		if (argExpressions != null) {
			for (Statement statement : argExpressions) argValues.add(statement.execute(state));
		}
		try {
			return invoke(state, methodPath, argValues);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	
	/**
	 * Invoke a method dynamically.
	 * If the method starts with a capital (ie, Math), it is assumed to be a
	 * class-level method; otherwise, it is assumed to be a method or variable on
	 * the current object (beginning with the this state object).
	 * <p>
	 * Structure for the method description is method1.method2(type...) where method1
	 * and method2 are method (or variable) names.  If there are arguments, the type 
	 * should be included in the parenthesis and i=Integer, d=Double, f=Float,
	 * c=Character, s=String, l=Long. 
	 */
	protected Object invoke(State state, String methodPath, List<Object> args) throws Exception {
		Pattern pattern = Pattern.compile("\\.");
		String[] tokens = pattern.split(methodPath);
		if (tokens != null && tokens.length > 0) {
			Object currentObject = evaluateInitialState(state, tokens[0]);
			for (int i=1; currentObject != null && i<tokens.length; i++) {
				String method = tokens[i];
				currentObject = evaluateMethod(currentObject, method, args);
			}
			return currentObject;
		}
		throw new IllegalArgumentException("Unable to match methodPath of '" + methodPath + "'.");
	}
	/**
	 * Evaluate the current method description with the currentObject and arguments (if any needed).
	 * This method maps all argument type descriptions to a Class array, locates the appropriate
	 * Method object, and then invokes that method.  Note that the only Object type supported at this
	 * time is String (everything else is a primitive).
	 */
	protected Object evaluateMethod(Object currentObject, String methodDesc, List<Object> args) throws Exception {
		Pattern pattern = Pattern.compile("[(,)]");
		String[] tokens = pattern.split(methodDesc);
		String methodName = tokens[0];
		List<Class> argTypes = new ArrayList<Class>();
		for (int i=1; i<tokens.length; i++) {
			switch (tokens[i].charAt(0)) {
			case 'c':	argTypes.add(char.class);		break;
			case 'S':	argTypes.add(String.class);		break;
			case 's':	argTypes.add(short.class);		break;
			case 'i':	argTypes.add(int.class);		break;
			case 'l':	argTypes.add(long.class);		break;
			case 'f':	argTypes.add(float.class);		break;
			case 'd':	argTypes.add(double.class);		break;
			default:	argTypes.add(Class.forName(tokens[i].replace('/','.')));	break;
			}
		}
		assert argTypes.size() == 0 || argTypes.size() != args.size() : "Argument list size does not match!";
		Class[] argTypesArray = new Class[argTypes.size()];
		argTypes.toArray(argTypesArray);
		Class clazz = (currentObject instanceof Class) ? (Class)currentObject : currentObject.getClass(); 
		Method method = clazz.getMethod(methodName, argTypesArray);
		Object[] argValues = new Object[argTypes.size()];
		if (argValues.length > 0) {
			// Only copy if we need 'em
			for (int i=0; i<argValues.length; i++) {
				Class type = argTypesArray[i];
				Object value = args.get(i);
				// Convert datatypes...
				if (type == short.class && value instanceof Double) {
					value = ((Double)value).shortValue();
				} else if (type == int.class && value instanceof Double) {
					value = ((Double)value).intValue();
				} else if (type == long.class && value instanceof Double) {
					value = ((Double)value).longValue();
				} else if (type == float.class && value instanceof Double) {
					value = ((Double)value).floatValue();
				}
				argValues[i] = value;
			}
		}
		return method.invoke(currentObject, argValues);
	}
	/**
	 * Evaluate the initial state description.
	 * If the string begins with a capital letter, assume it is a Class name;
	 * otherwise try to get an attribute from the current State object.
	 */
	protected Object evaluateInitialState(State state, String desc) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException, InstantiationException {
		if (desc == null || desc.length() == 0) throw new IllegalArgumentException("Invalid initial state: " + desc);
		if (Character.isUpperCase(desc.charAt(0))) {
			try {
				return Class.forName("java.lang." + desc);
			} catch (ClassNotFoundException ex1) {
				try {
					return Class.forName("java.awt." + desc);
				} catch (ClassNotFoundException ex2) {
					throw ex1;	// throw the first one...
				}
			}
		} else {
			return State.class.getField(desc).get(state);
		}
	}
}
