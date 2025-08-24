package musicplayer.extensions.builtin;

import java.io.IOException;

import musicplayer.MainProgram;
import musicplayer.extensions.Extension;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.parts.MusicPlayer;

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
	
	static String playlist_to_add_to = null;

	static String[] search_query;
	static String search_text;

	private void search(String[] query) {
		search_query = query;
		search_text = "";
		for (String part : search_query) search_text += part + " ";
		MainProgram.change_screen(search.identifier()); 
	}
	
//S	record SearchResult
	
	private void evaluate() {
		//search_query = query;
		search_text = "";
		for (String part : search_query) search_text += part + " ";
		MainProgram.change_screen(search.identifier()); 
	}
	
	static class G_SearchScreen extends G_Element implements Screen {
		
		G_Icon 		home 			= new G_Icon("home")
		{ @Override public void onClick() { 
			MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
		}};
		
		{ 
			home.halign(Alignment.MIDDLE);
		}
		
		{ addSubElement(home); }

		public static final G_SearchScreen INSTANCE = new G_SearchScreen();

		@Override
		public void recalculate_size() {
			home.recalculate_size();
		}
		
		int query_y;
		int left, right, top, bottom;
		
		@Override
		public void layout(int left, int top, int right, int bottom) {
			
			left += left_margin;
			right -= right_margin;
			top += top_margin;
			bottom -= bottom_margin;
			
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
		
			int yy = top;
			home.layout(left, top, right, top+home.height());
			yy += home.height();
			
			int query_height = 30;
			query_y = yy;
			yy += query_height;
			
		}

		@Override
		public void draw(int depth) {
			home.draw(depth);
			GraphicsAPI.text(left, query_y, depth, search_text);
		}

		@Override public G_Element instance() { return INSTANCE; }
		@Override public String identifier() { return "builtin;search";}
		
	}
	
	G_SearchScreen search = G_SearchScreen.INSTANCE;

	@Override public String   identifier() 		{ return "builtin;search"; }
	
	G_Icon 		add_to_playlist_icon 				= new G_Icon("+")
	{ @Override public void onClick() { } };
	G_Icon 		search_in_playlist_icon 			= new G_Icon("magnifying_glass")
	{ @Override public void onClick() { 
		search(new String[] { "playlist:" + MusicPlayer.view_playlist });
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