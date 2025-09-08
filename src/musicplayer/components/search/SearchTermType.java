package musicplayer.components.search;

public enum SearchTermType {
	// NOTE: these need to be defined from most specific to least
	// since parsing searches uses the iterator order with that assumption
	IN_PLAYLIST("playlist:"),
	TYPE_ALBUM("type:", "album"),
	TYPE_SONG("type:", "song"),
	TYPE_PLAYLIST("type:", "playlist"),
	KEYWORD();
	
	String[] segments;
	String match_string;
	SearchTermType(String...segments) {
		this.segments = segments;
		this.match_string = "";
		for (String segment : segments) {
			match_string += segment;
		}
	}
}