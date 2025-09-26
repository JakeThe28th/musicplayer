package musicplayer.graphics;

import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_FLOATING;

import java.io.IOException;
import java.util.Stack;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import musicplayer.components.settings.Settings;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;
import musicplayer.utility.Utility;

/** It's probably overkill to use the GUI library here so,
 *  making it its' own thing... 
 *  !!! Use this instead of directly calling stuff from the 'ext' package !!!
 *      (cuz I'll probably change that stuff later, it's better to have a 
 *       layer of abstraction so everything doesn't immediately break) */
public class GraphicsAPI {
	
	public static final Vector4f TRANSPARENT_WHITE 	= new Vector4f(1, 1, 1, 0.25f);
	public static final Vector4f TRANSLUCENT_WHITE 	= new Vector4f(1, 1, 1, 0.5f);
	public static final Vector4f TRANSPARENT_RED 	= new Vector4f(1, 0, 0, 0.25f);
	public static final Vector4f TRANSPARENT_AQUA 	= new Vector4f(0, 1, 0.75f, 0.25f);
	public static final Vector4f BLACK 				= new Vector4f(0,0,0,1);
	public static final Vector4f WHITE 				= new Vector4f(1,1,1,1);
	public static final Vector4f TRANSLUCENT_BLACK 	= new Vector4f(0, 0, 0, 0.5f);
	
	public static final Vector4f BLACK75 			= new Vector4f(0,0,0,0.75f);
	
	public static boolean is_iconified = false;

	static int width = 512;
	static int height = 8*96;
	
	static Text text = new Text();
	
	public static void init() throws IOException {
		Graphics.setup(width, height, "music thingy");
		text.font_size(Settings.font_size());
	}
	
	public static int 		width () { return Window.window_width; }
	public static int 		height() { return Window.window_height; }
	public static boolean 	isOpen() { return Graphics.isActive(); }
	
	public static void 		render() { RenderQueue.render(); }
	
	static String title = "None";
	
	public static void		title(String title) {
		Window.title(title);
		GraphicsAPI.title = title;
	}
	
	public static String	title() {
		return title;
	}
	
	/* -- Drawing stuffs -- */
	
	public static Vector2i size(String string) {
		return text.size(string);
	}
	
	/* -- -- */
	
	public static void font_size(int size) {
		text.font_size(size);
	}
		
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
		if (RenderQueue.current_scissor == null) return new Rectangle(0, 0, width, height);
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

	public static double scrollX() { return Input.scrollX(); }
	public static double scrollY() { return Input.scrollY() * 35; }

	public static void setMinimumWindowSize(int minw, int minh, int maxw, int maxh) {
		GLFW.glfwSetWindowSizeLimits(Window.identifier(), minw, minh, maxw, maxh);
	}

	public static void center_text(int depth, int y_offset, String string) {
		text((width/2)-(size(string).x/2), ((height/2)-(size(string).y/2)) + y_offset, depth, string);
	}

	public static void persistentRendering(boolean b) {
		RenderQueue.delete_queue_after_rendering = !b;
	}
	
	public static String input_string() {
		return Input.input_string;
	}
	
	public static void input_string(String str) {
		Input.input_string = str;
	}

	public static int getKeyState(int key) {
		return Input.getKeyActionWithScancode(GLFW.glfwGetKeyScancode(key));
	}

	public static void clearColor(float x, float y, float z, float a) {
		Graphics.clearColor(x, y, z, a);
	}
	
	public static boolean decorated;
	
	public static void setDecorated(boolean b) {
		decorated = b;
		if (b) {
			GLFW.glfwSetWindowAttrib(Window.identifier(), GLFW_DECORATED, GLFW_TRUE);
		} else {
			GLFW.glfwSetWindowAttrib(Window.identifier(), GLFW_DECORATED, GLFW_FALSE);
		}
	}

	public static boolean is_initialized() {
		return Window.identifier() != -1;
	}
	
	public static boolean pinned;

	public static void setWindowPinned(boolean b) {
		pinned = b;
		if (b) {
			GLFW.glfwSetWindowAttrib(Window.identifier(), GLFW_FLOATING, GLFW_TRUE);
		} else {
			GLFW.glfwSetWindowAttrib(Window.identifier(), GLFW_FLOATING, GLFW_FALSE);
		}
	}

	public static float focused_window_opacity = 1;
	public static float unfocused_window_opacity = 1;

	public static void setUnfocusedWindowOpacity(float opacity) {
		unfocused_window_opacity = opacity;
	}

	public static void setFocusedWindowOpacity(float opacity) {
		focused_window_opacity = opacity;
	}
	
	static long mouse_hover_time = 0;
	static boolean mouse_is_hovering = false;
	
	public static void tickOpacity() {
		// If the opacity isn't set to full
		if (focused_window_opacity != 1 && unfocused_window_opacity != 1) {
			// If the opacities aren't equal
			if (focused_window_opacity != unfocused_window_opacity) {

				// keep track of when the mouse last entered the window
				if (GLFW.glfwGetWindowAttrib(Window.identifier(), GLFW.GLFW_HOVERED) == GLFW.GLFW_FALSE) {
					if (mouse_is_hovering) {
						mouse_hover_time = System.currentTimeMillis();
					}
					mouse_is_hovering = false;
				} else {
					if (!mouse_is_hovering) {
						mouse_hover_time = System.currentTimeMillis();
					}
					mouse_is_hovering = true;
				}
				
				float time_seconds = (System.currentTimeMillis() - mouse_hover_time) / 1000f;
				float amount = Math.clamp(time_seconds*2, 0, 1);
				
				float a = unfocused_window_opacity;
				float b = focused_window_opacity;
				if (!mouse_is_hovering) {
					a = focused_window_opacity;
					b = unfocused_window_opacity;
				}

				GLFW.glfwSetWindowOpacity(Window.identifier(), (float) Utility.lerp(a, b, amount));
				
			} else {
				// If the opacities *are* equal, just set them once
				if (mouse_hover_time > 0) {
					mouse_hover_time = -1;
					GLFW.glfwSetWindowOpacity(Window.identifier(), focused_window_opacity);
				}
			}
		} else if (mouse_hover_time > 0) {
			// If the opacity *is* set to full, just set it once
			mouse_hover_time = -1;
			GLFW.glfwSetWindowOpacity(Window.identifier(), 1);
		}
	}

}
