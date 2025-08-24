package musicplayer.gui.screens;

import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_PlaylistHeader;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Text;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.parts.MusicPlayer;

public class G_PlaylistScreen extends G_Element implements Screen {
	
	public record PlaylistOptionIcon(G_Icon icon, boolean show_while_locked) {}
	public static ArrayList<PlaylistOptionIcon> icons = new ArrayList<PlaylistOptionIcon>();
	
	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return IDENTIFIER; }
	
	public static G_Scrollable playlist_gui_scroll = new G_Scrollable(new G_Text());
	public static G_List playlist_gui_list;
	public static G_PlaylistHeader playlist_header = new G_PlaylistHeader();
	
	private G_PlaylistScreen() {};
	public static final G_PlaylistScreen INSTANCE = new G_PlaylistScreen();
	public static final String IDENTIFIER = "playlist";

	{ addSubElement(playlist_header); 
	addSubElement(playlist_gui_scroll); }
	
	public static ArrayList<Option> playlist_menu_options = new ArrayList<Option>();

	static {
//		album_menu_options.add(new Option("Clone as Playlist", () -> {
//			MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
//		}));
	}

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
		INSTANCE.removeSubElement(playlist_gui_scroll);
		playlist_gui_scroll = new G_Scrollable(newlist);
		INSTANCE.addSubElement(playlist_gui_scroll);
	}

	public static void register_header_icon(G_Icon icon, boolean show_while_locked) {
		icons.add(new PlaylistOptionIcon(icon, show_while_locked));
	}
}
