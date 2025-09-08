package musicplayer.components.search;

import java.util.Comparator;

import musicplayer.parts.Playlist;
import musicplayer.parts.Song;

public class SearchRecords {
	
	public static record SearchTerm		(SearchTermType type, 	String 		extra) 						{}
	public static record SearchResult	(Object 		item, 	int 		count, 	Playlist playlist) 	{}
	public static record SongResult		(Song 			song, 	Playlist 	playlist) 					{}
	public static class SearchResultComparator implements Comparator<SearchResult> {
	    @Override
	    public int compare(SearchResult o1, SearchResult o2) {
	        return Integer.compare(o2.count, o1.count);
	    }
	}

}
