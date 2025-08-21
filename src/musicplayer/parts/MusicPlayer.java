package musicplayer.parts;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.MainProgram;
import musicplayer.audio.AudioDevice;
import musicplayer.audio.AudioSource;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Song;
import musicplayer.utility.Log;

/** Handles most stuff regarding music playback */
public class MusicPlayer {
	
	public boolean paused = false;

	public static int scroll_mode = 1;
	public static final int SCROLL_PAGE = 0;
	public static final int SCROLL_SONG = 1;

	public static int playback_mode = 10;
	public static final int LOOP_NONE = 0; // Don't loop
	public static final int LOOP_SONG = 1; // Loop the song
	public static final int LOOP_LIST = 2; // Loop the playlist
	public static final int SHUFFLE   = 3; // Shuffle

	// Variables for the current playlist
	public static String playlist = "default";
	public static String view_playlist = "default";
	public static int song_index = 0;

	private static Song current_song;
	private static AudioSource current_song_audio;
	
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
		
		// Maybe bad for loading songs quickly, but also maybe
		// not wasting 1000s of megabytes is worth that...
		// Also, even without loading things in advance,
		// I haven't actually heard any noticeable delay from songs being loaded...
		if (current_song != null) current_song.free_audio();

		try {
			if (current_song_audio != null) current_song_audio.stop();
			current_song = Library.getSongFromAlbum(album, identifier);
			current_song_audio = current_song.audio();
			MainProgram.controls.current(new UUID(album, identifier));
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		
		scroll_to_current();
	}
	
	public static void scroll_to_current() {
		if (view_playlist.equals(playlist)) {
			G_Scrollable scroll = MainProgram.playlist_gui_scroll;
			int element_height = MainProgram.playlist_gui_list.element(0).height();
			int target = (element_height * song_index);

			if (target > scroll.scroll_y && scroll.sheight != 0) {
			if (target < scroll.scroll_y + (scroll.sheight-element_height)) {
				target = -1;
			} else {
				if (scroll_mode == SCROLL_SONG) {
					target = target - (scroll.sheight-(element_height+10));
				}
			}
			}
			MainProgram.playlist_gui_scroll.scroll_target = target;
		}
	}

	public static void current(UUID song) {
		current(song.album, song.identifier);
	}
	
	static boolean playing = false;
	
	public static void play() { current_song_audio.play(); MainProgram.controls.setPlaying(true); playing = true; }
	public static void pause() { current_song_audio.pause(); playing = false;  }
	public static void stop() { current_song_audio.stop(); current_song_audio.seekstop(0); playing = false; }
	public static void update() { 
		current_song_audio.update();
		if (current_song_audio.stopped() && playing) {
			if (playback_mode == LOOP_SONG) {
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
		if (song_index >= songs.size() && playback_mode == LOOP_LIST) {
			song_index = 0;
		} else if (playback_mode == SHUFFLE) {
			song_index = (int) (Math.random() * (songs.size()));
		} else if (playback_mode != LOOP_LIST) {
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
		if (current_song_audio != null) {
			return current_song_audio.currentTimeMillis();
		} else 
			return 0;
	}

	public static long songLength() {
		if (current_song_audio != null) {
			return current_song_audio.lengthMillis();
		} else 
			return 0;
	}

	public static boolean playing() {
		if (current_song_audio != null) {
			return current_song_audio.playing();
		} else 
			return false;
	}

	public static void seek(long time) {
		if (current_song_audio != null) {
			current_song_audio.seek(time);
			if (!playing) pause();
		}
	}
	
	public static short level(long time) {
		if (current_song_audio != null) {
			return current_song_audio.sample(current_song_audio.msToSamples(time));
		}
		return 0;
	}
	
	public static short level_offset(long time, short sample_offset) {
		if (current_song_audio != null) {
			if (sample_offset < 0) return 0;
			return current_song_audio.sample(current_song_audio.msToSamples(time) + sample_offset);
		}
		return 0;
	}

	public static void cyclePlaybackMode() {
		playback_mode++;
		if (playback_mode > 3) playback_mode = 0;
		
		MainProgram.controls.shuffle.icon_name = switch (playback_mode) {
			case LOOP_NONE -> "play_once";
			case LOOP_SONG -> "loop_once";		// TODO
			case LOOP_LIST -> "loop";		// TODO
			case SHUFFLE   -> "shuffle";
			default -> "";
		};
	}

	static float volume = 1;
	
	static AudioDevice device;

	public static void initAudioDevice() {
		// TODO: this is scuffed... i need to refactor audio
		device = new AudioDevice(AudioDevice.defaultDevice());
	}
	
	public static void endAudioDevice() {
		device.end();
	}
	
	public static void volume(float new_value) {
		device.setListenerVolume(new_value);
		volume = new_value;
	}
	
	public static float volume() {
		return volume;
	}
}
