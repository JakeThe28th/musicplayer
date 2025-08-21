package musicplayer.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashSet;

import musicplayer.graphics.Texture;
import musicplayer.utility.Log;

public class Album {
	
	String identifier;
	public String getIdentifier() { return identifier; }
	
	// Every album has a matching playlist.
	// You should never actually view "albums" in the UI directly,
	// only playlists matching those albums.
	public Playlist linked_playlist;
		
	HashMap<String, Song> songs = new HashMap<String, Song>();
	
	public Album(File album_directory) throws IOException {
		
		identifier = album_directory.getName();

		for (File song_directory : album_directory.listFiles()) {
			if (song_directory.isDirectory()) {
				Song song = new Song(song_directory, new UUID(identifier, song_directory.getName()));
				songs.put(song.uuid().identifier, song);
			}
		}
		
		linked_playlist = new Playlist(album_directory, this);

	}
	
	/** For creating albums from scratch ONLY */
	public Album(String identifier) throws IOException {
		this.identifier = identifier;
		linked_playlist = new Playlist(identifier, this);
	}

	/** Sets a song in this album. Also sets it on disk. 
	 * @throws IOException */
	public Album set(Song song) throws IOException {
		if (!songs.containsKey(song.uuid().identifier)) linked_playlist.add(song.uuid());
		songs.put(song.uuid().identifier, song);
		song.save();
		return this;
	}
	
	public Song get(String idendifier) {
		return songs.get(idendifier);
	}
	
	public LinkedHashSet<Song> listSongs() {
		LinkedHashSet<Song> set = new LinkedHashSet<Song>();
		for (String identifier : songs.keySet()) {
			set.add(songs.get(identifier));
		}
		return set;
	}
	
	/** Saves this album (all of its' songs) to disk. */
	public void save() throws IOException {
		
		// Save metadata
		linked_playlist.save();
		
		// Save songs
		for (String s : songs.keySet()) {
			songs.get(s).save();
		}
		
	}

}
