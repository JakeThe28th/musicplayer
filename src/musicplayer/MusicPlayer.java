package musicplayer;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import musicplayer.audio.AudioDevice;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_List;
import musicplayer.gui.G_ScrollableList;
import musicplayer.gui.G_Song;
import musicplayer.gui.G_SongControls;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;
import musicplayer.parts.Song;
import musicplayer.utility.Log;

public class MusicPlayer {
	
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
	static String playlist = "default";
	static int song_index = 0;
	
	static ArrayList<String> current_playlist_song_list = null;

	static public void set_current_playlist(String name) {
		playlist = name;
		playlist_gui = new G_List().verticalify().scrollable(true);
		for (Song song : Library.getPlaylist(playlist).listSongs()) {
			playlist_gui.add(new G_Song(song.name()));
			playlist_gui.add(new G_Song(song.name())); // TEMP (TODO) 
			playlist_gui.add(new G_Song(song.name())); // TEMP (TODO) 
			playlist_gui.add(new G_Song(song.name())); // TEMP (TODO) 
		}
	}
	
	static public void draw_library_view() {
		
		
	}
	
	static G_List playlist_gui;
	static public void draw_playlist_view() {
		GraphicsAPI.color(0.15f, 0.25f, 0.5f, 1);
		GraphicsAPI.rect(0, 0, GraphicsAPI.width(), 30, 0);
		GraphicsAPI.color(1, 0.75f, 0.75f, 1);
		GraphicsAPI.text(10, 10, 2, playlist);
		GraphicsAPI.color(1, 1, 1, 1);
		int yy = 35;
		playlist_gui.recalculate_size();
		playlist_gui.layout(0, yy, GraphicsAPI.width(), GraphicsAPI.height()-controls.height());
		playlist_gui.draw(0);

		GraphicsAPI.text(GraphicsAPI.mouseX(), GraphicsAPI.mouseY(), 0, "Hello!");
	}
	
	static public void draw_minified_view() {
		
	}
	
	

}
