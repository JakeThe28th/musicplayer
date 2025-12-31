package nowplaying.settings.gui;

import frost3d.enums.IconType;
import frost3d.utility.Rectangle;
import nowplaying.gui.GUIHoverable;
import nowplaying.gui.abstracts.Screen;
import snowui.GUIInstance;
import snowui.elements.abstracts.GUIElement;
import snowui.elements.base.GUICollapsible;
import snowui.elements.base.GUIIcon;
import snowui.elements.base.GUIList;
import snowui.elements.base.GUIScrollable;
import snowui.elements.base.GUIText;

public class SettingsScreen extends Screen {
	
	public static final SettingsScreen instance = new SettingsScreen();
	public static SettingsScreen instance() { return instance; }
	
	GUIList 		header 			= new GUIList();
	
	GUIList 		list 			= new GUIList();
	GUIScrollable 	scroll 			= new GUIScrollable(list);
	
	{
		this.registerSubElement(header);
		this.registerSubElement(scroll);
		header.identifier("settings_header_icons");
		header.add(new GUIHoverable(new GUIIcon(IconType.GENERIC_HOME)) {
			
		});
		
		loadSettingsList();
	}
	
	@Override
	public void updateDrawInfo(GUIInstance gui) {
		Rectangle b = this.limit_rectangle();
					  this.hover_rectangle(b);
		header.limit_rectangle(new Rectangle(b.left(), b.top(), 				b.right(), b.top()+header.height()));
		scroll.limit_rectangle(new Rectangle(b.left(), b.top()+header.height(), b.right(), b.bottom()));
	}
	
	// -- -- -- //
	
	public void loadSettingsList() {
		list.clear();
		
		GUIList color_settings 		= new GUIList();
		GUIList boolean_settings 	= new GUIList();
		GUIList slider_settings 	= new GUIList();
		
		list.add(section("Colors", color_settings));
		list.add(section("Toggles", boolean_settings));
		list.add(section("Sliders", slider_settings));
		
		list.identifier("setting_list");


	}

	private GUICollapsible section(String name, GUIList setting_list) {
		GUIList hidden_list = new GUIList();
			hidden_list.add(setting_list);
			hidden_list.add(new GUISectionLine());
			hidden_list.identifier("setting_list");
		GUICollapsible result = new GUICollapsible();
			result.icon().identifier("setting_section_icon");
			result.root(new GUIText(name).identifier("setting_section_title"));
			result.hidden(hidden_list);
			result.even_center(true);
		result.identifier("setting_collapsible");
		return result;
	}

}
