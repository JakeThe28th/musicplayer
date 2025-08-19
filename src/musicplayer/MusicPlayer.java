package musicplayer;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joml.Vector4f;

import musicplayer.audio.AudioDevice;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_List;
import musicplayer.gui.G_PlaylistHeader;
import musicplayer.gui.G_ScrollableList;
import musicplayer.gui.G_Song;
import musicplayer.gui.G_SongControls;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;
import musicplayer.parts.Song;
import musicplayer.utility.Log;

public class MusicPlayer {
	
	public static final Vector4f ACCENT_COLOR = new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 1);
	public static final Vector4f LIGHT_COLOR = new Vector4f(63 / 255f, 89 / 255f, 84 / 255f, 1);
	public static final Vector4f DARK_COLOR = new Vector4f(24 / 255f, 55 / 255f, 49 / 255f, 1);
	public static final Vector4f DARKER_COLOR = new Vector4f(19 / 255f, 40 / 255f, 39 / 255f, 1);
	public static final Vector4f DARKEST_COLOR = new Vector4f(10 / 255f, 24 / 255f, 23 / 255f, 1);
	public static final Vector4f SEMIDARK_COLOR = new Vector4f(80 / 255f, 100 / 255f, 100 / 255f, 1);

	
	public static G_SongControls controls = new G_SongControls();
	
	// ^^^ GUI Objects ^^^ //
	
	public static void main(String[] args) throws IOException, ParseException {
		
		// TODO: this is scuffed i need to refactor audio
		AudioDevice d = new AudioDevice(AudioDevice.defaultDevice()); 
		
		for (Song song : Library.listSongs()) {
			Log.send(song.uuid() + ", name=" + song.name());
		}
		
		set_current_playlist("defaultalbum");
		Library.current("defaultalbum", "awesomedefaultsong");
		//Library.play();
		
		GraphicsAPI.init();
		
		// Main loop
		while (GraphicsAPI.isOpen()) {
			//GraphicsHandler.clear();
			
			Library.update();

			if (current_view == VIEW_MINIFIED) draw_minified_view();
			if (current_view == VIEW_PLAYLIST) draw_playlist_view();
			if (current_view == VIEW_LIBRARY) draw_playlist_view();
			
			controls.recalculate_size();
			controls.layout(0, GraphicsAPI.height() - controls.height(), GraphicsAPI.width(), GraphicsAPI.height());
			controls.draw(0);
			controls.input();

			GraphicsAPI.render();
						
			//GraphicsHandler.refresh();
		}
		
		d.end();
	}
		
	static int current_view = 2;
	public static final int VIEW_MINIFIED = 1; // The condensed music player view
	public static final int VIEW_PLAYLIST = 2; // Viewing the current list of songs
	public static final int VIEW_LIBRARY  = 3; // Viewing the list of albums & playlists

	public boolean shuffle = false;
	public boolean paused = false;
	
	public int loop = 0;
	public static final int LOOP_NONE = 0; // Don't loop
	public static final int LOOP_SONG = 1; // 
	public static final int LOOP_LIST = 2; // Loop the playlist

	public boolean pinned = false;
	
	// Variables for the current playlist
	public static String playlist = "default";
	public static int song_index = 0;
	
	static ArrayList<String> current_playlist_song_list = null;

	static public void set_current_playlist(String name) {
		playlist_header.set_playlist(name);
		playlist = name;
		playlist_gui = new G_List().verticalify().scrollable(true);
		for (Song song : Library.getPlaylist(playlist).listSongs()) {
			playlist_gui.add(new G_Song(song.uuid()));
			playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
		}
	}
	
	static public void draw_library_view() {
		
		
	}
	
	static G_List playlist_gui;
	static G_PlaylistHeader playlist_header = new G_PlaylistHeader();
	static public void draw_playlist_view() {		
		playlist_header.recalculate_size();
		playlist_header.layout(0, 0, GraphicsAPI.width(), playlist_header.height());
		playlist_header.draw(0);
		playlist_header.input();
		
		
		playlist_gui.recalculate_size();
		playlist_gui.layout(0, playlist_header.height(), GraphicsAPI.width(), GraphicsAPI.height()-controls.height());
		playlist_gui.draw(0);
		playlist_gui.input();
		
		//GraphicsAPI.text(GraphicsAPI.mouseX(), GraphicsAPI.mouseY(), 0, "Hello!");
	}
	
	static public void draw_minified_view() {
		
	}
	
	

}
