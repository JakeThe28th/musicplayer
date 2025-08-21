package musicplayer.gui.screens;

import musicplayer.gui.G_Element;
import musicplayer.gui.G_List;
import musicplayer.gui.G_PlaylistHeader;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Text;

public class G_PlaylistScreen extends G_Element {
	
	public static G_Scrollable playlist_gui_scroll = new G_Scrollable(new G_Text());
	public static G_List playlist_gui_list;
	public static G_PlaylistHeader playlist_header = new G_PlaylistHeader();
	
	private G_PlaylistScreen() {};
	public static G_PlaylistScreen instance = new G_PlaylistScreen();
	
	{ addSubElement(playlist_header); 
	addSubElement(playlist_gui_scroll); }

	@Override
	public void recalculate_size() {
		playlist_header.recalculate_size();
		playlist_gui_scroll.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		playlist_header.layout(left, top, right, playlist_header.height());
		playlist_gui_scroll.layout(left, playlist_header.height(), right, bottom);
	}

	@Override
	public void draw(int depth) {
		playlist_header.draw(0);
		playlist_gui_scroll.draw(0);
	}
	
	public static void set_playlist_list(G_List newlist) {
		playlist_gui_list = newlist;
		instance.removeSubElement(playlist_gui_scroll);
		playlist_gui_scroll = new G_Scrollable(newlist);
		instance.addSubElement(playlist_gui_scroll);
	}

}
