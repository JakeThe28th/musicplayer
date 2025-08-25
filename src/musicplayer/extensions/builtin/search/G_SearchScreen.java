package musicplayer.extensions.builtin.search;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import musicplayer.MainProgram;
import musicplayer.extensions.builtin.ProgramSettings;
import musicplayer.extensions.builtin.search.SearchRecords.*;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Song;
import musicplayer.gui.G_TypingBox;
import musicplayer.gui.G_WrappedList;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.Screen;
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
		MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
	}};
	
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
		home.halign(Alignment.MIDDLE);  
		addSubElement(home); 
		addSubElement(terms); 
		addSubElement(results_scroll); 
		addSubElement(input_box);
		addSubElement(search_go_button);
		search_go_button.valign(Alignment.MIDDLE);
		}

	public static final G_SearchScreen INSTANCE = new G_SearchScreen();
	
	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return "builtin;search";}
	
	{ 	
		ProgramSettings.bindKeyLimited(identifier(), ()->{search_go_button.onClick();}, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_RELEASE ); 
	}

	@Override
	public void recalculate_size() {
		home.recalculate_size();
		terms.recalculate_size();
		results_scroll.recalculate_size();
		input_box.recalculate_size();
		search_go_button.recalculate_size();
	}
			
	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		left += left_margin; right -= right_margin; top += top_margin; bottom -= bottom_margin;
		int yy = top;
		
		home.layout(left, yy, right, yy+home.height());
		yy += home.height();
		
		terms.layout(left, yy, right, yy+terms.height());
		yy += terms.height();
		
		int right_width = search_go_button.width();
		int input_height = input_box.height();
		input_box.layout(left, yy, right-right_width, yy+input_height);
		search_go_button.layout(right-right_width, yy, right, yy+input_height);
		yy += input_height;
		
		results_scroll.layout(left, yy, right, bottom);
	}

	@Override
	public void draw(int depth) {
		home.draw(depth);
		terms.draw(depth);
		results_scroll.draw(depth);
		input_box.draw(depth);
		search_go_button.draw(depth);
	}
	
}