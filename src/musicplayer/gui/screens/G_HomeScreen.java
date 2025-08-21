package musicplayer.gui.screens;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Grid;
import musicplayer.gui.G_Scrollable;

public class G_HomeScreen extends G_Element implements Screen {
	
	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return IDENTIFIER; }

	public static G_Grid albums_grid = new G_Grid();
	public static G_Scrollable albums_scroll = new G_Scrollable(albums_grid);

	public static G_Grid playlists_grid = new G_Grid();
	public static G_Scrollable playlists_scroll = new G_Scrollable(playlists_grid);
	
	private G_HomeScreen() {};
	public static final G_HomeScreen INSTANCE = new G_HomeScreen();
	public static final String IDENTIFIER = "library";

	{ addSubElement(albums_scroll);  addSubElement(playlists_scroll); }
	
	@Override
	public void recalculate_size() {
		albums_scroll.recalculate_size();
		playlists_scroll.recalculate_size();

	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		top += 10;
		int header_height = 30;
		int section_height = ( (bottom-top)-(header_height*2) ) / 2 ;
		int left_margin = 20;
		
		GraphicsAPI.color(GraphicsAPI.WHITE);
		GraphicsAPI.text(left+left_margin, top, 0, "Albums");
		
		int yy = top+header_height;
		
		albums_scroll.layout(left, yy, right, yy+section_height);
		
		GraphicsAPI.color(GraphicsAPI.WHITE);
		GraphicsAPI.text(left+left_margin, (top+section_height+header_height)+5, 0, "Playlists");
		
		yy += section_height + header_height;
		
		playlists_scroll.layout(left, yy, right, yy+section_height);
	}

	@Override
	public void draw(int depth) {
		albums_scroll.draw(0);
		playlists_scroll.draw(0);
	}

}
