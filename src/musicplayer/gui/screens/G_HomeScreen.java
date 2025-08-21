package musicplayer.gui.screens;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Grid;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Log;

public class G_HomeScreen extends G_Element implements Screen {
	
	enum Tab { ALBUMS, PLAYLISTS }
	static Tab current_tab = Tab.ALBUMS;
	
	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return IDENTIFIER; }

	public static G_Grid albums_grid = new G_Grid();
	public static G_Scrollable albums_scroll = new G_Scrollable(albums_grid);

	public static G_Grid playlists_grid = new G_Grid();
	public static G_Scrollable playlists_scroll = new G_Scrollable(playlists_grid);
	
	public static G_Text playlist_tab_text = new G_Text()
			{ @Override public void onClick() { current_tab = Tab.PLAYLISTS; } };
	public static G_Text album_tab_text = new G_Text()
			{ @Override public void onClick() { current_tab = Tab.ALBUMS; } };

	{
		playlist_tab_text.text("Playlists");
		playlist_tab_text.can_click = true;
		playlist_tab_text.left_margin = 15;
		playlist_tab_text.right_margin = 15;
		album_tab_text.text("Albums");
		album_tab_text.can_click = true;
		album_tab_text.left_margin = 15;
		album_tab_text.right_margin = 15;
	}
	public static G_List tab_selector = new G_List(album_tab_text, playlist_tab_text);
	{
		tab_selector.halign(Alignment.MIDDLE);
		tab_selector.recalculate_size();
	}
	
	private G_HomeScreen() {};
	public static final G_HomeScreen INSTANCE = new G_HomeScreen();
	public static final String IDENTIFIER = "library";

	{ addSubElement(albums_scroll);  addSubElement(playlists_scroll); addSubElement(tab_selector); }
	
	@Override
	public void recalculate_size() {
		albums_scroll.recalculate_size();
		playlists_scroll.recalculate_size();
		tab_selector.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		top += 10;
		
		int yy = top;
		tab_selector.layout(left, yy, right, yy+30);
	
		yy += 30;
		
		albums_scroll.layout(left, yy, right, bottom);
		playlists_scroll.layout(left, yy, right, bottom);

		if (current_tab == Tab.ALBUMS) {
			playlists_scroll.remove_input();
			playlist_tab_text.base_color = GraphicsAPI.TRANSLUCENT_WHITE;
			album_tab_text.base_color = GraphicsAPI.WHITE;
		} else {
			albums_scroll.remove_input();
			album_tab_text.base_color = GraphicsAPI.TRANSLUCENT_WHITE;
			playlist_tab_text.base_color = GraphicsAPI.WHITE;
		}
	}

	@Override
	public void draw(int depth) {
		tab_selector.draw(depth);
		if (current_tab == Tab.ALBUMS) {
			albums_scroll.draw(0);
		} else {
			playlists_scroll.draw(0);
		}
	}

}
