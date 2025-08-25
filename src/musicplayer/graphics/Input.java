package musicplayer.graphics;

import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import org.lwjgl.glfw.GLFW;

class Input {

	public static void clearKeys() {
		// Events
		current_keys = new Key[1024];
		current_mouse_buttons = new MouseButton[8];
		mouse_scroll_x = 0; mouse_scroll_y = 0;
	}
	
	public record Key(int key, int scancode, int action, int mods) {};
	public record MouseButton(int button, int action, int mods) {};

	static String 			input_string 			= "";
	static Key[] 			current_keys 			= new Key[1024];
	static MouseButton[] 	current_mouse_buttons 	= new MouseButton[8];
	static boolean[] 		down_mouse_buttons 		= new boolean[8];
	static double 			mouse_scroll_x 			= 0;
	static double 			mouse_scroll_y 			= 0;
	static Key				last_key 				= null;
	static double			mouse_x					= 0;
	static double			mouse_y					= 0;

	public static void setCallbacks(long current_window) {
		
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(current_window, (window, key, scancode, action, mods) -> {
			setKeyWithScancode(key, scancode, action, mods);
			last_key = new Key(key, scancode, action, mods);
			
			if (action == GLFW.GLFW_PRESS) setKeyScancodeDown(scancode, true);
			if (action == GLFW.GLFW_RELEASE) setKeyScancodeDown(scancode, false);
			
			if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT)
				if (key == GLFW.GLFW_KEY_BACKSPACE && input_string.length() > 0) 
					input_string = input_string.substring(0, input_string.length()-1);

			
			//Log.send(scancode + " : " + key);
			//if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
			//	glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		
		glfwSetCursorPosCallback(current_window, (window, xpos, ypos) -> {
			setMousePos(xpos, ypos);
		});
		
		glfwSetMouseButtonCallback(current_window, (window, button, action, mods) -> {
			setMouseButton(button, action, mods);
			
			if (action == GLFW.GLFW_PRESS) setMouseButtonDown(button, true);
			if (action == GLFW.GLFW_RELEASE) setMouseButtonDown(button, false);
		});
		
		glfwSetScrollCallback(current_window, (window, xoffset, yoffset) -> {
			setMouseScroll(xoffset, yoffset);
		});

		glfwSetCharCallback(current_window, (window, codepoint) -> {
			 input_string += (char) codepoint;
		});
	}

	private static void setMouseButtonDown(int button, boolean b) {
		down_mouse_buttons[button] = b;
	}

	private static void setKeyScancodeDown(int scancode, boolean b) {
		// TODO Auto-generated method stub
		
	}

	private static void setKeyWithScancode(int key, int scancode, int action, int mods) {
		// TODO Auto-generated method stub
		
	}

	private static void setMouseScroll(double xoffset, double yoffset) {
		mouse_scroll_x = xoffset;
		mouse_scroll_y = yoffset;
	}

	private static void setMouseButton(int button, int action, int mods) {
		current_mouse_buttons[button] = new MouseButton(button, action, mods);
	}

	private static void setMousePos(double xpos, double ypos) {
		mouse_x = xpos;
		mouse_y = ypos;
	}
	
	// -- Getters -- //
	
	public static int mouseX() { return (int) mouse_x; }
	public static int mouseY() { return (int) mouse_y; }
	
	public static boolean mouseButtonDown(int button) {
		return down_mouse_buttons[button];
	}
	
	public static boolean mouseButtonPressed(int button) {
		if (current_mouse_buttons[button] == null) return false;
		return current_mouse_buttons[button].action == GLFW.GLFW_PRESS;
	}
	
	public static boolean mouseButtonReleased(int button) {
		if (current_mouse_buttons[button] == null) return false;
		return current_mouse_buttons[button].action == GLFW.GLFW_RELEASE;
	}

	public static double scrollX() { return mouse_scroll_x; }
	public static double scrollY() { return mouse_scroll_y; }
}
