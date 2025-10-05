package musicplayer.gui.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_DraggableNamedGroup;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Grid;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_PlaylistGridItem;
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
import musicplayer.utility.Utility;

public class G_HomeScreen extends G_Element implements Screen {
	
	static ArrayList<Playlist> playlists = new ArrayList<>();
	
	public static void addPlaylist(Playlist playlist, boolean update) {
		playlists.add(playlist);
		if (update) update_playlist_views();
	}
	
	// Playlist/Album groups creation //
	
	public static int previous_grid_width = -1;
	
	public static void update_playlist_views() {

		album_groups.clear();
		HashMap<String, G_Grid> album_grids = new HashMap<>();
		for (String group : Library.album_group_order) {
			ensure(album_grids, group, false, true);
		}
		
		playlist_groups.clear();
		HashMap<String, G_Grid> playlist_grids = new HashMap<>();
		for (String group : Library.playlist_group_order) {
			ensure(playlist_grids, group, false, false);
		}
		
		for (Playlist playlist : playlists) {
						
			String group = playlist.metadata("group");
			if (group == null) group = "Default";
			
			if (playlist.is_album) {
				ensure(album_grids, group, true, true);
				G_Grid grid = album_grids.get(group);
				grid.add(new G_PlaylistGridItem(playlist));
			} else {
				ensure(playlist_grids, group, true, false);
				G_Grid grid = playlist_grids.get(group);
				grid.add(new G_PlaylistGridItem(playlist));
			}
			
		}
		
	}
	
	/** helper method only here to reduce copy pasting code */
	private static void ensure(HashMap<String, G_Grid> grids, String group, boolean update_group_order, boolean is_album) {
		if (!grids.containsKey(group)) {
			grids.put(group, new G_Grid());
			G_Grid grid = grids.get(group);	
			G_DraggableNamedGroup group_element = new G_DraggableNamedGroup(group, grid);
			if (is_album) album_groups.add(group_element);
			if (!is_album) playlist_groups.add(group_element);
			
			// avoid scrolling bugging out because the calculated size is wrong
			if (previous_grid_width > 0) grid.calculate_grid_dimensions(previous_grid_width);

			if (update_group_order) {
				if (is_album) Library.add_album_group(group);
				if (!is_album) Library.add_playlist_group(group);
				Library.save_group_order();
			}
		}
	}
	
	// -- //
	
	public static boolean editing = false;
	public static long edit_change_time = 0;
	public static long edit_anim_end_time = 0;
	public static final long EDIT_TRANSITION_TIME_MS = 100;
	
	public static float current_edit_anim_time() {
		if (System.currentTimeMillis() > edit_anim_end_time) return editing ? 1 : 0;
				
		int difference = (int) (System.currentTimeMillis() - edit_change_time);
		float amt = (difference / ((float) EDIT_TRANSITION_TIME_MS));
		
	//	amt = (float) Math.sqrt(amt);
		
		return editing ? amt : 1-amt;
	}
	
	public static boolean editing() {
		if (System.currentTimeMillis() > edit_anim_end_time) return editing;
		return true;
	}
	
	public static boolean editing_transition_complete() {
		return (System.currentTimeMillis() > edit_anim_end_time);
	}
	
	enum Tab { ALBUMS, PLAYLISTS }
	static Tab current_tab = Tab.ALBUMS;
	
	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return IDENTIFIER; }

	public static G_List album_groups = new G_List().verticalify();
	public static G_Scrollable albums_scroll = new G_Scrollable(album_groups);

	public static G_List playlist_groups = new G_List().verticalify();
	public static G_Scrollable playlists_scroll = new G_Scrollable(playlist_groups);
	
	public static G_Text playlist_tab_text = new G_Text()
			{ @Override public void onClick() { current_tab = Tab.PLAYLISTS; } };
	public static G_Text album_tab_text = new G_Text()
			{ @Override public void onClick() { current_tab = Tab.ALBUMS; } };
			
	public static G_Icon new_collection 	= new G_Icon("+")
		{ @Override public void onClick() {
			Option[] options = Option.from(current_tab == Tab.ALBUMS ? menu_options_album : menu_options_playlist);
			MainProgram.popups.add(new Popup( x, y + height(), Alignment.LEFT, Alignment.LEFT, options ) );
		} };
		
	public static G_Icon edit_collections 	= new G_Icon("pencil")
		{ @Override public void onClick() {
			editing = !editing;
			edit_change_time = System.currentTimeMillis();
			edit_anim_end_time = System.currentTimeMillis() + EDIT_TRANSITION_TIME_MS;
		} };
		
	public static G_Icon menu 			= new G_Icon("hamburger")
		{ @Override public void onClick() {
			Option[] options = Option.from(library_menu_options);
			MainProgram.popups.add(new Popup(x + width(), y + height(), Alignment.RIGHT, Alignment.LEFT, options));
		} };
		
	public G_List left_icons 			= new G_List(new_collection, edit_collections);
	public G_List right_icons 			= new G_List(menu);

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
		
		public static void register_left_icon(G_Icon icon) {
			INSTANCE.left_icons.add(icon, 0);
		}

		public static void register_right_icon(G_Icon icon) {
			INSTANCE.right_icons.add(icon, 0);
		}
		
	{
		playlist_tab_text.text("Playlists");
		playlist_tab_text.can_click = true;
		playlist_tab_text.left_margin = 10;
		playlist_tab_text.right_margin = 10;
		album_tab_text.text("Albums");
		album_tab_text.can_click = true;
		album_tab_text.left_margin = 10;
		album_tab_text.right_margin = 10;
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
		addSubElement(left_icons); 
		addSubElement(right_icons);
	}
	
	@Override
	public void recalculate_size() {
		albums_scroll.recalculate_size();
		playlists_scroll.recalculate_size();
		tab_selector.recalculate_size();
		left_icons.recalculate_size();
		right_icons.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		top += 10;
		
		left += left_margin;
		right -= right_margin;
		
		int icons_width = left_icons.width();
		if (icons_width < right_icons.width()) icons_width = right_icons.width();

		int header_height = 30;
		
		int yy = top;
		right_icons.layout(right-icons_width, yy, right, yy+header_height);
		left_icons.layout(left, yy, left+icons_width, yy+header_height);
		
		if ((icons_width*2) + tab_selector.width() > GraphicsAPI.width()) {
			yy += header_height;
		}
		
		tab_selector.layout(left+icons_width, yy, right-icons_width, yy+header_height);
		
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
		left_icons.draw(depth);
		right_icons.draw(depth);
		if (current_tab == Tab.ALBUMS) {
			albums_scroll.draw(0);
		} else {
			playlists_scroll.draw(0);
		}
	}

}
