package musicplayer.gui;

import java.util.ArrayList;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.extensions.builtin.search.Search;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.extra.Popup;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_Song extends G_Element implements I_DraggableElement {

	public UUID song;
	public G_Text name = new G_Text();
	
	public boolean temporary_dragging_highlight = false;
	private boolean is_search_result = true;
	
	Playlist playlist;
	
	G_Icon menu = new G_Icon("hamburger")
		{ @Override public void onClick() {
			ArrayList<Option> option_arrays = new ArrayList<Option>();
				option_arrays.addAll(G_PlaylistScreen.song_menu_options);

				option_arrays.add(new Option("Add to", () -> {
					ArrayList<Option> others = new ArrayList<Option>();
						for (Playlist p : Library.listPlaylists()) {
							others.add(new Option(p.name(), () -> {
								MainProgram.showError("Not implemented yet");
								// TODO
								// rethink options stuff so this can be in G_PlaylistScreen
							}));
						}
					MainProgram.popups.add(new Popup(x + width(), y + height(), Alignment.RIGHT, Alignment.LEFT, Option.from(others)));
				}));
				
			Option[] options = Option.from(option_arrays);
			MainProgram.popups.add(new Popup(x + width(), y + height(), Alignment.RIGHT, Alignment.LEFT, options));
		} };
		
	G_Icon drag = new G_Icon("up_down_arrow")
		{ @Override public void onLeftMousePress() { MainProgram.pickup(G_Song.this ); } };
	
	G_List icons = new G_List(drag, menu);

	{
		menu.base_color = MainProgram.SEMIDARK_COLOR;
		menu.icon_size = 10;
		drag.base_color = MainProgram.SEMIDARK_COLOR;
		drag.icon_size = 10;
		icons.halign(Alignment.MIDDLE);
		addSubElement(icons);
		addSubElement(name);
		name.bottom_margin = 0;
		name.top_margin = 0;
		name.right_margin = 0;
		name.left_margin = 0;
		name.recalculate_size();
	}
	
	boolean being_dragged = false;
	
	public int calculate_target_index() {
		int y = GraphicsAPI.mouseY();
		y = y - G_PlaylistScreen.playlist_gui_list.top;
		int element_height = G_PlaylistScreen.playlist_gui_list.elements.get(0).height();
		int new_index = y / element_height;
		int length = G_PlaylistScreen.playlist_gui_list.elements.size();
		if (new_index > length) new_index = length;
		return new_index;
	}

	@Override
	public void drop() {

		playlist.remove(index);
		playlist.add(song, calculate_target_index());
		MusicPlayer.reload_view_playlist();
	}
	
	@Override public void while_dragging() { 
		int left = G_PlaylistScreen.playlist_gui_list.left;
		int right = left + G_PlaylistScreen.playlist_gui_list.width();
		int y = GraphicsAPI.mouseY();
		layout(left, y,right, y+height());
		GraphicsAPI.color(GraphicsAPI.BLACK75);
		GraphicsAPI.rect(left, y,right, y+height(), 99);
		draw(100);
		
		for (G_Element e : G_PlaylistScreen.playlist_gui_list.elements) {
			G_Song element = (G_Song) e;
			if (element.draw_index == calculate_target_index()) element.temporary_dragging_highlight = true;
			}
		}

	@Override
	public void pickup() {
		being_dragged = true;
		G_PlaylistScreen.playlist_gui_list.remove(this);
	}
	
	public G_Song(UUID song, int i, Playlist p) {
		this(song, i, p, false);
	}
	
	public G_Song(UUID song, int i, Playlist p, boolean is_search_result) {
		this.song = song;
		name.text(Library.getSongFromAlbum(song).name());
		playlist = p;
		this.is_search_result = is_search_result;
		if (is_search_result) icons.remove(drag);
		if (playlist == null) icons.remove(drag);
		if (playlist!= null) if (playlist.locked) icons.remove(drag);

		index = i;
	}
	
	@Override
	public void recalculate_size() {
		this.unpadded_height = name.height();
		this.unpadded_width = name.width() + icons.width() + number_width;
		icons.recalculate_size();
		name.recalculate_size();
	}
	
	Rectangle song_rectangle;
	int number_width = 40;
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		this.left = left + this.left_margin + number_width;
		this.number_x = left + this.left_margin;
		this.top = top + this.top_margin;
		this.right = right - this.right_margin;
		this.bottom = bottom - this.bottom_margin;
		this.song_rectangle = new Rectangle(left, top, this.right, bottom);

		this.right -= icons.width();
		this.hover_rectangle = new Rectangle(this.left-this.left_margin, top, this.right, bottom);

		
		visualizer_left = this.right;
		icons.layout(this.right, this.top, this.right+icons.width(), this.bottom);
		
		name.layout(this.left, this.top, this.right, this.bottom);
	}
	
	int left, top, right, bottom;
	int number_x;
	int index = 0;
	int visualizer_left;
	public int draw_index = 0;;
	
	@Override
	public void draw(int depth) {
		
		boolean this_is_the_current_song = index == MusicPlayer.song_index && MusicPlayer.view_playlist.equals(MusicPlayer.playlist);
		
		if (draw_index % 2 == 1 && !being_dragged) {
			GraphicsAPI.color(MainProgram.DARKER_COLOR);
			GraphicsAPI.rect(song_rectangle, depth);
		}
		
		if (temporary_dragging_highlight) {
			GraphicsAPI.color(GraphicsAPI.WHITE);
			GraphicsAPI.rect(
					song_rectangle.left(), 
					song_rectangle.top(), 
					song_rectangle.right(),
					song_rectangle.top() + 3,
					depth);
		}
		
		if (MusicPlayer.temp_highlight.containsKey(song)) {
			float highlight_amount = (MusicPlayer.temp_highlight.get(song) - System.currentTimeMillis()) / (float) MusicPlayer.HIGHLIGHT_DURATION;
			if (highlight_amount <= 0) MusicPlayer.temp_highlight.remove(song);
			
			GraphicsAPI.color(new Vector4f(1, 1, 0, highlight_amount));
			GraphicsAPI.rect(song_rectangle, depth);
		}
		
		if (is_search_result && Search.playlist_to_add_to != null) {
			if (Library.getPlaylist(Search.playlist_to_add_to).listSongs().contains(Library.getSongFromAlbum(song))) {
				GraphicsAPI.color(GraphicsAPI.TRANSPARENT_AQUA);
				GraphicsAPI.rect(song_rectangle, depth);
			}
		}
		
		GraphicsAPI.color(MainProgram.SEMIDARK_COLOR);
		if (this_is_the_current_song) {
			GraphicsAPI.color(base_color);
		}
		GraphicsAPI.text(number_x, top, depth, index + "");
		
		GraphicsAPI.color(base_color);
		
		int text_width = this.unpadded_width;
		
		// Loading bar
		if (MusicPlayer.hasLoadProgress(song)) {
			float progress = MusicPlayer.getLoadProgress(song);
			GraphicsAPI.color(GraphicsAPI.TRANSPARENT_AQUA);
			int x_fill = (int) ((right-left) * progress);
			GraphicsAPI.rect(left, top, left+x_fill, bottom, depth + 1);
		}
		
		
		// visualizer thing
		if (this_is_the_current_song && !this.is_search_result) {
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
		
		icons.draw(depth+1);
		
		temporary_dragging_highlight = false;
		
	}

	@Override
	public void onClick() {
		if (!is_search_result) {
			MusicPlayer.song_index = index;
			MusicPlayer.current(song);
			MusicPlayer.set_current_playlist(MusicPlayer.view_playlist);
			MusicPlayer.seek(0);
			MusicPlayer.play();
		} else {
			if (Search.playlist_to_add_to == null) MusicPlayer.go_to_song_source(song, playlist);
			if (Search.playlist_to_add_to != null) {
				Library.getPlaylist(Search.playlist_to_add_to).add(song);
			}
		}
	}
	
}
