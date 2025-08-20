package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.RenderQueue;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;
import musicplayer.utility.Utility;

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
		
		this.right -= 30;
		
		name_concat = name;
		while (GraphicsAPI.size(name_concat).x > (this.right-this.left) && name_concat.length() >= 4) {
			name_concat = name_concat.substring(0, name_concat.length()-1);
		}
		name_concat = name_concat.substring(0, name_concat.length()-3);
		name_concat += "...";
		
		visualizer_left = this.right;
		menu.layout(this.right, this.top, this.right+30, this.bottom);
	}
	
	int left, top, right, bottom;
	int number_x;
	int index = 0;
	int visualizer_left;
	float visualizer_average_fill = 0;
	
	@Override
	public void draw(int depth) {
		
		if (index % 2 == 1) {
			GraphicsAPI.color(MainProgram.DARKER_COLOR);
			GraphicsAPI.rect(hover_rectangle, depth);
		}
		
		GraphicsAPI.color(MainProgram.SEMIDARK_COLOR);
		if (index == MusicPlayer.song_index) {
			GraphicsAPI.color(base_color);
		}
		GraphicsAPI.text(number_x, top, depth, index + "");
		
		GraphicsAPI.color(base_color);
		
		int text_width = this.unpadded_width;
		
		// visualizer thing
		if (index == MusicPlayer.song_index) {
			GraphicsAPI.color(MainProgram.TRANSPARENT_ACCENT_COLOR);
			int target_width 		= right - left;
			int slice_w 	 		= 2;
			int slices = target_width/slice_w;
			int offset_per_pixel	= 1;
			
			// averaging
			int combined_levels 	= 0; 
			int level_count			= 0;
			
			int visualizer_height 	= height() / 4;
			
			for (int i = 0; i < slices; i++) {
				short level = MusicPlayer.level_offset(MusicPlayer.songTime(), (short) ((slice_w*i) * offset_per_pixel));
				combined_levels += Math.abs(level);
				level_count++;
				int hh = (int) ((level / visualizer_average_fill) * visualizer_height);
				Log.send("' " + (level / visualizer_average_fill) + ", " + level);
				//hh = hh/hh;
				//hh += (bottom-top)/2;
				int hhoffset = (bottom-top)/2;;
				GraphicsAPI.rect(
						 left   			+(slice_w * (i+0) ), 
						(bottom -hhoffset)	- hh, 
						 left   			+(slice_w * (i+1) ), 
						 bottom -hhoffset, 
						depth + 1);
			}
			Log.send((combined_levels / (float) level_count), combined_levels, level_count);
			visualizer_average_fill = (float) Utility.lerp(visualizer_average_fill, (combined_levels / (float) level_count), 0.25);
			GraphicsAPI.color(MainProgram.ACCENT_COLOR);
		}

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
		MusicPlayer.current(song);
		MusicPlayer.seek(0);
		MusicPlayer.play();
		MusicPlayer.song_index = index;
	}

	public void index(int index) {
		this.index  = index;
	}
}
