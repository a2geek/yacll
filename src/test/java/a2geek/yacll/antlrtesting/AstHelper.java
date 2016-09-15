package a2geek.yacll.antlrtesting;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Stack;

import antlr.collections.AST;

/**
 * Helper classes to test ANTLR AST shape.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class AstHelper {
	/**
     * Builds an AstNode tree from expectedShape and compares against the givenShape.
	 * @throws IOException if AST shape is not able to be built from string
     * @throws ComparisonException if trees do not match
     */
    public static void assertEquals(String expectedShape, AST givenShape) throws IOException {
    	buildAstShape(expectedShape).assertEquals(givenShape);
    }

    /**
     * Construct an AstNode from the given String representation.
     * The basic premise is to work a Stack. When a '(' is seen, the current AstNode
     * needs to be saved on the stack and a new one is created.  When the ')' is
     * seen, the AstNode is pulled from the stack.
     *
     * @throws IOException if EOF unexpectedly seen or any other dasterdly deed
     * @throws java.util.EmptyStackException if parenthesis are unbalanced
     */
    public static AstNode buildAstShape(String expectedShape) throws IOException { 
        StreamTokenizer st = new StreamTokenizer(new StringReader(expectedShape));
        st.resetSyntax();
    	st.wordChars('a', 'z');
    	st.wordChars('A', 'Z');
    	st.wordChars(128 + 32, 255);
    	st.whitespaceChars(0, ' ');
    	st.commentChar('/');
    	st.quoteChar('"');
    	st.quoteChar('\'');
    	st.wordChars('0','9');
    	st.wordChars('-','-');
    	st.wordChars('.','.');
    	st.wordChars('_','_');
    	//st.parseNumbers();
        AstNode ast = null; 
        AstNode firstAst = null; 
        Stack<AstNode> stack = new Stack<AstNode>(); 
        while (st.nextToken() != StreamTokenizer.TT_EOF) { 
            switch (st.ttype) { 
                case StreamTokenizer.TT_WORD: 
                        if (ast.description == null) { 
                        	ast.description = st.sval; 
                        } else { 
							ast.children.add(new AstNode(st.sval)); 
                        } 
                        break; 
                case StreamTokenizer.TT_NUMBER:
                		throw new IllegalStateException("TT_NUMBER is not used.");
//                        if (ast.description == null) { 
//							ast.description = Double.toString(st.nval); 
//                        } else { 
//							ast.children.add(new AstNode(Double.toString(st.nval))); 
//                        } 
//                        break; 
                case StreamTokenizer.TT_EOL: 
                        break;	// we just ignore this - you should be able to give multi-line AST representations 
                case StreamTokenizer.TT_EOF: 
                        throw new IOException("Unexpected EOF"); 
                case '(': 
                        stack.push(ast); 
                        ast = new AstNode(); 
                        if (firstAst == null) firstAst = ast;
                        else stack.peek().children.add(ast);
                        break; 
                case ')': 
                        ast = (AstNode)stack.pop(); 
                        break; 
                default:
                		if (ast.description == null) {
                			ast.description = Character.toString((char)st.ttype);
                		} else {
                			ast.children.add(new AstNode(Character.toString((char)st.ttype)));
                		}
                		break;
            } 
        }
        return firstAst;
    } 
}
