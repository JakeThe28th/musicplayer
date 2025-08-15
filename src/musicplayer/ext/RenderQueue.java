package musicplayer.ext;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL40;

public class RenderQueue {
		
	public static HashMap<String, Integer> temp_integer_uniforms = new HashMap<String, Integer>();
	
	private static record RenderState(Mesh mesh, Matrix4f transform, Vector4f color, Texture texture, HashMap<String, Integer> integer_uniforms) { }
	
	static ArrayList<RenderState> queue = new ArrayList<RenderState>();
	
	public static void render() {
		Graphics.clear();
		for (RenderState state : queue) {
			state.mesh.bind();
			Shader.uniform("transform", state.transform);
			Shader.uniform("mix_color", state.color);

			for (String uniform : state.integer_uniforms.keySet()) {
				Shader.uniform(uniform, state.integer_uniforms.get(uniform));
			}
			
			if (state.texture != null) {
				glBindTexture(GL_TEXTURE_2D, state.texture.texture);
			}

			GL40.glDrawElements(GL_TRIANGLES, state.mesh.count(), GL_UNSIGNED_INT, 0);
		}
		temp_integer_uniforms.clear();
		queue.clear();
		Window.tick();
	}
	
	@SuppressWarnings("unchecked")
	public static void queue(Mesh mesh, Matrix4f transform, Vector4f color, Texture texture) {
		queue.add(new RenderState(mesh, transform, color, texture, (HashMap<String, Integer>) temp_integer_uniforms.clone()));
	}

}
