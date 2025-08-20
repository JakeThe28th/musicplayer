package musicplayer.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.MainProgram;
import musicplayer.audio.AudioSource;
import musicplayer.gui.G_PlaylistGridItem;
import musicplayer.utility.Log;

/** Stores the information for all loaded songs */
public class Library {
	
	public static final String library_directory = "library\\";
	
	private static HashMap<String, Playlist> playlists = new HashMap<String, Playlist>();

	private static HashMap<String, Album> albums = new HashMap<String, Album>();
	
	/** Load all of the songs/albums in [library_directory] */
	static {
		File library = new File(library_directory + "albums\\");
		for (File album : library.listFiles()) {
			try {
				registerAlbum(new Album(album));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void registerAlbum(Album album) {
		MainProgram.albums_grid.add(new G_PlaylistGridItem(album.linked_playlist));
		albums.put(album.getName(), album);
		if (playlists.get(album.getName()) != null) {
			throw new Error("Trying to add an album which has the same name as a playlist");
		} else {
			playlists.put(album.getName(), album.linked_playlist);
		}
	}
	
	public static void setSongInAlbum(String album_name, String identifier, Song song) {
		// Commented out since if the album is null something has gone wrong
		//if (albums.get(album_name) == null) albums.put(album_name, new Album());
		Album album = albums.get(album_name);
		album.put(identifier, song);
		
		getPlaylist(album_name).add(new UUID(album_name, identifier));
	}

	public static Song getSongFromAlbum(String album_name, String identifier) {
		if (albums.get(album_name) == null) return null;
		Album album = albums.get(album_name);
		return album.get(identifier);
	}
	
	public static Song getSongFromAlbum(UUID song) {
		return getSongFromAlbum(song.album, song.identifier);
	}
	
	public static Album getAlbum(String album_name) {
		if (albums.get(album_name) == null) return null;
		return albums.get(album_name);
	}
	
	public static Playlist getPlaylist(String playlist) {
		if (playlists.get(playlist) == null) return null;
		return playlists.get(playlist);
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

}
