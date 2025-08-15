package musicplayer.ext;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.*;
import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

public class Graphics {
	
	public static void setup(int width, int height, String name) {
		init();
		Window.init(width, height, name);
		Graphics.clearColor(0, 0, 0, 0.8f);
		Shader.shader();
	}

	public static void 		quit() 		{ Window.free(); free(); }
	public static boolean 	isActive() 	{ return !Window.shouldClose(); }
	public static void 		clear() 	{ glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); }
	
	public static void 		clearColor(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}
	
	/** Sets the openGL viewport & the shader's world 
	 *  transform so that when drawing, 1 unit = 1 pixel. 
	 *  (Called in a callback set in the Window class.) */
	static void fixViewScale(int width, int height) {
		GL11.glViewport(0, 0, width, height);
		Matrix4f world_transform = new Matrix4f().ortho(0, width, height, 0, -1024f, 1024f);
		Shader.uniform("world_transform", world_transform);
	}

	// --==+  internal methods  +==-- //

	private static void init() {
		/* (https://www.lwjgl.org/guide) */
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() ) throw new IllegalStateException("Unable to initialize GLFW");
	}

	/** (https://www.lwjgl.org/guide) Terminate GLFW and free the error callback */
	private static void free() {
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
}
