package musicplayer;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import musicplayer.audio.AudioDevice;
import musicplayer.gui.G_ScrollableList;
import musicplayer.gui.G_Song;
import musicplayer.gui.GraphicsHandler;
import musicplayer.parts.Library;
import musicplayer.parts.Song;
import musicplayer.utility.Log;

public class SnowMusicClient {
	
	public static void main(String[] args) throws IOException, ParseException {
		new SnowMusicClient().run();
	}
	
	int current_view = 2;
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
	String playlist = "default";
	int song_index = 0;
	
	ArrayList<String> current_playlist_song_list = null;
	
	private void run() throws IOException, ParseException {
		
		// TODO: this is scuffed i need to refactor audio
		AudioDevice d = new AudioDevice(AudioDevice.defaultDevice()); 
		
		for (Song song : Library.listSongs()) {
			Log.send(song.uuid() + ", name=" + song.name());
		}
		
		set_current_playlist("defaultalbum");
		Library.current("defaultalbum", "awesomedefaultsong");
		//Library.play();
		
		GraphicsHandler.init();

		// Main loop
		while (GraphicsHandler.isOpen()) {
			//GraphicsHandler.clear();
			
			//Library.update();

			if (current_view == VIEW_MINIFIED) draw_minified_view();
			if (current_view == VIEW_PLAYLIST) draw_playlist_view();
			if (current_view == VIEW_LIBRARY) draw_playlist_view();
			
			GraphicsHandler.render();
			
			//GraphicsHandler.refresh();
		}
		
	}
	
	public void set_current_playlist(String name) {
		playlist = name;
		playlist_gui = new G_ScrollableList();
		for (Song song : Library.getPlaylist(playlist).listSongs()) {
			playlist_gui.add(new G_Song(song.name()));
		}
	}
	
	public void draw_library_view() {
		
		
	}
	
	G_ScrollableList playlist_gui;
	public void draw_playlist_view() {
		GraphicsHandler.text(10, 10, 0, playlist);
		
		int yy = 30;
		playlist_gui.draw(0, yy, GraphicsHandler.width(), GraphicsHandler.height(), 0);
	}
	
	public void draw_minified_view() {
		
	}
	
	

}
