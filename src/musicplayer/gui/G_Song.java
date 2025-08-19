package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.RenderQueue;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;
import musicplayer.parts.UUID;
import musicplayer.utility.Rectangle;

public class G_Song extends G_Element {
	
	@Override public ArrayList<G_Element> sub_elements() { return subelements; }
	ArrayList<G_Element> subelements = new ArrayList<G_Element>();

	UUID song;
	String name;
	String name_concat = "";
	
	G_Icon menu = new G_Icon("hamburger");
	
	{ 
		menu.base_color = MainProgram.SEMIDARK_COLOR;
		menu.halign(Alignment.MIDDLE);
		menu.icon_size = 10;
		subelements.add(menu);
	}
	
	public G_Song(UUID n) {
		song = n;
		name = Library.get(song).name();
	}
	
	@Override
	public void recalculate_size() {
		this.unpadded_height = GraphicsAPI.size(name).y;
		this.unpadded_width = GraphicsAPI.size(name).x;
		menu.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		this.left = left + this.left_margin + 30;
		this.number_x = left + this.left_margin;
		this.top = top + this.top_margin;
		this.right = right - this.right_margin;
		this.bottom = bottom - this.bottom_margin;
		this.hover_rectangle = new Rectangle(left, top, this.right, bottom);
		
		this.right -= 40;
		
		name_concat = name;
		while (GraphicsAPI.size(name_concat).x > (this.right-this.left)) {
			name_concat = name_concat.substring(0, name_concat.length()-1);
		}
		name_concat = name_concat.substring(0, name_concat.length()-3);
		name_concat += "...";
		
		menu.layout(this.right, this.top, this.right+40, this.bottom);
	}
	
	int left, top, right, bottom;
	int number_x;
	int index = 0;

	@Override
	public void draw(int depth) {
		
		if (index % 2 == 1) {
			GraphicsAPI.color(MainProgram.DARKER_COLOR);
			GraphicsAPI.rect(hover_rectangle, depth);
		}
		
		GraphicsAPI.color(MainProgram.SEMIDARK_COLOR);
		GraphicsAPI.text(number_x, top, depth, index + "");
		
		GraphicsAPI.color(base_color);
		
		int text_width = this.unpadded_width;

		
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
		} else if (text_width > (right-left) ) {
			GraphicsAPI.text(left, top, depth, name_concat);
		} else {
			GraphicsAPI.text(left, top, depth, name);
		}
		
		menu.draw(depth+1);
		
	}

	@Override
	public void onClick() {
		Library.current(song);
		Library.seek(0);
		Library.play();
	}

	public void index(int index) {
		this.index  = index;
	}
}
