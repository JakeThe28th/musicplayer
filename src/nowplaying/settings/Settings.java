package nowplaying.settings;

import java.util.HashMap;

import nowplaying.NowPlayingMain;
import nowplaying.gui.UI;
import nowplaying.settings.data.Setting;
import nowplaying.settings.data.SettingSlot;
import nowplaying.settings.data.enums.SettingCategory;
import nowplaying.settings.data.types.BooleanSetting;
import nowplaying.settings.data.types.RangedIntegerSetting;

public class Settings {
	
	public static HashMap<String, SettingSlot> 		settings 		= new HashMap<>();
	public static HashMap<String, String> 			friendly_names 	= new HashMap<>();
	public static HashMap<String, SettingCategory> 	categories		= new HashMap<>();

	public static Setting 	get(String key) 				{ return settings.get(key).value(); 			 }
	public static void 		set(String key, Setting value)  { 		 settings.get(key).defined_value(value); }
	
	public static String			getname	   (String key) { return friendly_names.get(key);	}
	public static SettingCategory	getcategory(String key) { return categories	   .get(key);	}

	/** Initializes a setting */
	private static void put(String setting_id, String name, SettingCategory category, SettingSlot slot) {
		settings		.put(setting_id, slot		);
		friendly_names	.put(setting_id, name		);
		categories		.put(setting_id, category	);
	}
	
	static {
		put("use_vsync", "Enable Vsync", SettingCategory.PERFORMANCE, 
		  new SettingSlot(new BooleanSetting(true)) {
			@Override public void onChange(Setting new_value) {
				NowPlayingMain.window.setVsync(((BooleanSetting) new_value).value);
		  }
		});
		put("font_size", "Font Size", SettingCategory.THEME, 
		  new SettingSlot(new RangedIntegerSetting(18, 4, 30)) {
			@Override public void onChange(Setting new_value) {
				UI.setFontSize(((RangedIntegerSetting) new_value).current);
		  }
		});
		put("auto_reload_theme", "Automatically reload theme", SettingCategory.THEME, 
		  new SettingSlot(new BooleanSetting(false)) {
			@Override public void onChange(Setting new_value) { }
		});
		put("skip_broken_songs", "Skip broken songs", SettingCategory.BEHAVIOR, 
		  new SettingSlot(new BooleanSetting(true)) {
			@Override public void onChange(Setting new_value) { }
		});
		put("previous_song_buffer_threshold", "[previous_song_buffer_threshold]", SettingCategory.BEHAVIOR, 
		  new SettingSlot(new RangedIntegerSetting(5, 1, 30)) {
			@Override public void onChange(Setting new_value) { }
		});
		put("use_logarithmic_volume", "Use logarithmic volume", SettingCategory.BEHAVIOR, 
		  new SettingSlot(new BooleanSetting(true)) {
			@Override public void onChange(Setting new_value) { }
		});
	}
	
	public static boolean as_boolean(String setting) { return ((BooleanSetting) get(setting)).value; }
	public static int 	  as_integer(String setting) { return ((RangedIntegerSetting) get(setting)).current; }

	public static boolean use_vsync() 						{ return as_boolean("use_vsync"); }
	public static int 	  font_size() 						{ return as_integer("font_size"); }
	public static boolean auto_reload_theme() 				{ return as_boolean("auto_reload_theme"); }
	public static boolean skip_broken_songs()   			{ return as_boolean("skip_broken_songs"); }
	public static int 	  previous_song_buffer_threshold()  { return as_integer("previous_song_buffer_threshold"); }
	public static boolean use_logarithmic_volume()   		{ return as_boolean("use_logarithmic_volume"); }

	public static float   volume() {
		// TODO Auto-generated method stub
		return 1;
	}

}
