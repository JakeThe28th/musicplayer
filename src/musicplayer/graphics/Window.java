package musicplayer.graphics;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import musicplayer.components.settings.Settings;
import musicplayer.utility.Log;


class Window {

	private static long 		window = -1;
	private static boolean 		should_close = false;
	
	public static int 			window_height;
	public static int 			window_width;

	protected static void init(int width, int height, String title) {

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		
		if (!Settings.use_native_window_decorations()) {
			glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
			GraphicsAPI.decorated = false;
		}
		
		if (Settings.enable_anti_aliasing_requires_restart()) {
			glfwWindowHint(GLFW_SAMPLES, Settings.anti_aliasing_samples_requires_restart());
		}

		// Create the window
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		window_height = height;
		window_width = width;
				
		// Set up a callback to correct the viewport size when the window is resized
		GLFW.glfwSetWindowSizeCallback(window, (window, w, h) -> {
			window_height = h;
			window_width = w;
			Graphics.fixViewScale(window_width, window_height);
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		//glfwSwapInterval(1);
		setVsync(Settings.enable_vsync());

		// Make the window visible
		glfwShowWindow(window);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		Graphics.fixViewScale(window_width, window_height);
		
		Input.setCallbacks(window);
		
		if (Settings.enable_anti_aliasing_requires_restart()) {
			GL13.glEnable(GL13.GL_MULTISAMPLE);
		}
		
	}

	protected static void tick() {
		glfwSwapBuffers(window); // swap the color buffers
		
		Input.clearKeys();

		// Poll for window events.
		glfwPollEvents();
	}

	protected static boolean shouldClose() {
		return (should_close || GLFW.glfwWindowShouldClose(window));
	}

	protected static void free() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}
	
	protected static long identifier() {
		return window;
	}

	public static void title(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}

	public static void setVsync(boolean value) {
		if (window != -1) glfwSwapInterval(value ? 1 : 0);		
	}

}
