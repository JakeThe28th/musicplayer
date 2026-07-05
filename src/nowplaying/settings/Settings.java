package nowplaying.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import musicplayer.utility.Log;
import nowplaying.NowPlayingMain;
import nowplaying.gui.UI;
import nowplaying.settings.data.Setting;
import nowplaying.settings.data.SettingSlot;
import nowplaying.settings.data.enums.SettingCategory;
import nowplaying.settings.data.types.BooleanSetting;
import nowplaying.settings.data.types.RangedIntegerSetting;
import nowplaying.utility.Utility;

public class Settings {
	
	public static HashMap<String, SettingSlot> 		settings 		= new HashMap<>();
	public static HashMap<String, String> 			friendly_names 	= new HashMap<>();
	public static HashMap<String, SettingCategory> 	categories		= new HashMap<>();

	public static Setting 	get(String key) 				{ return settings.get(key).value(); }
	
	public static void 		set(String key, Setting value)  { 		
		settings.get(key).defined_value(value);
		save_settings(); 
	}
	
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
		put("max_album_grid_size", "[max_album_grid_size]", SettingCategory.APPEARANCE, 
		  new SettingSlot(new RangedIntegerSetting(500, 20, 1000)) {
			@Override public void onChange(Setting new_value) { }
		});
		put("min_album_grid_size", "[min_album_grid_size]", SettingCategory.APPEARANCE, 
		  new SettingSlot(new RangedIntegerSetting(150, 20, 1000)) {
			@Override public void onChange(Setting new_value) { }
		});
		put("album_grid_colum_target", "[album_grid_colum_target]", SettingCategory.APPEARANCE, 
		  new SettingSlot(new RangedIntegerSetting(3, 1, 10)) {
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
	public static int 	  max_album_grid_size() 			{ return as_integer("max_album_grid_size"); }
	public static int 	  min_album_grid_size() 			{ return as_integer("min_album_grid_size"); }
	public static int 	  album_grid_colum_target() 		{ return as_integer("album_grid_colum_target"); }

	public static float   volume() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	// ----- //
	
	public static final String CONFIG_PATH = "settings.txt";
	public static final String CONFIG_PATH_TEMP = "settings_new.txt";

	/** Saves all non-default settings to a file. */
	public static void save_settings() {
		String serialized = "";
			   serialized += "# " + NowPlayingMain.PROGRAM_TITLE + " configuration file.\n";
			   serialized += "# Any comments added to this file will not be saved.\n";
			   serialized += "\n";

		for (String setting_key : settings.keySet()) {
			if (!settings.get(setting_key).is_modified_from_default()) continue;
			SettingCategory category 	= categories		.get(setting_key);
			String 			name 		= friendly_names	.get(setting_key);
			Setting 		value 		= settings			.get(setting_key).value();
			String			type		= value.getClass().getSimpleName();
			String			s_value		= Utility.escape(value.serialize());
			serialized += "# " + category.friendlyname() + " - " + name + " (" + type + ")\n";
			serialized += setting_key + "=" + s_value + "\n";
			serialized += "\n";
		}
		
		try {
			Files.writeString(Paths.get(CONFIG_PATH_TEMP), serialized, StandardOpenOption.CREATE);
			if (Files.exists(Paths.get(CONFIG_PATH))) Files.delete(Paths.get(CONFIG_PATH));
			Files.copy(Paths.get(CONFIG_PATH_TEMP), Paths.get(CONFIG_PATH));
			Files.delete(Paths.get(CONFIG_PATH_TEMP));
		} catch (IOException e) {
			UI.showError("Failed to save settings file. " + e.getMessage());
			Log.trace(e);
		}
		
	}
	
	public static void load_settings() {
		if (!Files.exists(Paths.get(CONFIG_PATH))) return;
		try {
			String[] lines = Files.readString(Paths.get(CONFIG_PATH)) .split("\n");
			
			for (String line : lines) {
				if (line.isBlank()) continue;
				if (line.startsWith("#")) continue;
				String setting_name = line.substring(0, line.indexOf("="));
				String setting_value = line.substring(line.indexOf("=") + 1);
				// (settings from the file are treated like user defined settings, even if they're defaults)
				SettingSlot setting_slot = settings.get(setting_name);
				setting_slot.defined_value(setting_slot.value().deserialize(setting_value));
			}
			
		} catch (IOException e) {
			UI.showError("Failed to read settings file. " + e.getMessage());
			Log.trace(e);
		}
	}

}
