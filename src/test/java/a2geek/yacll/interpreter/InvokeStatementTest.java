package a2geek.yacll.interpreter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import a2geek.yacll.interpreter.InvokeStatement;
import a2geek.yacll.interpreter.State;
import junit.framework.TestCase;

/**
 * Exercise the invoke portion of the InvokeStatement class.
 */
public class InvokeStatementTest extends TestCase {
	private State state;
	private InvokeStatement invokeStatement;
	
	protected void setUp() {
		this.state = new State();
		this.invokeStatement = new InvokeStatement(null, null); // just testing invoke method
	}
	public void testInvokeNoArgumentFunctions() throws Exception {
		assertEquals(State.WIDTH, invokeStatement.invoke(state, "image.getWidth", null));
		assertEquals(State.HEIGHT, invokeStatement.invoke(state, "image.getHeight", new ArrayList<Object>()));
	}
	public void testInvokePlotFunction() throws Exception {
		List<Object> args = new ArrayList<Object>();
		args.add(5);
		args.add(5);
		args.add(1);
		args.add(1);
		assertNull(invokeStatement.invoke(state, "graphics.fillOval(i,i,i,i)", args));
	}
	public void testInvokeLineFunction() throws Exception {
		List<Object> args = new ArrayList<Object>();
		args.add(5);
		args.add(5);
		args.add(10);
		args.add(10);
		assertNull(invokeStatement.invoke(state, "graphics.drawLine(i,i,i,i)", args));
	}
	public void testInvokeMathFunctions() throws Exception {
		List<Object> args = new ArrayList<Object>();
		args.add(1.0);
		args.add(5.0);
		assertEquals(1.0, invokeStatement.invoke(state, "Math.min(d,d)", args));
		assertEquals(5.0, invokeStatement.invoke(state, "Math.max(d,d)", args));
	}
	public void testInvokeDrawStringFunction() throws Exception {
		List<Object> args = new ArrayList<Object>();
		args.add("Hello, World!");
		args.add(50);
		args.add(50);
		assertNull(invokeStatement.invoke(state, "graphics.drawString(S,i,i)", args));
	}
	public void testOtherArgument() throws Exception {
		List<Object> args = new ArrayList<Object>();
		args.add(Color.decode("707070"));
		assertNull(invokeStatement.invoke(state, "graphics.setColor(java/awt/Color)", args));
	}
}
