package musicplayer.extensions.builtin.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.extensions.Extension;
import musicplayer.extensions.builtin.search.SearchRecords.*;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Song;
import musicplayer.gui.G_WrappedList;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
import musicplayer.parts.Song;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class Search extends Extension  {
	
	static ArrayList<SearchTerm> search_query = new ArrayList<SearchTerm>();
	
	public static String playlist_to_add_to = null;

	public static void setSearchQuery(String query) {
		
		GraphicsAPI.input_string(query);
		
		search_query.clear();

		String[] parts = query.split(" ");
		for (String part : parts) {
			for (SearchTermType type : SearchTermType.values()) {
				if (part.startsWith(type.match_string)) {
					String remainder = part.replaceFirst(Pattern.quote(type.match_string), "");
					search_query.add(new SearchTerm(type, remainder));
					break;
				}
			}
		}
		
		G_WrappedList GUI_terms = new G_WrappedList();
		for (SearchTerm term : search_query) {
			GUI_terms.add(new G_SearchTerm(term));
		}
		
		G_SearchScreen.INSTANCE.terms(GUI_terms);
	}
	
	public static void setSearchQueryAndGo(String query) {
		setSearchQuery(query);
		MainProgram.change_screen(search.identifier()); 
		G_SearchScreen.INSTANCE.results(getSearchResults(search_query));
	}
	
	public static ArrayList<SearchResult> getSearchResults(ArrayList<SearchTerm> query) {
		
		ArrayList<SongResult> songs = new ArrayList<SongResult>();
		
		HashMap<Song, Integer> counter = new HashMap<>();

		boolean has_playlist_filter = false;
		boolean has_keywords = false;
		
		// Add only songs in the playlists searched for
		for (SearchTerm term : query) {
			if (term.type() == SearchTermType.IN_PLAYLIST) {
				has_playlist_filter = true;
				Playlist in = Library.getPlaylist(term.extra());
				for (Song song : in.listSongs()) {
					songs.add(new SongResult(song, in));
					if (!counter.containsKey(song)) counter.put(song, 1);
				}
			}
			if (term.type() == SearchTermType.KEYWORD) {
				has_keywords = true;
			}
		}
		
		// Add every song
		if (!has_playlist_filter) {
			for (Song song : Library.listSongs()) {
				songs.add(new SongResult(song, Library.getAlbum(song.uuid().album).linked_playlist));
			}
		}
				
		// Keywords...
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		for (SongResult song_result : songs) {
			Song song = song_result.song();
			
			if (!counter.containsKey(song)) counter.put(song, 0);
			
			String[] split_title = song.name().split(" ");
			
			
			for (SearchTerm term : query) if (term.type() == SearchTermType.KEYWORD) {
				String match_word = term.extra().toLowerCase();
				
				if (song.name().toLowerCase().contains(match_word)) counter.put(song, counter.get(song) + 1);
				
				for (String word : split_title) {
					if (word.isBlank()) continue;
					
					word = word.toLowerCase();
					if (word.equals(match_word)) counter.put(song, counter.get(song) + 2);
				}
				
			}
			
			if (counter.get(song) != 0) {
				if (!has_keywords) counter.put(song, counter.get(song) + 1);
				results.add(new SearchResult(song, counter.get(song), song_result.playlist()));
			}
			
		}
		
		Collections.sort(results, new SearchResultComparator());
		
//		for (SearchResult res : results) { Log.send("Results: " + ((Song) res.item()).name() + ", " + counter.get(res.item())); }

		return results;
	}
	
	static G_SearchScreen search = G_SearchScreen.INSTANCE;

	@Override public String   identifier() 		{ return "builtin;search"; }
	
	G_Icon 		add_to_playlist_icon 				= new G_Icon("+")
	{ @Override public void onClick() {
		Search.playlist_to_add_to = MusicPlayer.view_playlist;
		setSearchQueryAndGo("");

	} };
	G_Icon 		search_in_playlist_icon 			= new G_Icon("magnifying_glass")
	{ @Override public void onClick() { 
		setSearchQueryAndGo("playlist:" + MusicPlayer.view_playlist);
	} };
	G_Icon 		search_in_home_icon 				= new G_Icon("magnifying_glass")
	{ @Override public void onClick() { 
		setSearchQueryAndGo("");
	} };
	
	@Override
	public void onLoad() throws IOException {
		MainProgram.registerScreen(search);
		
		G_PlaylistScreen.register_header_icon(add_to_playlist_icon, false);
		G_PlaylistScreen.register_header_icon(search_in_playlist_icon, true);
		G_HomeScreen.register_right_icon(search_in_home_icon);
	}
	
	@Override
	public void onTick() {
		if (queued_query != null) {
			Search.setSearchQuery(queued_query);
			queued_query = null;
		}
		if (queued_get_results) {
			G_SearchScreen.INSTANCE.results(getSearchResults(search_query));
			queued_get_results = false;
		}
	}

	static String queued_query = null;
	public static void queueSearchQuery(String newtext) {
		queued_query = newtext;
	}

	static boolean queued_get_results = false;
	public static void queueGetSearchResults() {
		queued_get_results = true;
	}

}