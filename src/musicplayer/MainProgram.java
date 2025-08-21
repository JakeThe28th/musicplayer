package musicplayer;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.joml.Vector4f;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Grid;
import musicplayer.gui.G_List;
import musicplayer.gui.G_PlaylistHeader;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_SongControls;
import musicplayer.gui.extra.Popup;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Song;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

public class MainProgram {
	
	public static final Vector4f ACCENT_COLOR = new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 1);
	public static final Vector4f LIGHT_COLOR = new Vector4f(63 / 255f, 89 / 255f, 84 / 255f, 1);
	public static final Vector4f DARK_COLOR = new Vector4f(24 / 255f, 55 / 255f, 49 / 255f, 1);
	public static final Vector4f DARKER_COLOR = new Vector4f(19 / 255f, 40 / 255f, 39 / 255f, 1);
	public static final Vector4f DARKEST_COLOR = new Vector4f(10 / 255f, 24 / 255f, 23 / 255f, 1);
	public static final Vector4f SEMIDARK_COLOR = new Vector4f(80 / 255f, 100 / 255f, 100 / 255f, 1);
	public static final Vector4f TRANSPARENT_ACCENT_COLOR = new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 0.25f);

	public static ArrayList<Popup> popups = new ArrayList<Popup>();
	
	public static G_SongControls controls = new G_SongControls();
	
	public static ArrayList<Option> playlist_menu_options = new ArrayList<Option>();
	
	public static G_PlaylistScreen playlist_screen = G_PlaylistScreen.instance;
	public static G_HomeScreen library_screen = G_HomeScreen.instance;

	static {
		playlist_menu_options.add(new Option("View Library", () -> {
			change_view(MainProgram.VIEW_LIBRARY);
		}));
		playlist_menu_options.add(new Option("Other Test Button (play)", () -> {
			MusicPlayer.play();
		}));
		playlist_menu_options.add(new Option("Other Test Button (pause)", () -> {
			MusicPlayer.pause();
		}));
	}
	
	// ^^^ GUI Objects ^^^ //
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException {
		
		GraphicsAPI.init();
		MusicPlayer.initAudioDevice();
		
		for (Song song : Library.listSongs()) {
			Log.send(song.uuid() + ", name=" + song.name());
		}
		
		MusicPlayer.set_current_playlist("defaultalbum");
		MusicPlayer.set_current_view_playlist("defaultalbum");
		MusicPlayer.current("defaultalbum", "awesomedefaultsong");
		MusicPlayer.cyclePlaybackMode();
		//Library.play();
		
//		MusicPlayer.set_current_playlist("awesome-other-album");
//		MusicPlayer.current("awesome-other-album", "wowow");
		
		// Main loop
		while (GraphicsAPI.isOpen()) {
			//GraphicsHandler.clear();
			
			MusicPlayer.update();

			long transition_time = System.currentTimeMillis() - view_transition_timer;
			float transition_amount = transition_time / (float) view_transition_time;
			      transition_amount = (float) Utility.lerp(transition_amount, 1, transition_amount);
			int xoffset = (int) (GraphicsAPI.width() * (transition_amount));
			if (transition_time < view_transition_time) {
				draw_view(last_view, xoffset);
				draw_view(current_view, xoffset-GraphicsAPI.width());
			} else {
				draw_view(current_view, 0);
			}
			
			controls.recalculate_size();
			controls.layout(0, GraphicsAPI.height() - controls.height(), GraphicsAPI.width(), GraphicsAPI.height());
			controls.draw(0);
			controls.input();
			
			int i = 10;
			boolean should_close_popups = true;
			for (Popup p : (ArrayList<Popup>) popups.clone()) {
				i++;
				boolean close = p.draw(i);
				should_close_popups = should_close_popups && close;
			}
			if (should_close_popups) popups.clear();
			
			GraphicsAPI.render();
						
			//GraphicsHandler.refresh();
		}
		
		MusicPlayer.endAudioDevice();
	}
	
	public static void change_view(int new_view) {
		MainProgram.last_view = MainProgram.current_view;
		MainProgram.current_view = new_view;
		MainProgram.view_transition_timer = System.currentTimeMillis();
	}
		
	private static void draw_view(int view, int offset) {
		if (view == VIEW_MINIFIED) draw_minified_view();
		if (view == VIEW_PLAYLIST) draw_screen(offset, playlist_screen);
		if (view == VIEW_LIBRARY) draw_screen(offset, library_screen);
	}

	public static int current_view = 2;
	public static final int VIEW_MINIFIED = 1; // The condensed music player view
	public static final int VIEW_PLAYLIST = 2; // Viewing the current list of songs
	public static final int VIEW_LIBRARY  = 3; // Viewing the list of albums & playlists

	public boolean pinned = false;
	
	static long view_transition_time = 250;
	public static long view_transition_timer = 0;
	public static int last_view = 2;
	
	

	static public void draw_screen(int xx, G_Element screen) {		
		screen.recalculate_size();
		screen.layout(xx, 0, GraphicsAPI.width() + xx, GraphicsAPI.height()-controls.height());
		screen.draw(0);
		screen.input();
	}
	
	static public void draw_minified_view() {
		
	}
	
	

}
