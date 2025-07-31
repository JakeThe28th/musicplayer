package musicplayer.ext;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.MemoryStack;

/** Handles the window, all textures, shaders, and input, as well as rendering/the render queue.
 * Basically, everything but audio. TODO */
public class B_Graphics {
	
	/* -- Public API stuff -- */
	
	// Most of the code outside of this will probably be changed
	// So, everything else is private so you can't use those

	public static void setup() {
		init();
		WINDOW_init(400, 400, "Graphicics tesntmeoewo");
		B_Graphics.clearColor(0, 0, 0, 0.8f);
		shader();
		fixWorldTransform();
	}

	public static void quit() {
		WINDOW_free();
		free();
	}
	
	public static boolean isActive() {
		return !WINDOW_shouldClose();
	}
	
	// Render queue //
	
	static Matrix4f world_transform;
	
	private static record B_RenderState(B_Mesh mesh, Matrix4f transform, Vector4f color, B_Texture texture) { }
	
	static ArrayList<B_RenderState> queue = new ArrayList<B_RenderState>();
	
	public static void render() {
		clear();
		for (B_RenderState state : queue) {
			state.mesh.bind();
			GL40.glUniformMatrix4fv(GL40.glGetUniformLocation(shader(), "transform"), false, floats(state.transform));
			
			GL40.glUniform4f(GL40.glGetUniformLocation(shader(), "mix_color"), state.color.x, state.color.y, state.color.z, state.color.w);
			
			if (state.texture != null) {
				glBindTexture(GL_TEXTURE_2D, state.texture.texture);
			}
			
			GL40.glDrawElements(GL_TRIANGLES, state.mesh.count(), GL_UNSIGNED_INT, 0);
		}
		WINDOW_tick();
	}
	
	public static void queue(B_Mesh mesh, Matrix4f transform, Vector4f color, B_Texture texture) {
		queue.add(new B_RenderState(mesh, transform, color, texture));
	}
	

	// ++ --          -- ++//
	/* -- Overall state -- */
	// ++ --          -- ++//

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
	
	private static void clearColor(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}

	private static void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	// ++ --    -- ++//
	/* -- Shaders -- */
	// ++ --    -- ++//

	static int shader = -1;
	// There's only one shader needed in this program, so I'm hardcoding it.
	private static int shader() {
		if (shader == -1) {
			int vertex_shader = createShader(GL40.GL_VERTEX_SHADER, 
					"""
						#version 330 core
						layout (location = 0) in vec3 v_postion;
						layout (location = 1) in vec2 v_texcoord;

						uniform mat4 world_transform;						
						out vec2 f_texcoord;

						void main()
						{
							f_texcoord = v_texcoord;
						    gl_Position = world_transform * vec4(v_postion, 1.0);
						}
					""");
			int fragment_shader = createShader(GL40.GL_FRAGMENT_SHADER, 
					"""
						#version 330 core
						out vec4 FragColor;

						uniform sampler2D texture_image;
						in vec2 f_texcoord;
						
						void main()
						{
						    //FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
							FragColor = texture(texture_image, f_texcoord);
						} 
					""");
			
			shader = GL40.glCreateProgram();
			GL40.glAttachShader(shader, vertex_shader);
			GL40.glAttachShader(shader, fragment_shader);
			GL40.glLinkProgram(shader);
			
			// linking failed
		 	if (GL40.glGetProgrami(shader, GL40.GL_LINK_STATUS) == 0) {
		 		 String info_log = GL40.glGetProgramInfoLog(shader, 512);
		 		 throw new RuntimeException("Shader linking failed: " + info_log);
		 	}
			
		 	GL40.glDeleteShader(vertex_shader);
		 	GL40.glDeleteShader(fragment_shader);
		 	
		 	bindShader(shader);
		 	
		}
		return shader;
	}

	private static int createShader(int type, String source) {
		int shader = GL40.glCreateShader(type);
	 	GL40.glShaderSource(shader, source);
	 	GL40.glCompileShader(shader);
	 	
	 	// compiling failed
	 	if (GL40.glGetShaderi(shader, GL40.GL_COMPILE_STATUS) == 0) {
	 		 String info_log = glGetShaderInfoLog(shader, 512);
	 		 throw new RuntimeException("Shader compilation failed: " + info_log);
	 	}
	 	
		return shader;
	}
	
	// Since only one shader is needed, this will probably only be 
	// called once, in shader() at startup, but might as well have it
	private static void bindShader(int shader) {
		GL40.glUseProgram(shader);
	}
	
	// ++ --         -- ++//
	/* -- Window stuff -- */
	// ++ --         -- ++//
	
	// The window handle
	private static long window;
	private static boolean should_close = false;
	
	static int window_height;
	static int window_width;

	private static void WINDOW_init(int width, int height, String title) {
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

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
			fixWorldTransform();
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
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		
	}

	private static void fixWorldTransform() {
		GL11.glViewport(0, 0, window_width, window_height);
		world_transform = new Matrix4f().ortho(0, window_width, 0, window_height, -1024f, 1024f);
		GL40.glUniformMatrix4fv(GL40.glGetUniformLocation(shader(), "world_transform"), false, floats(world_transform));
	}

	private static void WINDOW_tick() {
		glfwSwapBuffers(window); // swap the color buffers

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
	}

	private static boolean WINDOW_shouldClose() {
		return (should_close || GLFW.glfwWindowShouldClose(window));
	}

	private static void WINDOW_free() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}

	// utility
	
	private static float[] floats(Matrix4f world_transform2) {
		float[] floats = new float[4*4];
		world_transform.get(floats);
		return floats;
	}

}
