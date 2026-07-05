package nowplaying.gui.screens;

import java.util.ArrayList;

import frost3d.enums.IconType;
import frost3d.utility.Rectangle;
import nowplaying.gui.UI;
import nowplaying.gui.abstracts.Screen;
import nowplaying.settings.gui.SettingsScreen;
import snowui.GUIInstance;
import snowui.coss.enums.PredicateKey;
import snowui.elements.abstracts.GUIElement;
import snowui.elements.base.GUIIcon;
import snowui.elements.base.GUIList;
import snowui.elements.base.GUIScrollable;
import snowui.elements.base.GUIText;
import snowui.elements.floating.GUIContextMenu;
import snowui.elements.floating.GUIContextMenuOption;
import snowui.elements.floating.GUIInputPopup;
import snowui.utility.GUIUtility;

public class HomeScreen extends Screen {
	
	public static final String FAVORITES_GROUP = "§[Favorites";
	
	public static final HomeScreen instance = new HomeScreen();
	public static HomeScreen instance() { return instance; }
	
	GUIList 		left_icons 		= new GUIList().horizontalify();
	GUIList 		right_icons 	= new GUIList().horizontalify();
	GUIList 		tabs 			= new GUIList().horizontalify();
	
	GUIList 		groups			= new GUIList();
	GUIScrollable 	groups_scroll 	= new GUIScrollable(groups);

	@Override
	public void updateDrawInfo(GUIInstance gui) {
		Rectangle b = this.limit_rectangle();
		this.hover_rectangle(b);
		
		int icons_ww = GUIUtility.max_width(GUIUtility.array(left_icons, right_icons));
		int icons_hh = GUIUtility.max_height(GUIUtility.array(left_icons, right_icons));

		left_icons	.limit_rectangle(new Rectangle(b.left(), b.top(), b.left() + left_icons.width(), b.top() + icons_hh));
		right_icons	.limit_rectangle(new Rectangle(b.right()-right_icons.width(), b.top(), b.right(), b.top() + icons_hh));

		int yy = b.top();

		// Albums / Playlists tab text is moved down if there's not enough room between the icons
		Rectangle inbetween = new Rectangle(b.left()+icons_ww, b.top(), b.right() - icons_ww, b.top()+icons_hh);		
		if (inbetween.width() >= tabs.width()) {
			tabs.limit_rectangle(inbetween);
			yy += icons_hh;
		} else {
			yy += icons_hh;
			tabs.limit_rectangle(new Rectangle(b.left(), yy, b.right(), yy+tabs.height()));
			yy += tabs.height();
		}
		
		// Album/Playlist groups
		groups_scroll.limit_rectangle(new Rectangle(b.left(), yy, b.right(), b.bottom()));
	
	}

	// -- ==    _____________________________________________    == -- //
	
	ArrayList<GUIContextMenuOption> add_album_options 		= new ArrayList<>();
	ArrayList<GUIContextMenuOption> add_playlist_options 	= new ArrayList<>();
	
	private enum Tab { ALBUMS, PLAYLISTS }
	private 	 Tab current_tab = Tab.ALBUMS;

	{
		this.registerSubElement(left_icons);
		this.registerSubElement(right_icons);
		this.registerSubElement(tabs);
		this.registerSubElement(groups_scroll);
		
		// Tabs //
				
		tabs.identifier("home_screen_tabs");
		
		addTab(new GUIText("Albums") {
			@Override public void onSingleClick() { selectTab(this, Tab.ALBUMS); }
			{ onSingleClick(); }
		});
		
		addTab(new GUIText("Playlists") {
			@Override public void onSingleClick() { selectTab(this, Tab.PLAYLISTS); }
		});
		
		// Icons //
		
		addLeftIcon(new GUIIcon(IconType.GENERIC_EDIT) {
			
		});
		
		addLeftIcon(new GUIIcon(IconType.GENERIC_PLUS) {
			@Override
			public void onSingleClick() {
				Rectangle b = hover_rectangle();
				if (current_tab == Tab.ALBUMS ) 	UI.addWindow(new GUIContextMenu(add_album_options, b.center().x, b.center().y));
				if (current_tab == Tab.PLAYLISTS ) 	UI.addWindow(new GUIContextMenu(add_playlist_options, b.center().x, b.center().y));
			}
		});
		
		addRightIcon(new GUIIcon(IconType.GENERIC_SETTINGS) {
			@Override
			public void onSingleClick() {
				UI.set_current_screen(SettingsScreen.instance());
			}
		});
		
		addRightIcon(new GUIIcon(IconType.GENERIC_HAMBURGER) {
			
		});
		
	}
	
	public void addLeftIcon	 (GUIIcon icon) { left_icons 	.add(icon); }
	public void addRightIcon (GUIIcon icon) { right_icons	.add(icon); }
	public void addTab	 	 (GUIText text) { tabs 			.add(text);  text.identifier("home_screen_tab_text"); }
	
	private void selectTab(GUIText tab, Tab type) {
		for (GUIElement t : tabs.sub_elements()) t.set(PredicateKey.SELECTED, false);
		tab.set(PredicateKey.SELECTED, true);
		current_tab = type;
		reload_groups();
	}
	
	public static void reload_groups() {
		// TODO Auto-generated method stub
		
	}
	
	// -- ==    _____________________________________________    == -- //

	{
		add_album_options.add(new GUIContextMenuOption(IconType.CONTROL_PIN, "New album", "spooky") {
			@Override
			public void onSingleClick(GUIInstance gui) {
				 {
//						String name = TinyFileDialogs.tinyfd_inputBox(
//								NowPlayingMain.PROGRAM_TITLE + " ", 
//								"Name of new album", 
//								"album-" + (Math.random() * 10000));
//						if (name != null) {
//							try {
//								Album n = new Album(name);
//								Library.registerAlbum(n);
//								n.save();
//							} catch (IOException e) { e.printStackTrace(); }
//						}
					 	GUIInputPopup input = new GUIInputPopup("Name of new album", "album-" + (Math.random() * 10000), gui) {
					 		@Override
					 		public void onFinish(String string) {
					 			
					 		}
					 	};
					 	UI.addWindow(input);
					}
				 close_menu(gui);
			}
		});
	}

}
