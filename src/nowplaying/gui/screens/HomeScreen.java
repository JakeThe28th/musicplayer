package nowplaying.gui.screens;

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
import snowui.utility.GUIUtility;

public class HomeScreen extends Screen {
	
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
	
	{
		this.registerSubElement(left_icons);
		this.registerSubElement(right_icons);
		this.registerSubElement(tabs);
		this.registerSubElement(groups_scroll);
				
		tabs.identifier("home_screen_tabs");
		
		addLeftIcon(new GUIIcon(IconType.GENERIC_EDIT) {
			
		});
		
		addRightIcon(new GUIIcon(IconType.GENERIC_SETTINGS) {
			@Override
			public void onSingleClick() {
				UI.set_current_screen(SettingsScreen.instance());
			}
		});
		
		addRightIcon(new GUIIcon(IconType.GENERIC_HAMBURGER) {
			
		});
		
		addTab(new GUIText("Albums") {
			@Override public void onSingleClick() {
				selectTab(this);
			}
			{ onSingleClick(); }
		});
		
		addTab(new GUIText("Playlists") {
			@Override public void onSingleClick() {
				selectTab(this);
			}
		});
		
	}
	
	public void addLeftIcon	 (GUIIcon icon) { left_icons 	.add(icon); }
	public void addRightIcon (GUIIcon icon) { right_icons	.add(icon); }
	public void addTab	 	 (GUIText text) { tabs 			.add(text);  text.identifier("home_screen_tab_text"); }
	
	private void selectTab(GUIText tab) {
		for (GUIElement t : tabs.sub_elements()) {
			((GUIText) t).set(PredicateKey.SELECTED, false);
		}
		tab.set(PredicateKey.SELECTED, true);
	}

}
