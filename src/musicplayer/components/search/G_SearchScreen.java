package musicplayer.components.search;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import musicplayer.MainProgram;
import musicplayer.components.search.SearchRecords.*;
import musicplayer.components.settings.ProgramSettings;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.KeybindAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Song;
import musicplayer.gui.G_Text;
import musicplayer.gui.G_TypingBox;
import musicplayer.gui.G_WrappedList;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Song;

public class G_SearchScreen extends G_Element implements Screen {
	
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
		Search.playlist_to_add_to = null;
		MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
	}};
	
	G_Icon back = new G_Icon("previous")
	{ @Override public void onClick() { 
		Search.playlist_to_add_to = null;
		// TODO: add an onChange or something event to Screen, and put this in G_PlayListScreen
		if (MusicPlayer.current_view_playlist() != null) MusicPlayer.reload_view_playlist();
		MainProgram.previous_screen();
	}};
	
	G_Text info_text = new G_Text();
	
	G_List icons = new G_List(back, home);
	
	G_Scrollable results_scroll = new G_Scrollable(new G_List());
	
	public G_TypingBox input_box = new G_TypingBox() {
		@Override
		public void onChangeText(String newtext) {
			if (newtext.endsWith(" ")) {
				text.text = "";
			} else {
				String [] split = newtext.split(" ");
				text.text = split[split.length-1];
			}
			Search.queueSearchQuery(newtext);
		}
	};
	
	public G_Icon search_go_button = new G_Icon("play") {
		@Override public void onClick() { 
			Search.queueGetSearchResults();
		}
	};
	
	public void results(G_Element newresults) {
		results_scroll.root(newresults);
	}
	
	public void results(ArrayList<SearchResult> all_items) {
		if (all_items.size() <= 0) {
			G_List new_results = new G_List().verticalify();
			G_Text oops = new G_Text();
			oops.text = "No search results found.";
			new_results.add(oops);
			results(new_results);
			return;
		}
		SearchResult result0 = all_items.get(0);
		if (result0.item() instanceof Song) {
			G_List new_results = new G_List().verticalify();
			for (SearchResult result : all_items) {
				G_Song song = new G_Song(((Song) result.item()).uuid(), result.count(), result.playlist(), true);
				if (result.count() <= 1) song.name.base_color = GraphicsAPI.TRANSLUCENT_WHITE;
				new_results.add(song);
			}
			results(new_results);
		}
	}
	
	{ 
		//icons.halign(Alignment.MIDDLE);  
		addSubElement(icons); 
		addSubElement(terms); 
		addSubElement(results_scroll); 
		addSubElement(input_box);
		addSubElement(search_go_button);
		addSubElement(info_text);
		search_go_button.valign(Alignment.MIDDLE);
		info_text.valign(Alignment.MIDDLE);

		}

	public static final G_SearchScreen INSTANCE = new G_SearchScreen();
	
	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return "builtin;search";}
	
	{ 	
		KeybindAPI.bind(
				true,
				"confirm_search", 
				GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_ENTER), 
				GLFW.GLFW_RELEASE,
				( ) -> {
					if (MainProgram.isCurrentScreen(identifier())) {
						search_go_button.onClick();
						KeybindAPI.should_stop_typing = true;
					}
				}
			); 
	}

	@Override
	public void recalculate_size() {
		icons.recalculate_size();
		terms.recalculate_size();
		results_scroll.recalculate_size();
		input_box.recalculate_size();
		search_go_button.recalculate_size();
		info_text.recalculate_size();
	}
			
	@Override
	public void layout(int left, int top, int right, int bottom) {
				
		left += left_margin; right -= right_margin; top += top_margin; bottom -= bottom_margin;
		int yy = top;
		
		icons.layout(left, yy, right, yy+icons.height());
		info_text.layout(left+icons.width(), yy, right, yy+home.height());
		yy += home.height()-5;
		
		terms.layout(left, yy, right, yy+terms.height());
		yy += terms.height();
		
		int right_width = search_go_button.width();
		int input_height = input_box.height();
		input_box.layout(left, yy, right-right_width, yy+input_height);
		search_go_button.layout(right-right_width, yy, right, yy+input_height);
		yy += input_height;
		
		results_scroll.layout(left, yy, right, bottom);
		
		info_text.text = "";
		if (Search.playlist_to_add_to != null) info_text.text = "Adding songs to [" + Library.getPlaylist(Search.playlist_to_add_to).name() + "]";
	}

	@Override
	public void draw(int depth) {
		icons.draw(depth);
		terms.draw(depth);
		results_scroll.draw(depth);
		input_box.draw(depth);
		search_go_button.draw(depth);
		info_text.draw(depth);
	}
	
}