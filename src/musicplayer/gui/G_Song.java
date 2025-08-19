package musicplayer.gui;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.RenderQueue;

public class G_Song extends G_Element {
	
	public G_Song(String n) {
		name = n;
	}
	
	String name;

	public void draw(int left, int top, int right, int bottom, int depth) {
		
		int text_width = GraphicsAPI.size(name).x;

		if (text_width > (right-left) && (GraphicsAPI.mouseY() > top && GraphicsAPI.mouseY() < bottom)) {
			RenderQueue.temp_integer_uniforms.put("first_fade_transparent_x", left);
			RenderQueue.temp_integer_uniforms.put("first_fade_opaque_x", left+30);
			RenderQueue.temp_integer_uniforms.put("second_fade_transparent_x", right);
			RenderQueue.temp_integer_uniforms.put("second_fade_opaque_x", right-30);
			text_width += 30;
			// scroll text
			int time = (int) Math.floorMod((System.currentTimeMillis() / 20), text_width);
			GraphicsAPI.text(left+time, top, depth, name);
			GraphicsAPI.text((left-text_width)+time, top, depth, name);
			RenderQueue.temp_integer_uniforms.put("first_fade_transparent_x", 0);
			RenderQueue.temp_integer_uniforms.put("first_fade_opaque_x", 0);
			RenderQueue.temp_integer_uniforms.put("second_fade_transparent_x", 0);
			RenderQueue.temp_integer_uniforms.put("second_fade_opaque_x", 0);
		} else {
			GraphicsAPI.text(left, top, depth, name);
		}
		
		
	}

	@Override
	public int height() {
		return GraphicsAPI.size(name).y;
	}

}
