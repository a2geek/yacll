package a2geek.yacll.interpreter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

/**
 * Maintains current state of the interpreter.
 * The primary purpose is to track variable state.
 * 
 * @author a2geek@users.noreply.github.com
 */
public class State {
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public Map<String,FunctionDefinition> functions = new HashMap<String,FunctionDefinition>();
	public Map<String,Object> variables = new HashMap<String,Object>();
	public BufferedImage image;
	public Graphics graphics;
	public JFrame window;
	
	/**
	 * Normal constructor that initializes the program-level state.
	 */
	public State() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		graphics = image.getGraphics();
	}
	
	/**
	 * Function-level constructor that initializes a new State based off a program-level state.
	 * Essentially, we keep the image, graphics, and window objects while (for the time being)
	 * throwing away functions and variables.
	 * TODO: Keep the parent state as an instance variable???
	 */
	public State(State parentState) {
		this.image = parentState.image;
		this.graphics = parentState.graphics;
		this.window = parentState.window;
	}
}
