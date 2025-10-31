package musicplayer.components.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL40;

import musicplayer.MainProgram;
import musicplayer.components.settings.interfaces.SettingChangeCallback;
import musicplayer.components.settings.types.BooleanSetting;
import musicplayer.components.settings.types.ColorSetting;
import musicplayer.components.settings.types.FloatSetting;
import musicplayer.components.settings.types.RangedIntegerSetting;
import musicplayer.components.settings.types.Setting;
import musicplayer.components.settings.types.SongControlsLayoutSetting;
import musicplayer.components.settings.types.StringSetting;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Grid;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.parts.Library;
import musicplayer.utility.Log;

public class Settings {
	
	public static final String SETTINGS_FILE = "config.txt";
	
	static HashMap<String, SettingChangeCallback> callbacks = new HashMap<>();
	public static void register(String setting_name, SettingChangeCallback cb) {
		callbacks.put(setting_name, cb);
	}
	
	static HashMap<String, Setting> settings = new HashMap<>();
	static boolean automatically_save = false;

	// -- + Initialization + -- //
	
	static {
		try {
			
		// Set default settings
		automatically_save = false;
		setColor("ACCENT_COLOR", new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 1));
		setColor("LIGHT_COLOR", new Vector4f(63 / 255f, 89 / 255f, 84 / 255f, 1));
		setColor("DARK_COLOR", new Vector4f(24 / 255f, 55 / 255f, 49 / 255f, 1));
		setColor("DARKER_COLOR", new Vector4f(19 / 255f, 40 / 255f, 39 / 255f, 1));
		setColor("DARKEST_COLOR", new Vector4f(10 / 255f, 24 / 255f, 23 / 255f, 1));
		setColor("SEMIDARK_COLOR",  new Vector4f(80 / 255f, 100 / 255f, 100 / 255f, 1));
		setColor("TRANSPARENT_ACCENT_COLOR", new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 0.25f));
		
		setColor("FAVORITES_COLOR", new Vector4f(255 / 255f, 177 / 255f, 0, 1f));
		register("FAVORITES_COLOR", (name, value) -> {
			if (Library.is_initialized) G_HomeScreen.update_playlist_views();
		});
		
		
		setBoolean("use_native_window_decorations", true);
		register("use_native_window_decorations", (name, value) -> {
			if (GraphicsAPI.is_initialized()) {
				boolean val = !((BooleanSetting) value).value;
				GraphicsAPI.setDecorated(!val);
				GraphicsAPI.setWindowPinned(val);
				if (!val) GraphicsAPI.mouse_hover_time = 1;
			}
		});
		
		setRangedInteger("font_size", 18, 4, 30);
		register("font_size", (name, value) -> {
			if (GraphicsAPI.is_initialized()) GraphicsAPI.font_size(((RangedIntegerSetting) value).value);
		});
		
		setRangedInteger("icon_size", 20, 4, 30);
		
		setBoolean("use_logarithmic_volume", true);
		
		setFloat("volume", 0.5f);
		
		setBoolean("always_scroll_current_song_title_in_song_controls", true);
		register("always_scroll_current_song_title_in_song_controls", (name, value) -> {
			if (MainProgram.controls != null) MainProgram.controls.setForceScroll(((BooleanSetting) value).value);
		});
		
		setBoolean("always_scroll_current_song_title_in_playlist", false);
		
		setBoolean("skip_broken_songs", true);
		
		setBoolean("playlist_start_at_one", true);

		
		setRangedInteger("unfocused_window_opacity", 65, 0, 100);
		register("unfocused_window_opacity", (name, value) -> {
			GraphicsAPI.setUnfocusedWindowOpacity(unfocused_window_opacity() / 100f);
			});
		setRangedInteger("focused_window_opacity", 95, 0, 100);
		register("focused_window_opacity", (name, value) -> {
			GraphicsAPI.setFocusedWindowOpacity(focused_window_opacity() / 100f);
		});
		
		setBoolean("show_fps", false);
		register("show_fps", (name, value) -> {
			MainProgram.SHOW_FPS = ((BooleanSetting) value).value;
		});
		
		setBoolean("enable_vsync", true);
		register("enable_vsync", (name, value) -> {
			GraphicsAPI.setVsync(((BooleanSetting) value).value);
		});
		
		setRangedInteger("previous_song_buffer_threshold", 5, 0, 30);
		
		setRangedInteger("focused_window_opacity", 95, 0, 100);
		register("focused_window_opacity", (name, value) -> {
			GraphicsAPI.setFocusedWindowOpacity(focused_window_opacity() / 100f);
		});
		
		
		setRangedInteger("max_album_grid_size", 500, 20, 1000);
		register("max_album_grid_size", (name, value) -> {
			G_Grid.MAX_ITEM_SIZE = ((RangedIntegerSetting) value).value;
		});
		
		setRangedInteger("min_album_grid_size", 150, 20, 1000);
		register("min_album_grid_size", (name, value) -> {
			G_Grid.MIN_ITEM_SIZE = ((RangedIntegerSetting) value).value;
		});
		
		setRangedInteger("album_grid_colum_target", 3, 1, 10);
		register("album_grid_colum_target", (name, value) -> {
			G_Grid.TARGET_COLUMN_COUNT = ((RangedIntegerSetting) value).value;
		});
		
		setBoolean("enable_anti_aliasing_requires_restart", true);
		setRangedInteger("anti_aliasing_samples_requires_restart", 4, 1, 16);

		setBoolean("enable_experimental_optimizations_requires_restart", false);
		
		setBoolean("force_song_conrols_buttons_centered", false);
		
		set("song_controls_layout", new SongControlsLayoutSetting());
		register("song_controls_layout", (name, value) -> {
			if (MainProgram.controls != null) MainProgram.controls.makeIconsLayout((SongControlsLayoutSetting) value);
		});
		
		setBoolean("use_heart_as_favorite_icon", false);
		register("use_heart_as_favorite_icon", (name, value) -> {
			if (Library.is_initialized) G_HomeScreen.update_playlist_views();
		});
		
		setBoolean("hide_empty_sections", false);
		register("hide_empty_sections", (name, value) -> {
			if (Library.is_initialized) G_HomeScreen.update_playlist_views();
		});
		
		setBoolean("use_programmer_art_icons", false);
		
		setRangedInteger("bitmap_icon_filtering", 3, 0, 3);
		register("bitmap_icon_filtering", (name, value) -> {
			if (GraphicsAPI.is_initialized()) {
				int filter_mode = GL40.GL_LINEAR;
				switch (((RangedIntegerSetting) value).value) {
					case 0: filter_mode = GL40.GL_NEAREST; break;
					case 1: filter_mode = GL40.GL_LINEAR; break;
					case 2: filter_mode = GL40.GL_LINEAR_MIPMAP_LINEAR; break;
				}
				GraphicsAPI.setIconFilteringMode(filter_mode);
			}
		});
		
		setRangedInteger("bitmap_icon_scale", 175, 1, 300);
		
		// Load settings from disk
		// (overrides but doesn't clear existing settings)
		automatically_save = true;
		load(SETTINGS_FILE);
		
		} catch (Exception e) {
			Log.trace(e);
			System.exit(0);
		}
		
	}
	
	// -- + setting/getting + -- //
	
	public static Vector4f ACCENT_COLOR() { return getColor("ACCENT_COLOR"); }
	public static Vector4f LIGHT_COLOR() { return getColor("LIGHT_COLOR"); }
	public static Vector4f DARK_COLOR() { return getColor("DARK_COLOR"); }
	public static Vector4f DARKER_COLOR() { return getColor("DARKER_COLOR"); }
	public static Vector4f DARKEST_COLOR() { return getColor("DARKEST_COLOR"); }
	public static Vector4f SEMIDARK_COLOR() { return getColor("SEMIDARK_COLOR"); }
	public static Vector4f TRANSPARENT_ACCENT_COLOR() { return getColor("TRANSPARENT_ACCENT_COLOR"); }
	
	public static Vector4f FAVORITES_COLOR() { return getColor("FAVORITES_COLOR"); }

	
	public static boolean use_native_window_decorations() {
		return getBoolean("use_native_window_decorations");
	}
	
	public static void use_native_window_decorations(boolean b) {
		setBoolean("use_native_window_decorations", b);
	}
	
	
	public static int font_size() {
		return getInt("font_size");
	}
	
	public static int icon_size() {
		return getInt("icon_size");
	}
	
	public static boolean use_logarithmic_volume() {
		return getBoolean("use_logarithmic_volume");
	}
	
	public static void volume(float v) {
		setFloat("volume", v);
	}
	
	public static float volume() {
		return getFloat("volume");
	}
	
	public static boolean always_scroll_current_song_title_in_playlist() {
		return getBoolean("always_scroll_current_song_title_in_playlist");
	}
	
	public static boolean always_scroll_current_song_title_in_song_controls() {
		return getBoolean("always_scroll_current_song_title_in_song_controls");
	}
	
	public static boolean skip_broken_songs() {
		return getBoolean("skip_broken_songs");
	}
	
	public static boolean playlist_start_at_one() {
		return getBoolean("playlist_start_at_one");
	}
	
	public static int focused_window_opacity() {
		return getInt("focused_window_opacity");
	}
	
	public static int unfocused_window_opacity() {
		return getInt("unfocused_window_opacity");
	}
	
	public static boolean show_fps() {
		return getBoolean("show_fps");
	}
	
	public static boolean enable_vsync() {
		return getBoolean("enable_vsync");
	}
	
	public static int previous_song_buffer_threshold() {
		return getInt("previous_song_buffer_threshold");
	}
	
	public static int max_album_grid_size() {
		return getInt("max_album_grid_size");
	}
	
	public static int min_album_grid_size() {
		return getInt("min_album_grid_size");
	}
	
	public static int album_grid_colum_target() {
		return getInt("album_grid_colum_target");
	}

	public static boolean enable_anti_aliasing_requires_restart() {
		return getBoolean("enable_anti_aliasing_requires_restart");
	}
	
	public static int anti_aliasing_samples_requires_restart() {
		return getInt("anti_aliasing_samples_requires_restart");
	}
	
	public static boolean force_song_conrols_buttons_centered() {
		return getBoolean("force_song_conrols_buttons_centered");
	}
	
	public static SongControlsLayoutSetting song_controls_layout() {
		return ((SongControlsLayoutSetting) get("song_controls_layout"));
	}
	
	public static void song_controls_layout(SongControlsLayoutSetting setting) {
		set("song_controls_layout", setting);
	}
	
	public static boolean use_heart_as_favorite_icon() {
		return getBoolean("use_heart_as_favorite_icon");
	}
	
	public static boolean hide_empty_sections() {
		return getBoolean("hide_empty_sections");
	}
	
	public static boolean use_programmer_art_icons() {
		return getBoolean("use_programmer_art_icons");
	}
	
	public static float bitmap_icon_scale() {
		return getInt("bitmap_icon_scale") / 100f;
	}
	
	// -- + setting/getting + -- //
		
	// -- Setters -- //
	
	public static void set(String key, Setting value) {
		settings.put(key, value);
		if (callbacks.containsKey(key)) {
			callbacks.get(key).onChange(key, value);
		}
		if (automatically_save) save(SETTINGS_FILE, settings);
	}
	
	public static void setColor(String key, String value) {
		set(key, new ColorSetting(value));
	}
	
	public static void setColor(String key, Vector4f value) {
		set(key, new ColorSetting(value));
	}
	
	public static void setString(String key, String value) {
		set(key, new StringSetting(value));
	}
	
	public static void setBoolean(String key, boolean value) {
		set(key, new BooleanSetting(value));
		G_SettingsScreen.INSTANCE.reload();
	}
	
	public static void setRangedInteger(String key, int value, int min, int max) {
		set(key, new RangedIntegerSetting(value, min, max));
	}
	
	public static void setRangedInteger(String key, RangedIntegerSetting setting) {
		set(key, setting);
	}
	
	public static void setFloat(String key, float value) {
		set(key, new FloatSetting(value));
	}
	
	// -- Getters -- //
	
	public static Setting get(String key) {
		return settings.get(key);
	}
	
	public static String getString(String key) {
		if (!settings.containsKey(key)) return null;
		return ((StringSetting) get(key)).value;
	}
	
	public static Vector4f getColor(String key) {
		if (!settings.containsKey(key)) return null;
		return ((ColorSetting) get(key)).value;
	}
	
	public static boolean getBoolean(String key) {
		if (!settings.containsKey(key)) return false;
		return ((BooleanSetting) get(key)).value;
	}
	
	public static RangedIntegerSetting getRangedInteger(String key) {
		if (!settings.containsKey(key)) return null;
		return ((RangedIntegerSetting) get(key));
	}
	
	public static int getInt(String key) {
		if (!settings.containsKey(key)) return -1;
		if (get(key) instanceof RangedIntegerSetting) {
			return ((RangedIntegerSetting) get(key)).value;
		}
		return -1;
	}
	
	public static float getFloat(String key) {
		if (!settings.containsKey(key)) return Float.NaN;
		return ((FloatSetting) get(key)).value;
	}
	
	// -- + Saving/Loading from disk + -- //
		
	/** Save settings to disk */
	public static void save(String path, HashMap<String, Setting> settings) {
		String serialized = "";
		for (String key : settings.keySet()) {
			serialized += key + "=" + settings.get(key).type() + "(" + Setting.escape(settings.get(key).serialize()) + ")\n";
		}
		try {
			Files.writeString(Paths.get(path), serialized, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			Log.send("Failed to save settings.");
			Log.trace(e);
		}
	}
	
	/** Load settings from disk */
	public static void load(String path) {
		try {
			String serialized = Files.readString(Paths.get(path));
			int index = 0;
			while (index < serialized.length()) {
				// Read until '=' (ex: read 'blah' in 'blah=(foo)';
				String key = "";
				while (index < serialized.length() && serialized.charAt(index) != '=') {
					key += serialized.charAt(index);
					index++;
				}
				index++; // skip '='
				// Read the type (read until '('.)
				String type = "";
				while (index < serialized.length() && serialized.charAt(index) != '(') {
					type += serialized.charAt(index);
					index++;
				}
				// Read until next unescaped closed parentheses (ex: read 'he\(llo\)' in foo=(he\(llo\)).)
				index++; // skip '('
				String value = "";
				while (index < serialized.length() && serialized.charAt(index) != ')') {
					value += serialized.charAt(index);
					index++;
					if (index > 0 && serialized.charAt(index-1) == '\\') {
						value += serialized.charAt(index);
						index++;
					}
				}
				index++; // skip ')'
				// Read until next non-whitespace
				while (index < serialized.length() && Character.isWhitespace(serialized.charAt(index))) {
					index++;
				}
				// Convert key/value into actual setting object
				//Log.send(type, key, value);
				switch (type) {
					case "string": set(key, new StringSetting(Setting.unescape(value))); break;
					case "color": set(key, new ColorSetting(Setting.unescape(value))); break;
					case "boolean": set(key, new BooleanSetting(Setting.unescape(value))); break;
					case "ranged_integer": set(key, new RangedIntegerSetting(Setting.unescape(value))); break;
					case "float": set(key, new FloatSetting(Setting.unescape(value))); break;
					case "song_controls_layout": set(key, new SongControlsLayoutSetting(Setting.unescape(value))); break;
				}
			}
		} catch (IOException e) {
			Log.send("Failed to read settings.");
			Log.trace(e);
		}
	}
	public static HashMap<String, Setting> onlyColorSettings() {
		HashMap<String, Setting> ret = new HashMap<>();
		for (String key : settings.keySet()) {
			if (settings.get(key) instanceof ColorSetting) ret.put(key, settings.get(key));
		}
		return ret;
	}
	
}
