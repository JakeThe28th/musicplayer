package musicplayer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector4f;

import musicplayer.components.ComponentAPI;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.KeybindAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_SongControls;
import musicplayer.gui.I_DraggableElement;
import musicplayer.gui.extra.Popup;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.parts.Album;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
import musicplayer.utility.Utility;
import static org.lwjgl.glfw.GLFW.*;

public class MainProgram {
	
	public static final boolean SHOW_FPS = false;
	
	public static final String VERSION = "0.3.4a";
	public static final String PROGRAM_NAME = "NOW PLAYING";
	public static final String PROGRAM_TITLE = PROGRAM_NAME + " " + VERSION;
	
	// Error print
	record ErrorMessage(String msg, long time) {}
	static ArrayList<ErrorMessage> errors = new ArrayList<>();
	
	public static void showError(String string) {
		errors.add(new ErrorMessage(string, System.currentTimeMillis()));
	}

	// Screens
	public static HashMap<String, Screen> screens = new HashMap<String, Screen>();
	static { 
		screens.put(G_HomeScreen.IDENTIFIER, G_HomeScreen.INSTANCE);
		screens.put(G_PlaylistScreen.IDENTIFIER, G_PlaylistScreen.INSTANCE);
	}
	
	public static void registerScreen(Screen screen) {
		screens.put(screen.identifier(), screen);
	}

	public static G_PlaylistScreen 	playlist_screen 		= G_PlaylistScreen.INSTANCE;
	public static G_HomeScreen 		library_screen 			= G_HomeScreen.INSTANCE;
	
	public static long 				view_transition_time 	= 250;
	public static long 				view_transition_timer 	= 0;	
	public static String 			current_screen 			= G_HomeScreen.IDENTIFIER;
	public static String 			last_screen 			= current_screen;

	public static ArrayDeque<String> screen_stack = new ArrayDeque<>();
	
	public static void change_screen(String view_identifier) {
		screen_stack.push(current_screen);
		transition_to_screen(view_identifier);
	}
	
	public static void previous_screen() {
		transition_to_screen(screen_stack.pop());
	}
	
	private static void transition_to_screen(String view_identifier) {
		MainProgram.last_screen = MainProgram.current_screen;
		MainProgram.current_screen = view_identifier;
		MainProgram.view_transition_timer = System.currentTimeMillis();
	}
	
	static public void draw_screen(int xx, String screen_name) {
		G_Element screen = screens.get(screen_name).instance();
		screen.recalculate_size();
		screen.layout(xx, 0, GraphicsAPI.width() + xx, GraphicsAPI.height()-controls.height());
		screen.draw(0);
		if (input) screen.input();
	}
	

	public static void recalculateScreenSize(String screen_name) {
		G_Element screen = screens.get(screen_name).instance();
		screen.recalculate_size();
		screen.layout(0, 0, GraphicsAPI.width(), GraphicsAPI.height()-controls.height());
	}
	
	// 
	
	public static G_SongControls controls = new G_SongControls();
	
	public static ArrayList<Popup> popups = new ArrayList<Popup>();
	
	public boolean pinned = false;
	
	public static boolean input = true;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException {
		
		GraphicsAPI.init();
		GraphicsAPI.title(PROGRAM_TITLE);
		
		GraphicsAPI.center_text(0, 0, "Initializing Audio...");
		GraphicsAPI.render();
		MusicPlayer.initAudioDevice();
		
		GraphicsAPI.center_text(0, 0, "Initializing Components...");
		GraphicsAPI.render();
		ComponentAPI.init();
		
		GraphicsAPI.center_text(0, 0, "Initializing Extensions...");
		GraphicsAPI.render();
		ExtensionAPI.init();

		GraphicsAPI.center_text(0, 0, "Loading songs...");
		GraphicsAPI.render();
		Library.init();
		
		KeybindAPI.bindkey("play/pause", GLFW_KEY_SPACE, GLFW_RELEASE, () -> { MusicPlayer.toggleplay(); } ); 
		KeybindAPI.bindkey("previous", GLFW_KEY_LEFT, GLFW_RELEASE, () -> { MusicPlayer.previous(); } ); 
		KeybindAPI.bindkey("next", GLFW_KEY_RIGHT, GLFW_RELEASE, () -> { MusicPlayer.next(); } ); 

		try {
		
		MusicPlayer.setPlaybackMode(MusicPlayer.LOOP_LIST);
		
		// Main loop
		while (GraphicsAPI.isOpen()) {
			
			KeybindAPI.tick();
			ComponentAPI.tick();
			ExtensionAPI.tick();
			
			input = true;
			
			//GraphicsHandler.clear();
			
			MusicPlayer.update();
			
			if (held_element != null) { input = false; }
			
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
			
			if (held_element != null) {
				held_element.while_dragging();
				if (GraphicsAPI.left_click_released()) {
					held_element.drop();
					held_element = null;
				}
			}
			
			controls.recalculate_size();
			controls.layout(0, GraphicsAPI.height() - controls.height(), GraphicsAPI.width(), GraphicsAPI.height());
			controls.draw(0);
			if (input) controls.input();
			
			int i = 10;
			boolean should_close_popups = true;
			for (Popup p : (ArrayList<Popup>) popups.clone()) {
				i++;
				boolean close = p.draw(i);
				should_close_popups = should_close_popups && close;
			}
			if (should_close_popups) popups.clear();
			
			// Error messages
			int yy = 10; int error_height = 30; long time = 5000;
			for (ErrorMessage e : (ArrayList<ErrorMessage>) errors.clone()) {
				long t = System.currentTimeMillis() - e.time;
				GraphicsAPI.color(GraphicsAPI.BLACK);
				GraphicsAPI.rect(10, yy, GraphicsAPI.width()-10, yy+error_height, 200);
				GraphicsAPI.color(GraphicsAPI.TRANSPARENT_WHITE);
				GraphicsAPI.rect(10, yy, (int) (10 + ((GraphicsAPI.width()-20) * (t/(float)time))), yy+error_height, 200);
				GraphicsAPI.color(GraphicsAPI.WHITE);
				GraphicsAPI.text(15, yy+5, 220, e.msg());
				yy += error_height + 5;
				if (t > time) errors.remove(e);
			}
			
			if (SHOW_FPS) draw_FPS();
			
			GraphicsAPI.render();
						
		}
		
		} finally {
			
			GraphicsAPI.center_text(10, 0, "Saving playlists");
			GraphicsAPI.render();
			for (Playlist p : Library.listPlaylists()) { p.save(); }
			
			GraphicsAPI.center_text(10, 0, "Saving albums");
			GraphicsAPI.render();
			for (Album a : Library.listAlbums()) { a.linked_playlist.save(); }

			GraphicsAPI.center_text(10, 0, "Closing extensions");
			GraphicsAPI.render();
			ExtensionAPI.end();
			
			GraphicsAPI.center_text(10, 0, "Closing components");
			GraphicsAPI.render();
			ComponentAPI.end();
			
			GraphicsAPI.center_text(10, 0, "Closing audio device");
			GraphicsAPI.render();
			MusicPlayer.endAudioDevice();
		}

	}
	
	// Dragging stuff
	static I_DraggableElement held_element = null;
	
	public static boolean pickup(I_DraggableElement candidate) {
		if (held_element == null) {
			held_element = candidate;
			held_element.pickup();
			return true;
		} else return false;
	}

	// Frame rate 
	static long last_frame_time = 0;
	static int frames_counted = 0;
	static long frame_time_cumulative = 0;
	static long last_reset = Long.MIN_VALUE;
	private static void draw_FPS() {
		if (SHOW_FPS) {
			
			if (last_reset + 5000 < System.currentTimeMillis()) {
				last_reset = System.currentTimeMillis();
				frames_counted = 0;
				frame_time_cumulative = 0;
			}
			
			long frame_time = System.currentTimeMillis() - last_frame_time;
			last_frame_time = System.currentTimeMillis();
			
			frame_time_cumulative += frame_time;
			frames_counted ++;
			
			GraphicsAPI.color(GraphicsAPI.BLACK75);
			GraphicsAPI.rect(5, 5, 200, (35) * 3, 999);
			GraphicsAPI.color(GraphicsAPI.WHITE);
			NumberFormat f = DecimalFormat.getInstance();
			f.setMinimumIntegerDigits(3);
			f.setMinimumFractionDigits(3);
			float t = (frame_time_cumulative /  (float) frames_counted);
			GraphicsAPI.text(10, 10, 1000, "    mspt: " 	+ f.format(t));
			GraphicsAPI.text(10, 40, 1000, "raw mspt: " 	+ f.format(frame_time));
			GraphicsAPI.text(10, 70, 1000, "     fps: " 	+ f.format(1000 / t));
		}
	}

	public static boolean isCurrentScreen(String identifier) {
		return identifier.equals(current_screen);
	}

}
