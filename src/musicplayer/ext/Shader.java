package musicplayer.ext;

import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

public class Shader {

	// There's only one shader needed in this program, so I'm hardcoding it.
	static int shader = -1;
	protected static int shader() {
		if (shader == -1) {
			int vertex_shader = create(GL40.GL_VERTEX_SHADER, 
					"""
						#version 330 core
						layout (location = 0) in vec3 v_position;
						layout (location = 1) in vec2 v_texcoord;

						uniform mat4 world_transform;	
						uniform mat4 transform;											
						out vec2 f_texcoord;

						void main()
						{
							f_texcoord = v_texcoord;
						    gl_Position = world_transform * transform * vec4(v_position, 1.0);
						}
					""");
			int fragment_shader = create(GL40.GL_FRAGMENT_SHADER, 
					"""
						#version 330 core
						out vec4 FragColor;

						uniform sampler2D texture_image;
						in vec2 f_texcoord;
						uniform vec4 mix_color;

						// (for the scrolling song titles)
						uniform float fade_transparent_x = 0;
						uniform float fade_opaque_x = 0;

						void main()
						{
						    //FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
							FragColor = texture(texture_image, f_texcoord) * mix_color;
	
							if (FragColor.a < 0.001) discard;

							// Fading (for the scrolling song titles)
							float t = (gl_FragCoord.x - fade_transparent_x) / (fade_opaque_x - fade_transparent_x);
							FragColor.a = clamp(t, 0, 1);
							if (FragColor.a < 0.001) discard;
						} 
					""");
			GL40.glEnable(GL40.GL_BLEND);  
			GL40.glBlendFunc(GL40.GL_SRC_ALPHA, GL40.GL_ONE_MINUS_SRC_ALPHA);
			
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
		 	
		 	bind(shader);
		 	
		}
		return shader;
	}
	
	protected static void uniform(String uniform, Matrix4f matrix) {
		GL40.glUniformMatrix4fv(
				GL40.glGetUniformLocation(shader(), uniform), 
				false, 
				floats(matrix)
			);
	}
	
	protected static void uniform(String uniform, Vector4f vector) {
		GL40.glUniform4f(
				GL40.glGetUniformLocation(shader(), uniform), 
				vector.x, 
				vector.y, 
				vector.z, 
				vector.w
			);
	}
	
	// --==+  internal methods  +==-- //

	/** Creates an openGL shader from source code,
	    used only in shader(). I'd make it a sub-method
	    if that was a feature Java had... */
	private static int create(int type, String source) {
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
	
	/** Since only one shader is needed, this will probably only be 
	    called once, in shader() at startup, but might as well have it */
	protected static void bind(int shader) {
		GL40.glUseProgram(shader);
	}
	
	/** Returns an array of floats from a given matrix.
	 *  ...Because that takes more than one line,
	 *  for some reason... */
	private static float[] floats(Matrix4f matrix) {
		float[] floats = new float[4*4];
		matrix.get(floats);
		return floats;
	}
	
}
