package a2geek.yacll.antlr;

import java.io.ByteArrayInputStream;
import java.util.List;

import a2geek.yacll.antlrtesting.AstHelper;
import antlr.debug.misc.ASTFrame;
import junit.framework.TestCase;

public class ParserTest extends TestCase {
	private static boolean visible = false;
	
	private void testList(String assertionMessage, String messagePrefix, List<String> messages) {
		if (!messages.isEmpty()) {
			for (String message : messages) System.out.println(messagePrefix + ": " + message);
			fail(assertionMessage);
		}
	}
	
	private void testProgramCode(String code, String answer) throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(code.getBytes());
		YacllLexer lexer = new YacllLexer(inputStream);
		YacllParser parser = new YacllParser(lexer);
		parser.program();
		testList("Lexer should have no errors", "Lexer errors", lexer.errors);
		testList("Lexer should have no warnings", "Lexer warnings", lexer.warnings);
		testList("Parser should have no errors", "Parser errors", parser.errors);
		testList("Parser should have no warnings", "Parser warnings", parser.warnings);
		if (visible) {
			ASTFrame frame = new ASTFrame(code, parser.getAST());
			frame.setVisible(true);
			System.out.print("Press ENTER: ");
			System.in.read();
			frame.setVisible(false);
		}
		AstHelper.assertEquals(answer, parser.getAST());
	}
	private void testIncludedProgramCode(String code, String answer) throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(code.getBytes());
		YacllLexer lexer = new YacllLexer(inputStream);
		YacllParser parser = new YacllParser(lexer);
		parser.includedProgram();
		testList("Lexer should have no errors", "Lexer errors", lexer.errors);
		testList("Lexer should have no warnings", "Lexer warnings", lexer.warnings);
		testList("Parser should have no errors", "Parser errors", parser.errors);
		testList("Parser should have no warnings", "Parser warnings", parser.warnings);
		if (visible) {
			ASTFrame frame = new ASTFrame(code, parser.getAST());
			frame.setVisible(true);
			System.out.print("Press ENTER: ");
			System.in.read();
			frame.setVisible(false);
		}
		AstHelper.assertEquals(answer, parser.getAST());
	}
	
	public void testAssignment() throws Exception {
		testProgramCode("pi = 3.14159;", "(program (= pi 3.14159))");
		testProgramCode("x = sin*50 + width/2;", "(program (= x (+ (* sin 50) (/ width 2))))");
		testProgramCode("c = -1;", "(program (= (unary- 1)))");
		testProgramCode("d = 5 - 3;", "(program (= d (- 5 3)))");
		testProgramCode("e = 7 - -1;", "(program (= e (- 7 (unary- 1))))");
	}
	public void testWhile() throws Exception {
		testProgramCode("while (a<5) { a=a+1; }", "(program (while (condition (< a 5)) (block (= a (+ a 1)))))");
	}
	public void testRepeat() throws Exception {
		testProgramCode("repeat a = a + 1; until (a<5);", "(program (repeat (block (= a (+ a 1))) (test (< a 5))))");
		testProgramCode("repeat { a=a+1; b=0; } until (a<5);", "(program (repeat (block (= a (+ a 1)) (= b 0)) (test (< a 5))))");
	}
	public void testFor() throws Exception {
		testProgramCode("for (a=0; a<5; a=a+1) b=b+1;", "(program (for (= a 0) (< a 5) (= a (+ a 1)) (block (= b (+ b 1)))))");
		testProgramCode("for (a=0; a<5; a=a+1) { b=b+1; c=c+2; }", "(program (for (= a 0) (< a 5) (= a (+ a 1)) (block (= b (+ b 1))) (= c (+ c 2))))");
	}
	public void testComparativeExpression() throws Exception {
		testProgramCode("b = a < 0;", "(program (= b (< a 0)))");
		testProgramCode("b = a <= 0;", "(program (= b (<= a 0)))");
		testProgramCode("b = a > 0;", "(program (= b (> a 0)))");
		testProgramCode("b = a >= 0;", "(program (= b (>= a 0)))");
		testProgramCode("b = a <> 0;", "(program (= b (<> a 0)))");
		testProgramCode("b = a != 0;", "(program (= b (!= a 0)))");
		testProgramCode("b = a = 0;", "(program (= b (= a 0)))");
	}
	public void testFunction() throws Exception {
		testProgramCode("plot(x,y);", "(program (call plot (arguments x y)))");
		testProgramCode("x = sin(a)*50 + width()/2;", "(program (= x (+ (call sin (arguments a)) 50) (/ (call width) 2)))");
		testProgramCode("x = width();", "(program (= x (call width)))");
	}
	public void testProblem1() throws Exception {
		testProgramCode("x = (centerx - mouthwidth) + a;", "(program (= x (- centerx (+ mouthwidth a))))");
	}
	public void testWithComments() throws Exception {
		testProgramCode("a=1;\n// comment\nb=2;", "(program (= a 1) (= b 2))");
		testProgramCode("a = /* comment */ 234;", "(program (= a 234))");
	}
	public void testFunctionDeclaration() throws Exception {
		testProgramCode("function aaa() { a=1; return a; }", "(program (function aaa (fnBlock (= a 1) (return a))))");
	}
	public void testInvoke() throws Exception {
		testProgramCode("invoke(\"Math.min(d,d)\", a,b);", "(program (invoke Math.min(d,d) (arguments a b)))");
	}
	public void testFunctionInvocation() throws Exception {
		testProgramCode("function min(a,b) { invoke(\"Math.min(d,d)\", a,b); } a = min(1,5);",
				 "(program (function min (fnParam a b) (fnBlock (invoke Math.min(d,d) (arguments a b)))) (= a call min (arguments 1 5)))");
	}
	public void testFunctionReturn() throws Exception {
		testProgramCode("function five() { return 5; } a = five();",
				"(program (function five (fnBlock (return 5))) (= a (call five)))");
	}
	public void testIncludedProgram() throws Exception {
		testIncludedProgramCode("function five() { return 5; }",
				"(includedProgram (function five (fnBlock (return 5)))");
	}
	public void testUseStatement() throws Exception {
		testProgramCode("use \"math\";", "(program (use math))");
	}
	public void testUseStatements() throws Exception {
		testProgramCode("use \"math\"; use \"graphics\";", "(program (use math) (use graphics))");
	}
}
