package a2geek.yacll.antlr;

import java.io.ByteArrayInputStream;
import java.util.List;

import a2geek.yacll.interpreter.Program;
import a2geek.yacll.interpreter.State;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import junit.framework.TestCase;

public class GeneratorTest extends TestCase {
	private void testList(String assertionMessage, String messagePrefix, List<String> messages) {
		if (!messages.isEmpty()) {
			for (String message : messages) System.out.println(messagePrefix + ": " + message);
			fail(assertionMessage);
		}
	}

	private void testProgram(State answer, String code) throws RecognitionException, TokenStreamException {
		// Transform to interpreted statements
		ByteArrayInputStream inputStream = new ByteArrayInputStream(code.getBytes());
		YacllLexer lexer = new YacllLexer(inputStream);
		YacllParser parser = new YacllParser(lexer);
		parser.program();
		testList("Lexer should have no errors", "Lexer errors", lexer.errors);
		testList("Lexer should have no warnings", "Lexer warnings", lexer.warnings);
		testList("Parser should have no errors", "Parser errors", parser.errors);
		testList("Parser should have no warnings", "Parser warnings", parser.warnings);
		YacllGenerator generator = new YacllGenerator();
		Program program = generator.program(parser.getAST());
		testList("Generator should have no errors", "Generator errors", generator.errors);
		testList("Generator should have no warnings", "Generator warnings", generator.warnings);
		// Run statements
		State state = new State();
		program.execute(state);
		// Verify results
		assertEquals("Results must match", answer.variables, state.variables);
	}
	private State testIncludedProgram(String code) throws RecognitionException, TokenStreamException {
		// Transform to interpreted statements
		ByteArrayInputStream inputStream = new ByteArrayInputStream(code.getBytes());
		YacllLexer lexer = new YacllLexer(inputStream);
		YacllParser parser = new YacllParser(lexer);
		parser.includedProgram();
		testList("Lexer should have no errors", "Lexer errors", lexer.errors);
		testList("Lexer should have no warnings", "Lexer warnings", lexer.warnings);
		testList("Parser should have no errors", "Parser errors", parser.errors);
		testList("Parser should have no warnings", "Parser warnings", parser.warnings);
		YacllGenerator generator = new YacllGenerator();
		Program program = generator.program(parser.getAST());
		testList("Generator should have no errors", "Generator errors", generator.errors);
		testList("Generator should have no warnings", "Generator warnings", generator.warnings);
		// Run statements
		State state = new State();
		program.execute(state);	// oddly enough, this loads the function(s)
		return state;
	}
	public void testAssignmentStatement1() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Double(0));
		testProgram(answer, "a = 0;");
	}
	public void testAssignmentStatement2() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Double(0));
		answer.variables.put("b", new Double(1));
		testProgram(answer, "a = 0; b=1;");
	}
	public void testMathematicalExpressionStatements() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Double(1));
		testProgram(answer, "a = 0 + 1;");
		testProgram(answer, "a = 2 - 1;");
		testProgram(answer, "a = 2 / 2;");
		testProgram(answer, "a = 2 * 0.5;");
	}
	public void testBooleanExpressionStatements() throws Exception {
		State answer = new State();
		answer.variables.put("a", Boolean.TRUE);
		testProgram(answer, "a = 1 < 2;");
		testProgram(answer, "a = 2 > 1;");
		testProgram(answer, "a = 1 <= 1;");
		testProgram(answer, "a = 1 >= 1;");
		testProgram(answer, "a = 1 != 2;");
		testProgram(answer, "a = 1 = 1;");
	}
	public void testWhileStatement() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Double(0));
		answer.variables.put("b", new Double(10));
		testProgram(answer, "a=5; while (a>0) { a=a-1; b=b+2;}");
	}
	public void testForStatement() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Double(5.0));
		answer.variables.put("b", new Double(10.0));
		testProgram(answer, "for (a=0; a<5; a=a+1) { b=b+2;}");
	}
	public void testRepeatStatement() throws Exception {
		State answer = new State();
		answer.variables.put("b", new Double(6));
		testProgram(answer, "repeat b=b+1; until (b > 5);");
	}
	public void testStrings() throws Exception {
		State answer = new State();
		answer.variables.put("a", "hello");
		testProgram(answer, "a = \"hello\";");
	}
	public void testFunctions() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Double(1));
		testProgram(answer, "function min(a,b) { invoke(\"Math.min(d,d)\", a,b); } a = min(1,5);");
		testProgram(answer, "function max(a,b) { invoke(\"Math.max(d,d)\", a,b); } a = max(-1,1);");
		testProgram(answer, "function cos(a) { invoke(\"Math.cos(d)\", a); } a = cos(0);");
		answer.variables.put("a", new Double(0));
		testProgram(answer, "function sin(a) { invoke(\"Math.sin(d)\", a); } a = sin(0);");
	}
	public void testWrongTypeArgumentsToFunction() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Long(1));
		testProgram(answer, "function min(a,b) { invoke(\"Math.min(l,l)\", a,b); } a = min(1,5);");
	}
	public void testFunctionsNoArguments() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Integer(State.HEIGHT));
		testProgram(answer, "function height() { return invoke(\"image.getHeight()\"); } a = height();");
		answer.variables.put("a", new Integer(State.WIDTH));
		testProgram(answer, "function width() { return invoke(\"image.getWidth()\"); } a = width();");
	}
	public void testFunctionReturnStatement() throws Exception {
		State answer = new State();
		answer.variables.put("a", new Double(5));
		testProgram(answer, "function five() { return 5; } a = five();");
	}
	public void testProblem1() throws Exception {
		State answer = new State();
		answer.variables.put("a", 9.0);
		testProgram(answer, "a = 5 - 3 + 7;");
	}
	public void testIncludedProgram() throws Exception {
		State result = testIncludedProgram("function fn() { return 5; }");
		assertEquals(1, result.functions.size());
		assertEquals("fn", result.functions.keySet().iterator().next());
	}
	public void testUseStatement() throws Exception {
		State answer = new State();
		answer.variables.put("a",3.0);
		answer.variables.put("b", 5.0);
		testProgram(answer, "use \"math\"; a=min(5,3); b=max(5,3);");
	}
	public void test2UseStatements() throws Exception {
		State answer = new State();
		answer.variables.put("a", 3.0);
		answer.variables.put("b", 5.0);
		testProgram(answer, "use \"math\"; use \"test\"; a=min(5,3); b=givemefive();");
	}
}
