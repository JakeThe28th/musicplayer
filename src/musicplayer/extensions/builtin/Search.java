package musicplayer.extensions.builtin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.extensions.Extension;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_WrappedList;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.parts.MusicPlayer;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class Search extends Extension  {
	
	/* 
	SEARCH
	Next to the hamburger menus in the home and 
	playlist screens, a search magnifying glass 
	icon is added.
	
	Clicking it will, open the search screen, 
	and if clicked from the playlist menu set 
	the query to "in:playlist_identifier type:song"
	in the home menu, it's just "type:album" or "type:playlist".
	
	A list of songs will appear. Note: These should
	be modified, so use Library.GUIOfSong(UUID), a
	-nd change MusicPlayer to use that too. Clicking
	a song still plays it, and the search menu works
	like any other playlist.  TODO: change the index
	check to JUST be a uuid check.
	
	An additional context menu option is added, "go to",
	which will take you to that song's album if clicked.
	
	
	== Algorithm ==
	Search results are stored as SearchResult<Object o, String[] keywords>
	  o is either Playlist or Song
	  keywords contains the words of the title, the artist name, etc
	
	There is an LinkedHashSet<SearchResult> called results.
	SearchResult's hashcode is the object's hashcode, and songs use UUIDs for that.
	So, no duplicate songs will be added.
	
	First, check for the type tag, if it's album, add all albums to the results list, same for playlist.
	If it's song, then check for an 'in' tag. 
	  For every 'in' tag, add all of the songs in that album or playlist.
	If no 'in' tag is present, add all songs.
	
	Then, create a new results list, of HashMap<Object o, Integer i>.
	for every song, count the number of keyword matches.
	At the end, rank by number of keywords.
	 */
	
	static record SearchTerm(SearchTermType type, String extra) {}
	
	enum SearchTermType {
		// NOTE: these need to be defined from most specific to least
		// since parsing searches uses the iterator order with that assumption
		IN_PLAYLIST("playlist:"),
		TYPE_ALBUM("type:", "album"),
		TYPE_SONG("type:", "song"),
		TYPE_PLAYLIST("type:", "playlist"),
		KEYWORD("");
		
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
	
	static class G_SearchTerm extends G_Element {
		
		{ allmargins(3); }
		
		static Vector4f[] colors = new Vector4f[] {
			new Vector4f(30 / 255f, 30 / 255f, 31 / 255f, 1),
			new Vector4f(129 / 255f, 217 / 255f, 227 / 255f, 1),
			new Vector4f(72 / 255f, 232 / 255f, 96 / 255f, 1),
			new Vector4f(232 / 255f, 184 / 255f, 72 / 255f, 1)
		};
		
		SearchTermType type;
		String extra;
		
		public G_SearchTerm(SearchTerm term) {
			type = term.type;
			extra = term.extra;
		}

		@Override
		public void recalculate_size() {
			this.unpadded_height = GraphicsAPI.size("to find out").y + (top_margin) + (bottom_margin);
			
			int count = type.segments.length + (extra.isEmpty() ? 0 : 1);
			this.unpadded_width = (left_margin * count) + (right_margin * count);
			for (String segment : type.segments) this.unpadded_width += GraphicsAPI.size(segment).x;
			if (!extra.isEmpty()) this.unpadded_width += GraphicsAPI.size(extra).x;
		}
		
		Rectangle[] segments;

		@Override
		public void layout(int left, int top, int right, int bottom) {
			left += left_margin;
			right -= right_margin;
			top += top_margin;
			bottom -= bottom_margin;

			segments = new Rectangle[type.segments.length + (extra.isEmpty() ? 0 : 1)];
			int xx = left;
			for (int i = 0; i < type.segments.length; i++) {
				int width = left_margin + right_margin + GraphicsAPI.size(type.segments[i]).x;
				segments[i] = new Rectangle(xx, top, xx+width, bottom);
				xx += width;
			}
			if (!extra.isEmpty()) {
				int width = left_margin + right_margin + GraphicsAPI.size(extra).x;
				segments[segments.length-1] = new Rectangle(xx, top, xx+width, bottom);
			}
		}

		@Override
		public void draw(int depth) {
		  for (int i = 0; i < segments.length; i++) {
			Rectangle r = segments[i];
			GraphicsAPI.color(colors[i]);
			GraphicsAPI.rect(r, depth);
			
			GraphicsAPI.color(GraphicsAPI.WHITE);
			if (i < type.segments.length) {
				GraphicsAPI.text(r.left() + left_margin, r.top() + top_margin, depth + 2, type.segments[i]);
			} else {
				GraphicsAPI.text(r.left() + left_margin, r.top() + top_margin, depth + 2, extra);
			}
		  }
		}
	}
	
	static class G_SearchScreen extends G_Element implements Screen {
		
		G_WrappedList terms = new G_WrappedList();
		{
			terms.add(new G_SearchTerm( new SearchTerm(SearchTermType.TYPE_PLAYLIST, "Hello!" )));
			terms.add(new G_SearchTerm( new SearchTerm(SearchTermType.IN_PLAYLIST, "Jello?!" )));
			terms.add(new G_SearchTerm( new SearchTerm(SearchTermType.KEYWORD, "Rello!" )));
			terms.add(new G_SearchTerm( new SearchTerm(SearchTermType.KEYWORD, "Mosaic" )));
			terms.add(new G_SearchTerm( new SearchTerm(SearchTermType.KEYWORD, "Staring blankly down at me.." )));
			terms.add(new G_SearchTerm( new SearchTerm(SearchTermType.TYPE_PLAYLIST, "Hello!" )));
		}
		
		public void terms(G_WrappedList newterms) {
			removeSubElement(terms);
			terms = newterms;
			addSubElement(terms);
		}
		
		G_Icon home = new G_Icon("home")
		{ @Override public void onClick() { 
			MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
		}};
		
		{ home.halign(Alignment.MIDDLE);  addSubElement(home); addSubElement(terms); }

		public static final G_SearchScreen INSTANCE = new G_SearchScreen();
		
		@Override public G_Element instance() { return INSTANCE; }
		@Override public String identifier() { return "builtin;search";}

		@Override
		public void recalculate_size() {
			home.recalculate_size();
			terms.recalculate_size();
		}
				
		@Override
		public void layout(int left, int top, int right, int bottom) {
			
			left += left_margin; right -= right_margin; top += top_margin; bottom -= bottom_margin;
		
			int yy = top;
			home.layout(left, top, right, top+home.height());
			yy += home.height();
			
			terms.layout(left, yy, right, bottom);
		}

		@Override
		public void draw(int depth) {
			home.draw(depth);
			terms.draw(depth);
		}
		
	}
	
	// ^^^ GUI Stuff ^^^ //
	
	ArrayList<SearchTerm> search_query = new ArrayList<SearchTerm>();
	
	static String playlist_to_add_to = null;

	private void search(String query) {
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
		
//		for (SearchTerm ob : search_query) {
//			Log.send(ob.type + " : " + ob.extra);
//		}
		
		G_WrappedList GUI_terms = new G_WrappedList();
		for (SearchTerm term : search_query) {
			GUI_terms.add(new G_SearchTerm(term));
		}
		G_SearchScreen.INSTANCE.terms(GUI_terms);
		
		
		MainProgram.change_screen(search.identifier()); 
	}
	
	G_SearchScreen search = G_SearchScreen.INSTANCE;

	@Override public String   identifier() 		{ return "builtin;search"; }
	
	G_Icon 		add_to_playlist_icon 				= new G_Icon("+")
	{ @Override public void onClick() { } };
	G_Icon 		search_in_playlist_icon 			= new G_Icon("magnifying_glass")
	{ @Override public void onClick() { 
		search("playlist:" + MusicPlayer.view_playlist );
	} };
	G_Icon 		search_in_home_icon 				= new G_Icon("magnifying_glass")
	{ @Override public void onClick() { } };
	
	@Override
	public void onLoad() throws IOException {
		MainProgram.registerScreen(search);
		
		G_PlaylistScreen.register_header_icon(add_to_playlist_icon, false);
		G_PlaylistScreen.register_header_icon(search_in_playlist_icon, true);

	}

}