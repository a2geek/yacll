package a2geek.yacll.antlrtesting;

import java.util.ArrayList;
import java.util.List;

import junit.framework.ComparisonFailure;
import antlr.collections.AST;

/**
 * Very basic AST to represent and compare AST shape.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class AstNode { 
    public String description; 
    public List<AstNode> children = new ArrayList<AstNode>(); 
    public AstNode() {}; 
    public AstNode(String description) { this.description = description; }
    /**
     * Render a parenthesized string representation of the AST tree.
     */
    public String toString() { 
            if (children.size() == 0) { 
                    return description; 
            } 
            StringBuffer buf = new StringBuffer(); 
            buf.append("("); 
            buf.append(description); 
            buf.append(" "); 
            for (int i=0; i<children.size(); i++) { 
                    if (i > 0) buf.append(" "); 
                    buf.append(children.get(i).toString()); 
            } 
            buf.append(")"); 
            return buf.toString(); 
    }
    /**
     * Compare to a real-live AST.
     * @throws ComparisonException if trees do not match 
     */
    public void assertEquals(AST ast) {
    	if (!ast.getText().equals(description)) {
    		throw new ComparisonFailure("Description does not match", description, ast.getText());
    	}
    	if (ast.getNumberOfChildren() != children.size()) {
    		throw new ComparisonFailure("Different number of children", 
    				Integer.toString(children.size()), 
    				Integer.toString(ast.getNumberOfChildren()));
    	}
    	ast = ast.getFirstChild();
    	for (AstNode astNode : children) {
    		astNode.assertEquals(ast);
    		ast = ast.getNextSibling();
    	}
    }
}