package nowplaying.gui.screens;

import frost3d.enums.IconType;
import frost3d.utility.Rectangle;
import nowplaying.gui.GUIHoverable;
import nowplaying.gui.abstracts.Screen;
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
 
		left_icons	.limit_rectangle(new Rectangle(b.left(), b.top(), b.left() + icons_ww, b.top() + icons_hh));
		right_icons	.limit_rectangle(new Rectangle(b.right()-icons_ww, b.top(), b.right(), b.top() + icons_hh));

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
		
		addLeftIcon(new GUIHoverable(new GUIIcon(IconType.GENERIC_EDIT)) {
			
		});
		
		addRightIcon(new GUIHoverable(new GUIIcon(IconType.GENERIC_SETTINGS)) {
			
		});
		
		addRightIcon(new GUIHoverable(new GUIIcon(IconType.GENERIC_HAMBURGER)) {
			
		});
		
		addTab(new GUIHoverable(new GUIText("Albums").identifier("home_screen_tab_text")) {
			@Override public void onSingleClick() {
				selectTab(this);
			}
			{ onSingleClick(); }
		});
		
		addTab(new GUIHoverable(new GUIText("Playlists").identifier("home_screen_tab_text")) {
			@Override public void onSingleClick() {
				selectTab(this);
			}
		});
		
	}
	
	public void addLeftIcon	 (GUIHoverable icon) { left_icons 	.add(icon); }
	public void addRightIcon (GUIHoverable icon) { right_icons	.add(icon); }
	public void addTab	 	 (GUIHoverable text) { tabs 		.add(text); }
	
	private void selectTab(GUIHoverable tab) {
		for (GUIElement t : tabs.sub_elements()) {
			((GUIHoverable) t).root().set(PredicateKey.SELECTED, false);
		}
		tab.root().set(PredicateKey.SELECTED, true);
	}

}
