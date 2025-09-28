package musicplayer.components.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.components.settings.interfaces.SettingChangeCallback;
import musicplayer.components.settings.types.BooleanSetting;
import musicplayer.components.settings.types.ColorSetting;
import musicplayer.components.settings.types.FloatSetting;
import musicplayer.components.settings.types.RangedIntegerSetting;
import musicplayer.components.settings.types.Setting;
import musicplayer.components.settings.types.StringSetting;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Log;

public class Settings {
	
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

		setBoolean("use_native_window_decorations", true);
		register("use_native_window_decorations", (name, value) -> {
			if (GraphicsAPI.is_initialized()) {
				boolean val = !((BooleanSetting) value).value;
				GraphicsAPI.setDecorated(!val);
				GraphicsAPI.setWindowPinned(val);
				if (val) {
					GraphicsAPI.setUnfocusedWindowOpacity(0.65f);
					GraphicsAPI.setFocusedWindowOpacity(0.95f);
				} else {
					GraphicsAPI.mouse_hover_time = 1;
					GraphicsAPI.setUnfocusedWindowOpacity(1);
					GraphicsAPI.setFocusedWindowOpacity(1);
				}
			}
		});
		
		setRangedInteger("font_size", 18, 4, 30);
		register("font_size", (name, value) -> {
			if (GraphicsAPI.is_initialized()) GraphicsAPI.font_size(((RangedIntegerSetting) value).value);
		});
		
		setRangedInteger("icon_size", 20, 4, 30);
		
		setBoolean("use_logarithmic_volume", true);
		
		setFloat("volume", 0.5f);
		
		setBoolean("always_scroll_current_song_title", true);
		register("always_scroll_current_song_title", (name, value) -> {
			if (MainProgram.controls != null) MainProgram.controls.setForceScroll(((BooleanSetting) value).value);
		});
		
		// Load settings from disk
		// (overrides but doesn't clear existing settings)
		automatically_save = true;
		load();
		
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
	
	public static boolean always_scroll_current_song_title() {
		return getBoolean("always_scroll_current_song_title");
	}
	
	// -- + setting/getting + -- //
		
	// -- Setters -- //
	
	public static void set(String key, Setting value) {
		settings.put(key, value);
		if (callbacks.containsKey(key)) {
			callbacks.get(key).onChange(key, value);
		}
		if (automatically_save) save();
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
	
	public static final String SETTINGS_FILE = "config.txt";
	
	/** Save settings to disk */
	public static void save() {
		String serialized = "";
		for (String key : settings.keySet()) {
			serialized += key + "=" + settings.get(key).type() + "(" + Setting.escape(settings.get(key).serialize()) + ")\n";
		}
		try {
			Files.writeString(Paths.get(SETTINGS_FILE), serialized, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			Log.send("Failed to save settings.");
			Log.trace(e);
		}
	}
	
	/** Load settings from disk */
	public static void load() {
		try {
			String serialized = Files.readString(Paths.get(SETTINGS_FILE));
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
				}
			}
		} catch (IOException e) {
			Log.send("Failed to read settings.");
			Log.trace(e);
		}
	}
	
}
