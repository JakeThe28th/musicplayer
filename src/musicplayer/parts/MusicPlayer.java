package musicplayer.parts;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.MainProgram;
import musicplayer.audio.AudioSource;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Song;

/** Handles most stuff regarding music playback */
public class MusicPlayer {
	
	public boolean shuffle = false;
	public boolean paused = false;
	
	public int loop = 0;
	public static final int LOOP_NONE = 0; // Don't loop
	public static final int LOOP_SONG = 1; // 
	public static final int LOOP_LIST = 2; // Loop the playlist
	
	// Variables for the current playlist
	public static String playlist = "default";
	public static int song_index = 0;

	private static AudioSource current_song;
	
	static public void set_current_playlist(String name) {
		MainProgram.playlist_header.set_playlist(name);
		playlist = name;
		MainProgram.playlist_gui = new G_List().verticalify().scrollable(true);
		for (Song song : Library.getPlaylist(playlist).listSongs()) {
			MainProgram.playlist_gui.add(new G_Song(song.uuid()));
			MainProgram.playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			MainProgram.playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			MainProgram.playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			MainProgram.playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
			MainProgram.playlist_gui.add(new G_Song(song.uuid())); // TEMP (TODO) 
		}
	}
	
	public static void current(String album, String identifier) {
		try {
			if (current_song != null) current_song.stop();
			current_song = Library.get(album, identifier).audio();
			MainProgram.controls.current(new UUID(album, identifier));
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public static void current(UUID song) {
		current(song.album, song.identifier);
	}
	
	public static void play() { current_song.play(); MainProgram.controls.setPlaying(true); }
	public static void pause() { current_song.pause(); }
	public static void stop() { current_song.stop(); current_song.seekstop(0); }
	public static void update() { current_song.update(); }

	public static long songTime() {
		if (current_song != null) {
			return current_song.currentTimeMillis();
		} else 
			return 0;
	}

	public static long songLength() {
		if (current_song != null) {
			return current_song.lengthMillis();
		} else 
			return 0;
	}

	public static boolean playing() {
		if (current_song != null) {
			return current_song.playing();
		} else 
			return false;
	}

	public static void seek(long time) {
		boolean playing = playing();
		if (current_song != null) {
			current_song.seek(time);
			if (!playing) pause();
		}
	}
	
}
