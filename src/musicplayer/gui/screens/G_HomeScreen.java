package musicplayer.gui.screens;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Grid;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.extra.Popup;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.parts.Album;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
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
			
	public static G_Icon new_collection 	= new G_Icon("+")
		{ @Override public void onClick() {
			Option[] options = Option.from(current_tab == Tab.ALBUMS ? menu_options_album : menu_options_playlist);
			MainProgram.popups.add(new Popup( x, y + height(), Alignment.LEFT, Alignment.LEFT, options ) );
		} };
		
	public static G_Icon menu 			= new G_Icon("hamburger")
		{ @Override public void onClick() {
			Option[] options = Option.from(library_menu_options);
			MainProgram.popups.add(new Popup(x + width(), y + height(), Alignment.RIGHT, Alignment.LEFT, options));
		} };
	
		public static ArrayList<Option> library_menu_options = new ArrayList<Option>();
		public static ArrayList<Option> menu_options_album = new ArrayList<Option>();
		public static ArrayList<Option> menu_options_playlist = new ArrayList<Option>();
		
		static {
//			library_menu_options.add(new Option("Other Test Button (pause)", () -> {
//				MusicPlayer.pause();
//			}));

			menu_options_album.add(new Option("New album", () -> {
				String name = TinyFileDialogs.tinyfd_inputBox(
						MainProgram.PROGRAM_TITLE + " ", 
						"Name of new album", 
						"album-" + (Math.random() * 10000));
				if (name != null) {
					try {
						Album n = new Album(name);
						Library.registerAlbum(n);
						n.save();
					} catch (IOException e) { e.printStackTrace(); }
				}
			}));
			
			menu_options_playlist.add(new Option("New playlist", () -> {
				String name = TinyFileDialogs.tinyfd_inputBox(
						MainProgram.PROGRAM_TITLE + " ", 
						"Name of new playlist", 
						"playlist-" + (Math.random() * 10000));
				if (name != null) {
					try {
						Playlist p = new Playlist(name, null);
						Library.registerPlaylist(p);
						p.save();
					} catch (IOException e) { e.printStackTrace(); }
				}
			}));
			
		}
		
		public static void addMenuOption(Option o) {
			library_menu_options.add(o);
		}

		public static void addAlbumMenuOption(Option o) {
			menu_options_album.add(o);
		}

		public static void addPlaylistMenuOption(Option o) {
			menu_options_playlist.add(o);
		}
		
	{
		playlist_tab_text.text("Playlists");
		playlist_tab_text.can_click = true;
		playlist_tab_text.left_margin = 15;
		playlist_tab_text.right_margin = 15;
		album_tab_text.text("Albums");
		album_tab_text.can_click = true;
		album_tab_text.left_margin = 15;
		album_tab_text.right_margin = 15;
		left_margin = 10;
		right_margin = 10;
	}
	public static G_List tab_selector = new G_List(album_tab_text, playlist_tab_text);
	{
		tab_selector.halign(Alignment.MIDDLE);
		tab_selector.recalculate_size();
	}
	
	private G_HomeScreen() {};
	public static final G_HomeScreen INSTANCE = new G_HomeScreen();
	public static final String IDENTIFIER = "library";

	{ 
		addSubElement(albums_scroll);  
		addSubElement(playlists_scroll); 
		addSubElement(tab_selector); 
		addSubElement(menu); 
		addSubElement(new_collection);
	}
	
	@Override
	public void recalculate_size() {
		albums_scroll.recalculate_size();
		playlists_scroll.recalculate_size();
		tab_selector.recalculate_size();
		menu.recalculate_size();
		new_collection.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		top += 10;
		
		left += left_margin;
		right -= right_margin;
		
		int icon_width = menu.width();
		int header_height = 30;
		
		int yy = top;
		tab_selector.layout(left+icon_width, yy, right-icon_width, yy+header_height);
		
		menu.layout(right-icon_width, yy, right, yy+header_height);
		new_collection.layout(left, yy, left+icon_width, yy+header_height);
		
		yy += header_height;
		
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
		menu.draw(depth);
		new_collection.draw(depth);
		if (current_tab == Tab.ALBUMS) {
			albums_scroll.draw(0);
		} else {
			playlists_scroll.draw(0);
		}
	}

}
