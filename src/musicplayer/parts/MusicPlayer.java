package musicplayer.parts;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.MainProgram;
import musicplayer.audio.AudioSource;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Song;
import musicplayer.utility.Log;

/** Handles most stuff regarding music playback */
public class MusicPlayer {
	
	public boolean shuffle = false;
	public boolean paused = false;
	
	public static int loop_mode = 2;
	public static final int LOOP_NONE = 0; // Don't loop
	public static final int LOOP_SONG = 1; // 
	public static final int LOOP_LIST = 2; // Loop the playlist
	
	// Variables for the current playlist
	public static String playlist = "default";
	public static String view_playlist = "default";
	public static int song_index = 0;

	private static AudioSource current_song;
	
	static public void set_current_playlist(String name) {
		playlist = name;
	}
	
	static public void set_current_view_playlist(String name) {
		view_playlist = name;
		MainProgram.playlist_header.set_playlist(name);
		MainProgram.set_playlist_list(new G_List().verticalify());
		for (Song song : Library.getPlaylist(view_playlist).listSongs()) {
			MainProgram.playlist_gui_list.add(new G_Song(song.uuid()));
		}
	}
	
	public static void current(String album, String identifier) {
		try {
			if (current_song != null) current_song.stop();
			current_song = Library.getSongFromAlbum(album, identifier).audio();
			MainProgram.controls.current(new UUID(album, identifier));
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public static void current(UUID song) {
		current(song.album, song.identifier);
	}
	
	static boolean playing = false;
	
	public static void play() { current_song.play(); MainProgram.controls.setPlaying(true); playing = true; }
	public static void pause() { current_song.pause(); playing = false;  }
	public static void stop() { current_song.stop(); current_song.seekstop(0); playing = false; }
	public static void update() { 
		current_song.update();
		if (current_song.stopped() && playing) {
			if (loop_mode == LOOP_SONG) {
				seek(0);
				play();
			} else {
				next();
			}
		}
	}
	
	public static void next() { 
		song_index++;
		ArrayList<Song> songs = Library.getPlaylist(playlist).listSongs();
		if (song_index >= songs.size() && loop_mode == LOOP_LIST) {
			song_index = 0;
		} else if (loop_mode != LOOP_LIST) {
			song_index --;
			return;
		}
		
		Song next_song = songs.get(song_index);
		current(next_song.uuid());
		seek(0);
		if (playing) play();
		
	}
	
	public static void previous() { 
		song_index--;
		if (song_index < 0) {
			song_index = 0;
		}
		Song next_song = Library.getPlaylist(playlist).listSongs().get(song_index);
		current(next_song.uuid());
		seek(0);
		if (playing) play();
	}

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
		if (current_song != null) {
			current_song.seek(time);
			if (!playing) pause();
		}
	}
	
	public static short level(long time) {
		if (current_song != null) {
			return current_song.sample(current_song.msToSamples(time));
		}
		return 0;
	}
	
	public static short level_offset(long time, short sample_offset) {
		if (current_song != null) {
			if (sample_offset < 0) return 0;
			return current_song.sample(current_song.msToSamples(time) + sample_offset);
		}
		return 0;
	}
}
