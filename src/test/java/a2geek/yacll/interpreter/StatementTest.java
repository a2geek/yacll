package a2geek.yacll.interpreter;

import java.util.ArrayList;
import java.util.List;

import a2geek.yacll.interpreter.AssignmentStatement;
import a2geek.yacll.interpreter.BinaryOperator;
import a2geek.yacll.interpreter.CompositeStatement;
import a2geek.yacll.interpreter.ConstantValue;
import a2geek.yacll.interpreter.ForStatement;
import a2geek.yacll.interpreter.FunctionDefinition;
import a2geek.yacll.interpreter.FunctionInvocationStatement;
import a2geek.yacll.interpreter.InvokeStatement;
import a2geek.yacll.interpreter.Operation;
import a2geek.yacll.interpreter.Program;
import a2geek.yacll.interpreter.RepeatStatement;
import a2geek.yacll.interpreter.ReturnStatement;
import a2geek.yacll.interpreter.State;
import a2geek.yacll.interpreter.Statement;
import a2geek.yacll.interpreter.UnaryOperator;
import a2geek.yacll.interpreter.VariableReference;
import a2geek.yacll.interpreter.WhileStatement;
import junit.framework.TestCase;

public class StatementTest extends TestCase {
	private static final Double PI = 3.14159;
	private State state;
	
	protected void setUp() {
		this.state = new State();
	}
	
	public void testAtomic() {
		state.variables.put("a", PI);
		assertEquals(PI, new VariableReference("a").execute(state));	// variable reference
		assertEquals(PI, new ConstantValue(PI).execute(state));			// numeric constant
		assertEquals(0.0, new VariableReference("b").execute(state));	// undeclared variable = 0!
	}
	public void testAssignment() {
		new AssignmentStatement("a", new ConstantValue(PI)).execute(state);
		assertEquals(PI, state.variables.get("a"));
	}
	public void testComposite() {
		CompositeStatement statement = new CompositeStatement();
		statement.addStatement(new AssignmentStatement("a", new ConstantValue(PI)));
		statement.addStatement(new AssignmentStatement("b", new ConstantValue(2*PI)));
		statement.execute(state);
		assertEquals(PI, state.variables.get("a"));
		assertEquals(2*PI, state.variables.get("b"));
	}
	public void testOperations() {
		ConstantValue one = new ConstantValue(1.0);
		ConstantValue two = new ConstantValue(2.0);
		// Binary Operations
		assertEquals(2.0, new BinaryOperator(Operation.ADD, one, one).execute(state));
		assertEquals(2.0, new BinaryOperator(Operation.DIV, two, one).execute(state));
		assertFalse((Boolean)new BinaryOperator(Operation.EQ, one, two).execute(state));
		assertFalse((Boolean)new BinaryOperator(Operation.GE, one, two).execute(state));
		assertFalse((Boolean)new BinaryOperator(Operation.GT, one, two).execute(state));
		assertTrue((Boolean)new BinaryOperator(Operation.LE, one, two).execute(state));
		assertTrue((Boolean)new BinaryOperator(Operation.LT, one, two).execute(state));
		assertEquals(2.0, new BinaryOperator(Operation.MUL, one, two).execute(state));
		assertTrue((Boolean)new BinaryOperator(Operation.NE, one, two).execute(state));
		assertEquals(-1.0, new BinaryOperator(Operation.SUB, one, two).execute(state));
		// Unary Operations
		assertEquals(-1.0, new UnaryOperator(Operation.MINUS, one).execute(state));
	}
	public void testWhile() {
		// a < 5.0
		BinaryOperator expression = new BinaryOperator(Operation.LT, 
				new VariableReference("a"), new ConstantValue(5.0));
		// { a = a + 1; b = b + PI; }
		CompositeStatement block = new CompositeStatement();
		block.addStatement(new AssignmentStatement("a",
				new BinaryOperator(Operation.ADD, new VariableReference("a"), new ConstantValue(1.0))));
		block.addStatement(new AssignmentStatement("b",
				new BinaryOperator(Operation.ADD, new VariableReference("b"), new ConstantValue(PI))));
		// perform test
		WhileStatement w = new WhileStatement(expression, block);
		w.execute(state);
		assertEquals(5.0, state.variables.get("a"));
		assertEquals(5*PI, state.variables.get("b"));
	}
	/**
	 * This method tests the min(a,b) and max(a,b) for function definitions.
	 * They are a bit confusing - a FunctionDefinition consists of the function name,
	 * the list of argument names (ie, Strings), and the CompositeStatement (aka block).
	 * The CompositeStatement for each consists of the appropriate InvokeStatement to
	 * "call out" to the Java environment.  The InvokeStatement itself consists of a 
	 * String expression (a constant in our case) and a list of argument Statements which
	 * are variable references. 
	 */
	public void testFunctionDefinitionStatement() {
		// Setup min function
		List<Statement> argExpressions = new ArrayList<Statement>();
		argExpressions.add(new VariableReference("a"));
		argExpressions.add(new VariableReference("b"));
		CompositeStatement minStatements = new CompositeStatement();
		minStatements.addStatement(new InvokeStatement(new ConstantValue("Math.min(d,d)"), argExpressions));
		List<String> argNames = new ArrayList<String>();
		argNames.add("a");
		argNames.add("b");
		FunctionDefinition minFunc = new FunctionDefinition("min", argNames, minStatements);
		// Setup max function
		CompositeStatement maxStatements = new CompositeStatement();
		maxStatements.addStatement(new InvokeStatement(new ConstantValue("Math.max(d,d)"), argExpressions));
		FunctionDefinition maxFunc = new FunctionDefinition("max", argNames, maxStatements);
		// Setup variable state
		state.variables.put("a", 1.0);
		state.variables.put("b", 2.0);
		// Test!
		assertEquals(1.0, minFunc.execute(state));
		assertEquals(2.0, maxFunc.execute(state));
	}
	/**
	 * This method tests function invocation using the cosine function.
	 * It consists of a function definition (see {@see #testFunctionDefinitionStatement()})
	 * for details on the function definition as well as the invocation of that
	 * function.
	 */
	public void testFunctionInvocation() {
		// Build function definition
		List<Statement> argExpressions = new ArrayList<Statement>();
		argExpressions.add(new VariableReference("a"));
		CompositeStatement cosStatements = new CompositeStatement();
		cosStatements.addStatement(new InvokeStatement(new ConstantValue("Math.cos(d)"), argExpressions));
		List<String> argNames = new ArrayList<String>();
		argNames.add("a");
		FunctionDefinition cosFunc = new FunctionDefinition("cos", argNames, cosStatements);
		// Define function within the program
		Program program = new Program();
		program.addFunction(cosFunc);
		// Create the program
		program.addStatement(new AssignmentStatement("answer", new FunctionInvocationStatement("cos", argExpressions)));
		// Setup the value for "a"
		state.variables.put("a", 0.0);
		// Run and test result
		program.execute(state);
		assertEquals(1.0, state.variables.get("answer"));
	}
	public void testCascadingOperations() {
		AssignmentStatement assignment = new AssignmentStatement("a",
				new BinaryOperator(Operation.ADD, new ConstantValue(1.0),
				new BinaryOperator(Operation.ADD, new ConstantValue(1.0), new ConstantValue(1.0))));
		assignment.execute(state);
		assertEquals(3.0, state.variables.get("a"));
	}
	public void testFor() {
		// for (a = 0; a<5; a=a+1) { b=b+1; }
		CompositeStatement block = new CompositeStatement();
		block.addStatement(new AssignmentStatement("b",
				new BinaryOperator(Operation.ADD, new VariableReference("b"), new ConstantValue(1.0))));
		ForStatement f = new ForStatement(
				new AssignmentStatement("a", new ConstantValue(1.0)),
				new BinaryOperator(Operation.LT, new VariableReference("a"), new ConstantValue(5.0)),
				block,
				new AssignmentStatement("a", new BinaryOperator(Operation.ADD, new VariableReference("a"), new ConstantValue(1.0))));
		// perform test
		f.execute(state);
		assertEquals(5.0, state.variables.get("a"));
		assertEquals(4.0, state.variables.get("b"));
	}
	public void testRepeat() {
		// repeat { a=a+1; } until (a > 5);
		CompositeStatement block = new CompositeStatement();
		block.addStatement(new AssignmentStatement("a",
				new BinaryOperator(Operation.ADD, new VariableReference("a"), new ConstantValue(1.0))));
		BinaryOperator expression = new BinaryOperator(Operation.GT, 
				new VariableReference("a"), new ConstantValue(5.0));
		RepeatStatement r = new RepeatStatement(block, expression);
		// perform test
		r.execute(state);
		assertEquals(6.0, state.variables.get("a"));
	}
	public void testReturn() {
		// return 5;
		ReturnStatement returnStatement = new ReturnStatement(new ConstantValue(5.0));
		// perform test
		assertEquals(5.0, returnStatement.execute(state));
	}
}
