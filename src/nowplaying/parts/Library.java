package nowplaying.parts;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import frost3d.utility.Log;
import nowplaying.gui.UI;
import nowplaying.gui.screens.HomeScreen;
import nowplaying.gui.screens.PlaylistScreen;
import nowplaying.parts.data.Album;
import nowplaying.parts.data.Playlist;
import nowplaying.parts.data.Song;
import nowplaying.parts.data.UUID;
import nowplaying.utility.Utility;

/** Stores the information for all loaded songs */
public class Library {
	
	public static final String library_directory 	= "library\\";
	public static final String album_directory 		= library_directory + "albums\\";
	public static final String playlist_directory 	= library_directory + "playlists\\";
	
	public static final String album_group_order_location = library_directory + "album_group_order.txt";
	public static final String playlist_group_order_location = library_directory + "playlist_group_order.txt";

	private static HashMap<String, Playlist> playlists = new HashMap<String, Playlist>();

	private static HashMap<String, Album> albums = new HashMap<String, Album>();
	
	public static void save_group_order() {
		Utility.writeArray(album_group_order_location, album_group_order);
		Utility.writeArray(playlist_group_order_location, playlist_group_order);
	}
	
	public static void add_album_group(String group_name) {
		Library.album_group_order = Utility.append(Library.album_group_order, group_name);
	}

	public static void add_playlist_group(String group_name) {
		Library.playlist_group_order = Utility.append(Library.playlist_group_order, group_name);
	}

	public static String[] album_group_order = null;
	public static String[] playlist_group_order = null;
	public static boolean is_initialized = false;

	/** Load all of the songs/albums in [library_directory] */
	public static void init() {
		for (File album : new File(album_directory).listFiles()) {
			try {
				registerAlbum(new Album(album));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (File playlist : new File(playlist_directory).listFiles()) {
			try {
				registerPlaylist(new Playlist(playlist));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		album_group_order = Utility.readStringDefaulted(album_group_order_location, HomeScreen.FAVORITES_GROUP + "\nDefault").split("\n");
		playlist_group_order = Utility.readStringDefaulted(playlist_group_order_location, HomeScreen.FAVORITES_GROUP + "\nDefault").split("\n");
		
		is_initialized = true;
		HomeScreen.instance().reload_groups();
	}
	
	public static void registerAlbum(Album album) {
		
		if (albums.get(album.getIdentifier()) != null) {
			throw new Error("Trying to add an album that already exists: " + album.getIdentifier());
		}
		
		albums.put(album.getIdentifier(), album);
		if (playlists.get(album.getIdentifier()) != null) {
			throw new Error("Trying to add an album which has the same name as a playlist");
		} else {
			playlists.put(album.getIdentifier(), album.linked_playlist);
		}
		if (is_initialized) HomeScreen.instance().reload_groups();
	}
	
	public static void registerPlaylist(Playlist playlist) {
		if (playlists.get(playlist.identifier()) != null) {
			throw new Error("Trying to add an album that already exists");
		}
		
		playlists.put(playlist.identifier(), playlist);
		if (is_initialized) HomeScreen.instance().reload_groups();
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

	public static Playlist[] listPlaylists() {
		Playlist[] playlists_return = new Playlist[playlists.size()];
		
		Object[] set = playlists.keySet().toArray();
		for (int i = 0; i < set.length; i++) {
			playlists_return[i] = playlists.get(set[i]);
		}
		return playlists_return;
	}
	
	public static void print() {
		for (Song song : Library.listSongs()) {
			Log.send(song.uuid() + ", name=" + song.name());
		}
	}

	
	
	public static void deletePlaylist(String identifier) {
		Playlist playlist = getPlaylist(identifier);
		playlists.remove(identifier);
		File dir = new File(playlist_directory + identifier);
		Utility.delete(dir);
		Log.send("Deleted " + playlist.name());
		UI.showError("Deleted " + playlist.name());
		HomeScreen.instance().reload_groups();
	}

	public static void deleteAlbum(String identifier) {
		Album album = getAlbum(identifier);
		playlists.remove(identifier);
		albums.remove(identifier);
		
		PlaylistScreen.clear();
		UI.set_current_screen(HomeScreen.instance());
		
		File dir = new File(album_directory + identifier);
		Utility.delete(dir);
		
		Log.send("Deleted " + album.linked_playlist.name());
		UI.showError("Deleted " + album.linked_playlist.name());
		HomeScreen.instance().reload_groups();
	}
	
}
