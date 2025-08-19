package musicplayer.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.MusicPlayer;
import musicplayer.audio.AudioSource;

/** Stores the information for all loaded songs */
public class Library {
	
	public static final String library_directory = "library\\";
	
	private static HashMap<String, Playlist> playlists = new HashMap<String, Playlist>();

	private static HashMap<String, Album> albums = new HashMap<String, Album>();
	
	/** Load all of the songs/albums in [library_directory] */
	static {
		File library = new File(library_directory + "albums\\");
		for (File album : library.listFiles()) {
			albums.put(album.getName(), new Album(album));
			playlists.put(album.getName(), new Playlist(getAlbum(album.getName()))); // Every album should get a playlist
			for (File song : album.listFiles()) {
				if (song.isDirectory()) loadSong(album.getName(), song.getName(), song);
			}
		}
	}
	
	public static void set(String album_name, String identifier, Song song) {
		// Commented out since if the album is null something has gone wrong
		//if (albums.get(album_name) == null) albums.put(album_name, new Album());
		Album album = albums.get(album_name);
		album.put(identifier, song);
		
		getPlaylist(album_name).add(new UUID(album_name, identifier));
	}

	public static Song get(String album_name, String identifier) {
		if (albums.get(album_name) == null) return null;
		Album album = albums.get(album_name);
		return album.get(identifier);
	}
	
	public static Song get(UUID song) {
		return get(song.album, song.identifier);
	}
	
	public static Album getAlbum(String album_name) {
		if (albums.get(album_name) == null) return null;
		return albums.get(album_name);
	}
	
	public static Playlist getPlaylist(String playlist) {
		if (playlists.get(playlist) == null) return null;
		return playlists.get(playlist);
	}

	public static void loadSong(String album, String identifier, File directory) {
		try { 
			set(album, identifier, new Song(directory));
			get(album, identifier).uuid(album, identifier);
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public static Album[] listAlbums() {
		Album[] albums_return = new Album[albums.size()];
		
		Object[] set = albums.keySet().toArray();
		for (int i = 0; i < set.length; i++) {
			albums_return[i] = albums.get(set[i]);
		}
		return albums_return;
		
	}
	
	/** Returns every song. */
	public static LinkedHashSet<Song> listSongs() {
		LinkedHashSet<Song> set = new LinkedHashSet<Song>();
		for (Album album : listAlbums()) {
			for (Song song : album.listSongs()) {
				set.add(song);
			}
		}
		return set;
	}
	
	
	/* -- ++ Actually playing songs and stuff ++ -- */

	private static AudioSource current_song;
	
	public static void current(String album, String identifier) {
		try {
			current_song = get(album, identifier).audio();
			MusicPlayer.controls.current(new UUID(album, identifier));
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public static void play() { current_song.play(); }
	public static void pause() { current_song.pause(); }
	public static void stop() { current_song.stop(); }
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
