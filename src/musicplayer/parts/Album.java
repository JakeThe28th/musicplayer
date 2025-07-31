package musicplayer.parts;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Album {
	
	HashMap<String, Song> songs = new HashMap<String, Song>();
	
	public Album(File album_directory) {
		// TODO load album image and metadata and stuff
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

}
