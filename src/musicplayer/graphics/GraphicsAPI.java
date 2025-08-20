package musicplayer.graphics;

import java.io.IOException;
import java.util.Stack;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

/** It's probably overkill to use the GUI library here so,
 *  making it its' own thing... 
 *  !!! Use this instead of directly calling stuff from the 'ext' package !!!
 *      (cuz I'll probably change that stuff later, it's better to have a 
 *       layer of abstraction so everything doesn't immediately break) */
public class GraphicsAPI {
	
	public static final Vector4f TRANSPARENT_WHITE 	= new Vector4f(1, 1, 1, 0.25f);
	public static final Vector4f TRANSLUCENT_WHITE 	= new Vector4f(1, 1, 1, 0.5f);
	public static final Vector4f TRANSPARENT_RED 	= new Vector4f(1, 0, 0, 0.25f);
	public static final Vector4f BLACK 				= new Vector4f(0,0,0,1);
	public static final Vector4f WHITE 				= new Vector4f(1,1,1,1);
	public static final Vector4f TRANSLUCENT_BLACK 	= new Vector4f(0, 0, 0, 0.5f);

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
	
	public static void color(Vector4f c) {
		if (c == null) throw new Error();
		Shapes.color(c);
		text.color(c);
	}
	
	public static void text(int x, int y, int depth, String string) {
		text.text(x, y, depth, string);
	}

	public static void rect(int left, int top, int right, int bottom, int depth) {
		Shapes.rect(left, top, right, bottom, depth);
	}
	
	public static void rect(int left, int top, int right, int bottom, int depth, Texture texture) {
		Shapes.rect(left, top, right, bottom, depth, texture);
	}
	
	public static void rect(Rectangle rect, int depth) {
		rect(rect.left(), rect.top(), rect.right(), rect.bottom(), depth);
	}
	
	public static void rect(Rectangle rect, int depth,  Texture texture) {
		rect(rect.left(), rect.top(), rect.right(), rect.bottom(), depth, texture);
	}
	
	public static void dot(int x, int y, int z, int radius) {
		Shapes.dot(x, y, z, radius);
	}
	

	public static void icon(int x, int y, int z, String name, int size) {
		Icons.icon(x, y, z, name, size);
	}
	
	static Stack<Rectangle> scissor_stack = new Stack<Rectangle>();
	
	/** Saves the current scissor and then sets the new scissor to this. */
	public static void push_scissor(Rectangle box) {
		scissor_stack.push(RenderQueue.current_scissor);
		RenderQueue.current_scissor = box;
	}
	
	/** Reverts to the previous scissor box. */
	public static void pop_scissor() {
		RenderQueue.current_scissor = scissor_stack.pop();
	}
	
	public static Rectangle scissor() {
		return RenderQueue.current_scissor;
	}

	
	/* -- Input stuffs -- */
	
	public static int mouseX() { return Input.mouseX(); }
	public static int mouseY() { return Input.mouseY(); }
	
	public static boolean left_click_pressed() {
		return Input.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}
	
	public static boolean left_click_released() {
		return Input.mouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}
	
	public static boolean left_click_down() {
		return Input.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}

	public static void setFadeColumn(int left, int left_inner, int right, int right_inner) {
		RenderQueue.temp_integer_uniforms.put("first_fade_transparent_x", left);
		RenderQueue.temp_integer_uniforms.put("first_fade_opaque_x", left_inner);
		RenderQueue.temp_integer_uniforms.put("second_fade_transparent_x", right);
		RenderQueue.temp_integer_uniforms.put("second_fade_opaque_x", right_inner);
	}
	
	public static void resetFadeColumn() {
		setFadeColumn(0,0,0,0);
	}

}
