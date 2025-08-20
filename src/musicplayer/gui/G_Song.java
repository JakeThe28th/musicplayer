package musicplayer.gui;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.UUID;
import musicplayer.utility.Rectangle;

public class G_Song extends G_Element {

	UUID song;
	G_Text name = new G_Text();
	
	G_Icon menu = new G_Icon("hamburger");
	
	{ 
		menu.base_color = MainProgram.SEMIDARK_COLOR;
		menu.halign(Alignment.MIDDLE);
		menu.icon_size = 10;
		addSubElement(menu);
		addSubElement(name);
		name.bottom_margin = 0;
		name.top_margin = 0;
		name.right_margin = 0;
		name.left_margin = 0;
		name.recalculate_size();
	}
	
	public G_Song(UUID n) {
		song = n;
		name.text(Library.getSongFromAlbum(song).name());
	}
	
	@Override
	public void recalculate_size() {
		this.unpadded_height = name.height();
		this.unpadded_width = name.width();
		menu.recalculate_size();
		name.recalculate_size();
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
		
		visualizer_left = this.right;
		menu.layout(this.right, this.top, this.right+30, this.bottom);
		
		name.layout(this.left, this.top, this.right, this.bottom);
	}
	
	int left, top, right, bottom;
	int number_x;
	int index = 0;
	int visualizer_left;
	
	@Override
	public void draw(int depth) {
		
		boolean this_is_the_current_song = index == MusicPlayer.song_index && MusicPlayer.view_playlist.equals(MusicPlayer.playlist);
		
		if (index % 2 == 1) {
			GraphicsAPI.color(MainProgram.DARKER_COLOR);
			GraphicsAPI.rect(hover_rectangle, depth);
		}
		
		GraphicsAPI.color(MainProgram.SEMIDARK_COLOR);
		if (this_is_the_current_song) {
			GraphicsAPI.color(base_color);
		}
		GraphicsAPI.text(number_x, top, depth, index + "");
		
		GraphicsAPI.color(base_color);
		
		int text_width = this.unpadded_width;
		
		// visualizer thing
		if (this_is_the_current_song) {
			GraphicsAPI.color(MainProgram.TRANSPARENT_ACCENT_COLOR);
			int target_width 		= right - left;
			int slice_w 	 		= 2;
			int slices = target_width/slice_w;
			int offset_per_pixel	= 1;
			for (int i = 0; i < slices; i++) {
				float hh = MusicPlayer.level_offset(MusicPlayer.songTime(), (short) ((slice_w*i) * offset_per_pixel)) / ((float) Short.MAX_VALUE / 32f);
				//hh = hh/hh;
				hh += (bottom-top)/2;
				GraphicsAPI.rect(left+(slice_w*i), bottom-((int) hh), left+(slice_w*(i+1)), bottom, depth + 1);
			}
			GraphicsAPI.color(MainProgram.ACCENT_COLOR);
		}

		name.draw(depth+2);
		
		menu.draw(depth+1);
		
	}

	@Override
	public void onClick() {
		MusicPlayer.current(song);
		MusicPlayer.set_current_playlist(MusicPlayer.view_playlist);
		MusicPlayer.seek(0);
		MusicPlayer.play();
		MusicPlayer.song_index = index;
	}

	public void index(int index) {
		this.index  = index;
	}
}
