package musicplayer.parts;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import musicplayer.graphics.Texture;

public class Playlist {
	
	boolean locked = false; // True for auto-generated album playlists
	
	Texture cover;
	String name;
	String identifier;

	public Playlist(Album album) {
		// TODO load playlist image and metadata and stuff from album
		locked = true;
		name = album.getName();
		identifier = album.getIdentifier();
		cover = album.cover();
	}
	
	public Playlist(File directory) {
		// TODO load playlist image and metadata and stuff from directory
	}

	ArrayList<UUID> songs = new ArrayList<UUID>();

	public ArrayList<Song> listSongs() {
		ArrayList<Song> set = new ArrayList<Song>();
		for (UUID identifier : songs) {
			set.add(Library.getAlbum(identifier.album).get(identifier.identifier));
		}
		return set;
	}

	public void add(UUID uuid) {
		songs.add(uuid);
	}

	public Texture cover() {
		return cover;
	}

	public String name() {
		return name;
	}

	public String identifier() {
		return identifier;
	}

}
