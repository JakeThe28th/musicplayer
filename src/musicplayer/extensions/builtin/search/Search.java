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
	
	ArrayList<SearchTerm> search_query = new ArrayList<SearchTerm>();
	
	static String playlist_to_add_to = null;

	private void setSearchQuery(String query) {
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
		MainProgram.change_screen(search.identifier()); 
		
		
		G_SearchScreen.INSTANCE.results(getSearchResults(search_query));
	}
	
	private ArrayList<SearchResult> getSearchResults(ArrayList<SearchTerm> query) {
		
		ArrayList<SongResult> songs = new ArrayList<SongResult>();
		
		boolean has_playlist_filter = false;
		
		// Add only songs in the playlists searched for
		for (SearchTerm term : query) {
			if (term.type() == SearchTermType.IN_PLAYLIST) {
				has_playlist_filter = true;
				Playlist in = Library.getPlaylist(term.extra());
				for (Song song : in.listSongs()) {
					songs.add(new SongResult(song, in));
				}
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
		HashMap<Song, Integer> counter = new HashMap<>();
		for (SongResult song_result : songs) {
			Song song = song_result.song();
			
			if (!counter.containsKey(song)) counter.put(song, 0);
			
			String[] split_title = song.name().split(" ");
			
			
			for (SearchTerm term : query) if (term.type() == SearchTermType.KEYWORD) {
				String match_word = term.extra().toLowerCase();
				
				if (song.name().contains(match_word)) counter.put(song, counter.get(song) + 1);
				
				for (String word : split_title) {
					word = word.toLowerCase();
					if (word.equals(match_word)) counter.put(song, counter.get(song) + 1);
				}
				
			}
			
			results.add(new SearchResult(song, counter.get(song), song_result.playlist()));
			
		}
		
		Collections.sort(results, new SearchResultComparator());
		
//		for (SearchResult res : results) { Log.send("Results: " + ((Song) res.item()).name() + ", " + counter.get(res.item())); }

		return results;
	}
	
	G_SearchScreen search = G_SearchScreen.INSTANCE;

	@Override public String   identifier() 		{ return "builtin;search"; }
	
	G_Icon 		add_to_playlist_icon 				= new G_Icon("+")
	{ @Override public void onClick() { } };
	G_Icon 		search_in_playlist_icon 			= new G_Icon("magnifying_glass")
	{ @Override public void onClick() { 
		setSearchQuery("playlist:" + MusicPlayer.view_playlist + " music box");
	} };
	G_Icon 		search_in_home_icon 				= new G_Icon("magnifying_glass")
	{ @Override public void onClick() { 
		setSearchQuery("die prologue");
	} };
	
	@Override
	public void onLoad() throws IOException {
		MainProgram.registerScreen(search);
		
		G_PlaylistScreen.register_header_icon(add_to_playlist_icon, false);
		G_PlaylistScreen.register_header_icon(search_in_playlist_icon, true);
		G_HomeScreen.register_right_icon(search_in_home_icon);
	}

}