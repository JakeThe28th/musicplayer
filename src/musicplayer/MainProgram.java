package musicplayer;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector4f;

import musicplayer.audio.AudioSource;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_SongControls;
import musicplayer.gui.extra.Popup;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Song;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

public class MainProgram {
	
	public static final String VERSION = "0.1a";
	public static final String PROGRAM_NAME = "NOW PLAYING";
	public static final String PROGRAM_TITLE = PROGRAM_NAME + " " + VERSION;
	
	public static final Vector4f ACCENT_COLOR = new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 1);
	public static final Vector4f LIGHT_COLOR = new Vector4f(63 / 255f, 89 / 255f, 84 / 255f, 1);
	public static final Vector4f DARK_COLOR = new Vector4f(24 / 255f, 55 / 255f, 49 / 255f, 1);
	public static final Vector4f DARKER_COLOR = new Vector4f(19 / 255f, 40 / 255f, 39 / 255f, 1);
	public static final Vector4f DARKEST_COLOR = new Vector4f(10 / 255f, 24 / 255f, 23 / 255f, 1);
	public static final Vector4f SEMIDARK_COLOR = new Vector4f(80 / 255f, 100 / 255f, 100 / 255f, 1);
	public static final Vector4f TRANSPARENT_ACCENT_COLOR = new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 0.25f);
	
	// Screens
	public static HashMap<String, Screen> screens = new HashMap<String, Screen>();
	static { 
		screens.put(G_HomeScreen.IDENTIFIER, G_HomeScreen.INSTANCE);
		screens.put(G_PlaylistScreen.IDENTIFIER, G_PlaylistScreen.INSTANCE);
	}

	public static G_PlaylistScreen 	playlist_screen 		= G_PlaylistScreen.INSTANCE;
	public static G_HomeScreen 		library_screen 			= G_HomeScreen.INSTANCE;
	
	public static long 				view_transition_time 	= 250;
	public static long 				view_transition_timer 	= 0;	
	public static String 			current_screen 			= G_HomeScreen.IDENTIFIER;
	public static String 			last_screen 			= current_screen;

	public static void change_screen(String view_identifier) {
		MainProgram.last_screen = MainProgram.current_screen;
		MainProgram.current_screen = view_identifier;
		MainProgram.view_transition_timer = System.currentTimeMillis();
	}
	
	static public void draw_screen(int xx, String screen_name) {
		G_Element screen = screens.get(screen_name).instance();
		screen.recalculate_size();
		screen.layout(xx, 0, GraphicsAPI.width() + xx, GraphicsAPI.height()-controls.height());
		screen.draw(0);
		screen.input();
	}
	
	// 
	
	public static G_SongControls controls = new G_SongControls();
	
	public static ArrayList<Popup> popups = new ArrayList<Popup>();
	
	public boolean pinned = false;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException {
		
		GraphicsAPI.init();
		MusicPlayer.initAudioDevice();
		
		ExtensionAPI.init();
		
		for (Song song : Library.listSongs()) {
			Log.send(song.uuid() + ", name=" + song.name());
		}
		
		//MusicPlayer.set_current_playlist("defaultalbum");
		//MusicPlayer.set_current_view_playlist("defaultalbum");
		//MusicPlayer.current("defaultalbum", "awesomedefaultsong");
		
		MusicPlayer.cyclePlaybackMode();
		
		//Library.play();
		
//		MusicPlayer.set_current_playlist("awesome-other-album");
//		MusicPlayer.current("awesome-other-album", "wowow");
		
		// Main loop
		while (GraphicsAPI.isOpen()) {
			//GraphicsHandler.clear();
			
			MusicPlayer.update();

			long transition_time = System.currentTimeMillis() - view_transition_timer;
			if (transition_time < view_transition_time) {
				// Get offset for sliding screen transition
				float transition_amount = transition_time / (float) view_transition_time;
			      transition_amount = (float) Utility.lerp(transition_amount, 1, transition_amount);
			      int xoffset = (int) (GraphicsAPI.width() * (transition_amount));
			    // ... //
				draw_screen(xoffset, last_screen);
				draw_screen(xoffset-GraphicsAPI.width(), current_screen);
			} else {
				draw_screen(0, current_screen);
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

}
