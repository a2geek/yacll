package a2geek.yacll.antlrtesting;

import java.io.IOException;

import antlr.CommonAST;
import antlr.collections.AST;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

/**
 * Exercise AstHelper class.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class AstHelperTest extends TestCase {
	public void testBuildAstShape_ParentChild() throws IOException {
		AstNode ast = AstHelper.buildAstShape("(parent child1 child2)");
		assertNotNull(ast);
		assertEquals("parent", ast.description);
		assertEquals(2, ast.children.size());
		assertEquals("child1", ast.children.get(0).description);
		assertEquals("child2", ast.children.get(1).description);
	}
	public void testBuildAstShape_NestedTreeStructure() throws IOException {
		AstNode ast = AstHelper.buildAstShape("(p1 (pc2 c3 c4) (pc5 (pc6)))");
		assertNotNull(ast);
		assertEquals("p1", ast.description);
		assertEquals(2, ast.children.size());
		assertEquals("pc2", ast.children.get(0).description);
		assertEquals(2, ast.children.get(0).children.size());
		assertEquals("c3", ast.children.get(0).children.get(0).description);
		assertEquals("c4", ast.children.get(0).children.get(1).description);
		assertEquals("pc5", ast.children.get(1).description);
		assertEquals(1, ast.children.get(1).children.size());
		assertEquals("pc6", ast.children.get(1).children.get(0).description);
	}
	public void testAssertEquals_ParentChild() throws IOException {
		AST realAst = new CommonAST();
		realAst.setText("parent");
			AST childAst1 = new CommonAST();
			childAst1.setText("child1");
			realAst.addChild(childAst1);
			AST childAst2 = new CommonAST();
			childAst2.setText("child2");
			realAst.addChild(childAst2);
		AstHelper.assertEquals("(parent child1 child2)", realAst);
	}
	public void testAssertEquals_Not() throws IOException {
		AST realAst = new CommonAST();
		realAst.setText("parent");
			AST childAst1 = new CommonAST();
			childAst1.setText("child1");
			realAst.addChild(childAst1);
		try {
			AstHelper.assertEquals("(parent child1 child2)", realAst);
			fail("This should fail");
		} catch (ComparisonFailure expected) {
			// We're ok
		}
	}
	public void testAssertEquals_NestedTreeStructure() throws IOException {
		AST realAst = new CommonAST();
		realAst.setText("p1");
			AST childAst = new CommonAST();
			childAst.setText("pc2");
			realAst.addChild(childAst);
			AST childAst2 = new CommonAST();
			childAst2.setText("c3");
			childAst.addChild(childAst2);
			childAst = new CommonAST();
			childAst.setText("pc4");
			realAst.addChild(childAst);
		AstHelper.assertEquals("(p1 (pc2 c3) pc4)", realAst);
	}
	public void testAssertEquals_FailingNestedTreeStructure() throws IOException {
		AST realAst = new CommonAST();
		realAst.setText("p1");
			AST childAst = new CommonAST();
			childAst.setText("pc2");
			realAst.addChild(childAst);
			AST childAst2 = new CommonAST();
			childAst2.setText("c3");
			childAst.addChild(childAst2);
			childAst = new CommonAST();
			childAst.setText("pc4");
			realAst.addChild(childAst);
		try { 
			AstHelper.assertEquals("(p1 (pc2 no_match) pc4)", realAst);
			fail("This should fail");
		} catch (ComparisonFailure expected) {
			// We're ok
		}
	}
	public void testAssertEquals_WithNumbers() throws IOException {
		AST realAst = new CommonAST();
		realAst.setText("123");
		AstHelper.assertEquals("(123)", realAst);
	}
}
