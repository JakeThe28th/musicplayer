package musicplayer.parts;

import java.io.File;
import java.util.LinkedHashSet;

public class Playlist {
	
	boolean locked = false; // True for auto-generated album playlists

	public Playlist(Album album) {
		// TODO load playlist image and metadata and stuff from album
		locked = true;
	}
	
	public Playlist(File directory) {
		// TODO load playlist image and metadata and stuff from directory
	}

	LinkedHashSet<UUID> songs = new LinkedHashSet<UUID>();

	public LinkedHashSet<Song> listSongs() {
		LinkedHashSet<Song> set = new LinkedHashSet<Song>();
		for (UUID identifier : songs) {
			set.add(Library.getAlbum(identifier.album).get(identifier.identifier));
		}
		return set;
	}

	public void add(UUID uuid) {
		songs.add(uuid);
	}

}
