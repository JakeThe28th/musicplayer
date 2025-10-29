package musicplayer.parts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Stack;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.MainProgram;
import musicplayer.audio.AudioDevice;
import musicplayer.audio.AudioSource;
import musicplayer.components.settings.Settings;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Song;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

/** Handles most stuff regarding music playback */
public class MusicPlayer {
	
	public static AudioSource EMPTY;
	static boolean restart_song = false;
	static boolean reload_playlist_gui = false;

	private static HashMap<UUID, Float> load_progress = new HashMap<UUID, Float>();
	
	synchronized public static void setLoadProgress(UUID song, float value) {
		load_progress.put(song, value);
	}
	
	synchronized public static float getLoadProgress(UUID song) {
		return load_progress.get(song);
	}
	
	synchronized public static boolean hasLoadProgress(UUID song) {
		return load_progress.containsKey(song);
	}
	
	synchronized public static void finishLoading(UUID song) {
		load_progress.remove(song);
		reload_playlist_gui = true;
		if(song.equals(current_song.uuid())) restart_song = true;
	}
	
	//
	
	private static LinkedHashSet<UUID> broken_songs = new LinkedHashSet<>();

	public static void markAsBroken(UUID song) {
		broken_songs.add(song);
	}
	
	public static boolean isBroken(UUID song) {
		return broken_songs.contains(song);
	}
	
	//
	
	static record PreviousSongData(String playlist, int index) {}
	static Stack<PreviousSongData> previous_songs = new Stack<>();
	
	//
	
	public static final long HIGHLIGHT_DURATION = 4000;
	public static HashMap<UUID, Long> temp_highlight = new HashMap<UUID, Long>();
		
	public static int scroll_mode = 1;
	public static final int SCROLL_PAGE = 0;
	public static final int SCROLL_SONG = 1;

	public static int playback_mode = 10;
	public static final int LOOP_NONE = 0; // Don't loop
	public static final int LOOP_SONG = 1; // Loop the song
	public static final int LOOP_LIST = 2; // Loop the playlist
	public static final int SHUFFLE   = 3; // Shuffle

	// Variables for the current playlist
	static final String NO_PLAYLIST = "No playlist";
	public static String playlist = NO_PLAYLIST;
	public static String view_playlist = NO_PLAYLIST;
	public static int song_index = 0;

	private static Song current_song;
	private static AudioSource current_song_audio;
	
	static public void set_current_playlist(String name) {
		playlist = name;
	}
	
	static public void set_current_view_playlist(String name) {
		if (name == null) {
			playlist = NO_PLAYLIST;
			view_playlist = NO_PLAYLIST;
			return;
		}
		view_playlist = name;
		G_PlaylistScreen.playlist_header.set_playlist(name);
		double scroll_y = G_PlaylistScreen.playlist_gui_scroll.scroll_y;
		double scroll_target = G_PlaylistScreen.playlist_gui_scroll.scroll_target();
		G_PlaylistScreen.set_playlist_list(new G_List().verticalify());
		G_PlaylistScreen.playlist_gui_list.force_no_recalculate_size = true;
		int index = 0;
		for (Song song : Library.getPlaylist(view_playlist).listSongs()) {
			G_Song song_element = new G_Song(song.uuid(), index, Library.getPlaylist(view_playlist));
			G_PlaylistScreen.playlist_gui_list.add(song_element);
			ExtensionAPI.modifyGUI(song_element);
			index++;
		}
		G_PlaylistScreen.playlist_gui_list.force_no_recalculate_size = false;
		G_PlaylistScreen.playlist_gui_list.recalculate_size();
		G_PlaylistScreen.playlist_gui_scroll.scroll_y = scroll_y;
		G_PlaylistScreen.playlist_gui_scroll.scroll_target(scroll_target);

		MainProgram.recalculateScreenSize(G_PlaylistScreen.IDENTIFIER);
	}
	
	public static void reload_view_playlist() {
		MusicPlayer.set_current_view_playlist(MusicPlayer.view_playlist);
	}
	
	public static Song current_song() {
		return current_song;
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
			if (current_song_audio == null) {
				current_song_audio = EMPTY;
				stop();
			}
			MainProgram.controls.current(new UUID(album, identifier));
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		
		scroll_to_current();
		
		GraphicsAPI.title(current_song.name() + " (" + Library.getAlbum(current_song.uuid().album).linked_playlist.name + ") - " + MainProgram.PROGRAM_TITLE);

	}
	
	public static void currentFromPlaylist(int index, Playlist playlist) {
		
		previous_songs.push(new PreviousSongData(MusicPlayer.playlist, song_index));

		MusicPlayer.song_index = index;
		MusicPlayer.current(playlist.listSongs().get(index).uuid());
		//MusicPlayer.set_current_playlist(MusicPlayer.view_playlist);
		MusicPlayer.set_current_playlist(playlist.identifier());
		MusicPlayer.seek(0);
		MusicPlayer.play();
	}
	
	public static void go_to_song_source(UUID song, Playlist playlist) {
		if (playlist == null) playlist = Library.getAlbum(song.album).linked_playlist;
		
		MusicPlayer.set_current_view_playlist(playlist.identifier);
		MainProgram.change_screen(MainProgram.playlist_screen.identifier());
		scroll_to_index(playlist.songs.indexOf(song));
		temp_highlight.put(song, System.currentTimeMillis() + HIGHLIGHT_DURATION);
	}
	
	public static void scroll_to_current() {
		if (view_playlist.equals(playlist)) {
			scroll_to_index(song_index);
		}
	}
	
	public static void scroll_to_index(int index) {
		G_Scrollable scroll 	= G_PlaylistScreen.playlist_gui_scroll;
		int element_height 		= G_PlaylistScreen.playlist_gui_list.element(0).height();

		double page_height		= (scroll.sheight-(element_height*1.25));
		double screen_top 		= scroll.scroll_y;
		double screen_bottom	= scroll.scroll_y + page_height;
		double target 			= (element_height * index);

		if (target > screen_top) {
			if 		(target < screen_bottom) 		{ target = -1; } 
			else if (scroll_mode == SCROLL_SONG) 	{ target = target - page_height; }
		}
		
		if (target > 1) G_PlaylistScreen.playlist_gui_scroll.scroll_target(target);
	}

	public static void current(UUID song) {
		current(song.album, song.identifier);
	}
	
	static boolean playing = false;
	
	public static void play() { 
		playing = true; 
		MainProgram.controls.setPlaying(true); 
		if (current_song == null) return;
		if (hasLoadProgress(current_song.uuid())) return;
		current_song_audio.play();
		}
	
	public static void pause() { pause(true); }
	
	public static void pause(boolean update_controls) {
		playing = false;  
		if (update_controls) MainProgram.controls.setPlaying(false); 
		if (current_song == null) return;
		if (hasLoadProgress(current_song.uuid())) return;
		current_song_audio.pause(); 
		}
	
	public static void stop() { 
		playing = false; 
		MainProgram.controls.setPlaying(false); 
		if (current_song == null) return;
		if (hasLoadProgress(current_song.uuid())) return;
		current_song_audio.stop(); 
		current_song_audio.seekstop(0); 
		}
	
	public static void update() { 
		
//		for (PreviousSongData d : previous_songs) {
//			Log.send(d.index + ", " + d.playlist); // debug
//		}
		
		if (current_song != null && isBroken(current_song.uuid()) && Settings.skip_broken_songs()) {
			next();
		}
		
		if (reload_playlist_gui) {
			reload_playlist_gui = false;
			set_current_view_playlist(view_playlist); // reload playlist gui
		}
		if (restart_song) {
			restart_song = false;
			current(current_song.uuid());
			seek(0);
			if (playing) {
				play();
			} else {
				stop();
			}
		}
		if (current_song_audio != null) {
			if (current_song_audio == EMPTY|| hasLoadProgress(current_song.uuid())) return;
			current_song_audio.update();
			if (current_song_audio.stopped() && playing) {
				if (playback_mode == LOOP_SONG) {
					seek(0);
					play();
				} else {
					next(true);
				}
			}
		}
	}
	
	public static void next() { next(false); }
	public static void next(boolean auto) { 	
		if (playlist.equals(NO_PLAYLIST)) return;
		if (Library.getPlaylist(playlist) == null) return;
		
		// using a local variable since currentFromPlaylist needs to read the
		// old song index to add it to the previous songs list
		int song_index = MusicPlayer.song_index;
		
		song_index++;
		ArrayList<Song> songs = Library.getPlaylist(playlist).listSongs();
		if (playback_mode == SHUFFLE) {
			song_index = (int) (Math.random() * (songs.size()));
		} else if (playback_mode != LOOP_LIST && auto) {
			song_index --;
			return;
		}
		
		if (song_index >= songs.size()) {
			song_index = 0;
		}
		
		currentFromPlaylist(song_index, Library.getPlaylist(playlist));
		
//		Song next_song = songs.get(song_index);
//		current(next_song.uuid());
//		seek(0);
//		if (playing) play();
	}
	
	public static void previous() { 
		
		// Seek threshold
		if (current_song_audio != null) {
			int threshold = Settings.previous_song_buffer_threshold();
			int time = Utility.MStoSeconds(current_song_audio.currentTimeMillis());
			if (threshold != 0 && time > threshold ) {
				seek(0);
				if (playing) play();
				return;
			}
		}
		
		if (previous_songs.isEmpty()) return;
		
		PreviousSongData last_song = previous_songs.pop();
		String last_playlist = last_song.playlist;
		
		if (last_playlist.equals(NO_PLAYLIST)) return;
		
		currentFromPlaylist(last_song.index, Library.getPlaylist(last_playlist));
		previous_songs.pop(); // currentFromPlaylist adds to the stack, so remove immediately
		
//		if (playlist.equals(NO_PLAYLIST)) return;
//		
//		song_index--;
//		if (song_index < 0) {
//			song_index = 0;
//		}
//		Song next_song = Library.getPlaylist(playlist).listSongs().get(song_index);
//		current(next_song.uuid());
//		seek(0);
//		if (playing) play();
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
		return playing;
//		if (current_song_audio != nu/ll) {
//			return current_song_audio.playing();
//		} else 
//			return false;
	}

	public static void seek(long time) {
		if (hasLoadProgress(current_song.uuid())) return;
		if (current_song_audio != null) {
			current_song_audio.seek(time);
			if (!playing) pause(false);
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
		setPlaybackMode(playback_mode);
	}
	
	public static void setPlaybackMode(int mode) {
		playback_mode = mode;
		if (playback_mode > 3) playback_mode = 0;
		
		MainProgram.controls.shuffle.icon_name = switch (playback_mode) {
			case LOOP_NONE -> "play_once";
			case LOOP_SONG -> "loop_once";		// TODO
			case LOOP_LIST -> "loop";		// TODO
			case SHUFFLE   -> "shuffle";
			default -> "";
		};
	}

	static float volume = Settings.volume();
	
	static AudioDevice device;

	public static void initAudioDevice() {
		// TODO: this is scuffed... i need to refactor audio
		device = new AudioDevice(AudioDevice.defaultDevice());
		MusicPlayer.EMPTY = new AudioSource();
		
		volume(volume);
	}
	
	public static void endAudioDevice() {
		device.end();
	}
	
	public static void volume(float new_value) {
		if (Settings.use_logarithmic_volume()) {
			//Log.send("prelog " + new_value);
			new_value = (float) (Math.log10((-new_value*0.9)+1) * -1);
			//Log.send(new_value);
		}
		
		device.setListenerVolume(new_value);
		volume = new_value;
	}
	
	public static float volume() {
		return volume;
	}

	public static Playlist current_view_playlist() { return Library.getPlaylist(view_playlist); }

	public static void toggleplay() {
		if (playing()) pause(); else play();
	}

	public static String getTimecodeString() {
		if (current_song_audio != null) {
			long time_ms = current_song_audio.currentTimeMillis();
			
			double time_seconds = time_ms / 1000;
			double time_minutes = time_seconds / 60;
			
			int seconds = (int) (time_seconds % 60);
			int minutes = (int) (time_minutes % 60);
			int hours = (int) (time_minutes / 60);
			
			if (hours > 0) return hours + ":" + minutes + ":" + String.format("%02d", seconds);
			return minutes + ":" + String.format("%02d", seconds);
		}
		return "No Song";
	}

	public static String current_song_name() {
		if (current_song == null) return "No song";
		return current_song.name();
	}

}
