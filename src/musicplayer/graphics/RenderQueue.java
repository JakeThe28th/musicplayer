package musicplayer.graphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL40;

import musicplayer.components.settings.Settings;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

class RenderQueue {
		
	public static HashMap<String, Integer> temp_integer_uniforms = new HashMap<String, Integer>();
	public static Rectangle current_scissor = null;
	
	private static record RenderState(Mesh mesh, Matrix4f transform, Vector4f color, Texture texture, HashMap<String, Integer> integer_uniforms, Rectangle scissor) { }
	
	static ArrayList<RenderState> queue = new ArrayList<RenderState>();
	
	public static boolean delete_queue_after_rendering = true;
	
	// i think final booleans get culled out by the compiler so they don't get checked unnecessarily
	// right
	public static final boolean EXPERIMENTAL_OPTIMIZATIONS = Settings.getBoolean("enable_experimental_optimizations_requires_restart");
	public static Mesh last_mesh = null;
	public static Texture last_texture = null;
	public static Rectangle last_scissor = null;
	
	public static void render() {
		Graphics.clear();
		for (RenderState state : queue) {
			
			if (EXPERIMENTAL_OPTIMIZATIONS) {
				if (last_mesh != state.mesh) {
					state.mesh.bind();
					last_mesh = state.mesh;
				}
			} else {
				state.mesh.bind();
			}
			
			Shader.uniform("transform", state.transform);
			Shader.uniform("mix_color", state.color);

			for (String uniform : state.integer_uniforms.keySet()) {
				Shader.uniform(uniform, state.integer_uniforms.get(uniform));
			}
			
			if (EXPERIMENTAL_OPTIMIZATIONS) {
				if (last_texture != state.texture) {
					if (state.texture != null) {
						glBindTexture(GL_TEXTURE_2D, state.texture.texture);
					}
					last_texture = state.texture;
				}
			} else {
				if (state.texture != null) {
					glBindTexture(GL_TEXTURE_2D, state.texture.texture);
				}
			}
			
			if (EXPERIMENTAL_OPTIMIZATIONS) {
				if (state.scissor() != null) {
					if (!state.scissor.equals(last_scissor)) {
					GL40.glScissor(
							state.scissor().left(), 
							Window.window_height - state.scissor().bottom(), 
							(state.scissor().right()-state.scissor().left()), 
							(state.scissor().bottom()-state.scissor().top()));
					GL40.glEnable(GL40.GL_SCISSOR_TEST);
					}
				} else {
					if (last_scissor != null) {
					GL40.glScissor(0, 0, Window.window_width, Window.window_height);
					GL40.glDisable(GL40.GL_SCISSOR_TEST);
					}
				}
				last_scissor = state.scissor;
			} else {
				if (state.scissor() != null) {
					GL40.glScissor(
							state.scissor().left(), 
							Window.window_height - state.scissor().bottom(), 
							(state.scissor().right()-state.scissor().left()), 
							(state.scissor().bottom()-state.scissor().top()));
					GL40.glEnable(GL40.GL_SCISSOR_TEST);
				} else {
					GL40.glScissor(0, 0, Window.window_width, Window.window_height);
					GL40.glDisable(GL40.GL_SCISSOR_TEST);

				}
			}
			

			GL40.glDrawElements(GL_TRIANGLES, state.mesh.count(), GL_UNSIGNED_INT, 0);
		}
		temp_integer_uniforms.clear();
		if (delete_queue_after_rendering) queue.clear();
		Window.tick();
	}
	
	@SuppressWarnings("unchecked")
	public static void queue(Mesh mesh, Matrix4f transform, Vector4f color, Texture texture) {
		queue.add(new RenderState(mesh, transform, color, texture, (HashMap<String, Integer>) temp_integer_uniforms.clone(), current_scissor));
	}

}
