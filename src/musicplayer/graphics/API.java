package musicplayer.graphics;

import java.io.IOException;

import org.joml.Vector2i;
import org.joml.Vector4f;

/** It's probably overkill to use the GUI library here so,
 *  making it its' own thing... 
 *  !!! Use this instead of directly calling stuff from the 'ext' package !!!
 *      (cuz I'll probably change that stuff later, it's better to have a 
 *       layer of abstraction so everything doesn't immediately break) */
public class API {
	
	static int width = 512;
	static int height = 8*96;
	
	static Text text = new Text();
	
	public static void init() throws IOException {
		Graphics.setup(width, height, "music thingy");
	}
	
	public static int 		width () { return Window.window_width; }
	public static int 		height() { return Window.window_height; }
	public static boolean 	isOpen() { return Graphics.isActive(); }
	
	public static void 		render() { RenderQueue.render(); }
	
	/* -- Drawing stuffs -- */
	
	public static Vector2i size(String string) {
		return text.size(string);
	}
	
	/* -- -- */
		
	public static void color(float r, float g, float b, float a) {
		Shapes.color(new Vector4f(r,g,b,a));
		text.color(new Vector4f(r,g,b,a));
	}
	
	public static void text(int x, int y, int depth, String string) {
		text.text(x, y, depth, string);
	}

	public static void rect(int left, int top, int right, int bottom, int depth) {
		Shapes.rect(left, top, right, bottom, depth);
	}
	
	/** Saves the current scissor and then sets the new scissor to this. */
	public static void push_scissor(int left, int top, int right, int bottom) {
		// TODO 
	}

	/** Reverts to the previous scissor box. */
	public static void pop_scissor() {
		// TODO Auto-generated method stub
	}
	
	
	/* -- Input stuffs -- */
	
	public static int mouseX() { return Input.mouseX(); }
	public static int mouseY() { return Input.mouseY(); }

	
}
