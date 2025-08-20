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
	
	public String getName() {
		return identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	// Every album has a matching playlist.
	// You should never actually view "albums" in the UI directly,
	// only playlists matching those albums.
	public Playlist linked_playlist;
	
	Texture cover;
	
	HashMap<String, Song> songs = new HashMap<String, Song>();
	
	public Album(File album_directory) throws IOException {
		// TODO load album image and metadata and stuff
		
		identifier = album_directory.getName();

		for (File song_directory : album_directory.listFiles()) {
			if (song_directory.isDirectory()) {
				Song song = new Song(song_directory);
				song.uuid(identifier, song_directory.getName());
				put(song.uuid().identifier, song);
			}
		}
		
		File cover = new File(album_directory.getPath() + "/cover.png");
		if (!cover.exists()) { cover = new File(album_directory.getPath() + "/cover.jpg"); }	
		
		if (cover.exists()) {
			this.cover = new Texture(cover.getPath());
		} else {
			this.cover = new Texture(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, 1, 1);
		}
		
		linked_playlist = new Playlist(this);
		
		File song_order = new File(album_directory.getPath() + "/song_order.txt");
		// If the song order exists, just add the songs listed there to the playlist
		if (song_order.exists()) {
			for (String line : Files.readString(song_order.toPath()).split("\n")) {
				line = line.strip();
				UUID uuid = new UUID(line.split(":")[0], line.split(":")[1]);
				linked_playlist.add(uuid);
			}
		} else {
		// Otherwise, use an arbitrary ordering
			for (String song_name : songs.keySet()) {
				linked_playlist.add(songs.get(song_name).uuid());
			}
		}

	}

	public Album put(String idendifier, Song song) {
		songs.put(idendifier, song);
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

	public Texture cover() {
		return cover;
	}

}
