package musicplayer.parts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import musicplayer.graphics.Texture;

public class Playlist {
	
	boolean is_album = false; // True for auto-generated album playlists
	
	Texture cover;
	String name;
	String identifier;

	public Playlist(File playlist_directory) throws FileNotFoundException, IOException {
		this(playlist_directory, null);
	}

	
	public Playlist(File playlist_directory, Album album) throws FileNotFoundException, IOException {
		
		identifier = playlist_directory.getName();
		name = playlist_directory.getName();

		// Check if this playlist is also an album
		is_album = album != null; //Library.getAlbum(identifier) != null;
		
		
		// Get playlist cover
		
		File cover = new File(playlist_directory.getPath() + "/cover.png");
		if (!cover.exists()) { cover = new File(playlist_directory.getPath() + "/cover.jpg"); }	
		
		if (cover.exists()) {
			this.cover = new Texture(cover.getPath());
		} else {
			this.cover = new Texture(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, 1, 1);
		}
		
		
		// Add songs
		
		File song_order = new File(playlist_directory.getPath() + "/song_order.txt");
		// If the song order exists, just add the songs listed there to the playlist
		if (song_order.exists()) {
			for (String line : Files.readString(song_order.toPath()).split("\n")) {
				line = line.strip();
				UUID uuid = new UUID(line.split(":")[0], line.split(":")[1]);
				this.add(uuid);
			}
		} else if (is_album) {
			// Otherwise, if this is an album playlist, use an arbitrary ordering
			for (String song_name : album.songs.keySet()) {
				this.add(album.songs.get(song_name).uuid());
			}
		}
		
	}
	
	/** For creating playlists from scratch ONLY */
	public Playlist(String identifier, Album album) throws FileNotFoundException, IOException {
		this.name = identifier;
		this.identifier = identifier;
		is_album = album != null;
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
	
	
	public void save() throws IOException {
		// Create a folder for this playlist if it doesn't already exist
		String directory = (is_album ? Library.album_directory :  Library.playlist_directory);
		File playlist_folder = new File(directory + identifier + "\\");
		playlist_folder.mkdirs();
		
		// Save album image
		if (cover != null) cover.save(playlist_folder.toString() + "\\cover.png");
		
		// Save song list
		String order = "";
		for (UUID song : songs) {
			order += song.album + ":" + song.identifier + "\n";
		}
		Files.writeString(Paths.get(playlist_folder.toString() + "\\song_order.txt"), order);
	}


	public void cover(BufferedImage image) {
		if (this.cover != null) this.cover.free();
		this.cover = new Texture(image);
	}
	
}
