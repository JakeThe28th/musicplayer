package musicplayer.parts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import musicplayer.MainProgram;
import musicplayer.graphics.Texture;
import musicplayer.utility.Utility;

public class Playlist {
	
	boolean is_album = false; // True for auto-generated album playlists
	
	BufferedImage cover_raw;
	BufferedImage cover;
	Texture glcover;
	String name;
	String identifier;

	private HashMap<String, String> fields = new HashMap<String, String>();

	public Playlist(File playlist_directory) throws FileNotFoundException, IOException {
		this(playlist_directory, null);
	}

	
	public Playlist(File playlist_directory, Album album) throws FileNotFoundException, IOException {
		
		identifier = playlist_directory.getName();
		name = playlist_directory.getName();
		
		this.fields = Utility.readKeyValue(Paths.get(playlist_directory.getPath() + "\\info.txt"));
		if (fields.containsKey("name")) name = fields.get("name");
		
		// Check if this playlist is also an album
		is_album = album != null; //Library.getAlbum(identifier) != null;
		locked = is_album;
		
		// Get playlist cover
		
		File cover = Utility.getIfExists(playlist_directory.getPath() + "/cover", ".png", ".jpg");
		
		if (cover != null) {
			if (Utility.getIfExists(playlist_directory.getPath() + "/cover_raw", ".png", ".jpg") == null) {
				this.cover_raw = ImageIO.read(cover);
			}

			cover(ImageIO.read(cover));
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
		locked = is_album;
	}


	ArrayList<UUID> songs = new ArrayList<UUID>();

	public boolean locked;

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

	public Texture glcover() {
		if (glcover == null) {
			if (cover == null) {
				glcover = new Texture(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, 1, 1);
				return glcover;
			}
			glcover = new Texture(cover);
		}
		return glcover;
	}

	public String name() {
		return name;
	}
	
	public void name(String n) {
		name = n;
		fields.put("name", name);
	}
	
	public String metadata(String key) {
		return fields.get(key);
	}
	
	public void metadata(String key, String value) {
		fields.put(key, value);
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
		if (cover != null) ImageIO.write(cover, "png", new File(playlist_folder.toString() + "\\cover.png"));
		if (cover_raw != null) ImageIO.write(cover_raw, "png", new File(playlist_folder.toString() + "\\cover_raw.png"));

		// Save metadata
		Utility.writeKeyValue(Paths.get(playlist_folder.toString() + "\\info.txt"), fields);
		
		// Save song list
		String order = "";
		for (UUID song : songs) {
			order += song.album + ":" + song.identifier + "\n";
		}
		Files.writeString(Paths.get(playlist_folder.toString() + "\\song_order.txt"), order);
	}
	
	public void trysave() {
		try {
			save();
		} catch (IOException e) {
			MainProgram.showError("Failed to save playlist/or/album to disk");
			e.printStackTrace();
		}
	}

	public void cover(BufferedImage image) {
		this.cover = image;
		
		if (image.getWidth() != image.getHeight()) {
			int ww = image.getWidth();
			int hh =image.getHeight();
			
			int larger_axis = (ww < hh) ? hh : ww;
			
			BufferedImage square = new BufferedImage(larger_axis, larger_axis, BufferedImage.TYPE_INT_ARGB);
			Graphics g = square.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, larger_axis, larger_axis);
			int left = 0;
			if (hh > ww) left = (larger_axis/2) - (ww/2);
			int top = 0;
			if (hh < ww) top = (larger_axis/2) - (hh/2);
			int right = left+ww;
			int bottom = top+hh;
			g.drawImage(image, left, top, right, bottom, 0, 0, ww, hh, null);
			g.dispose();
			this.cover = square;
		}
		
		this.glcover = null;
	}


	public void remove(int index) {
		songs.remove(index);
	}

	public void add(UUID song, int new_index) {
		songs.add(new_index, song);
	}
	
}
