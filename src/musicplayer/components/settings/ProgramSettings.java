package musicplayer.components.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import musicplayer.MainProgram;
import musicplayer.components.settings.types.Setting;
import musicplayer.components.settings.types.StringSetting;
import musicplayer.extensions.Extension;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.utility.Log;

public class ProgramSettings extends Extension  {
	
	public static final String SETTINGS_FILE = "config.txt";
	
	static HashMap<String, Setting> settings = new HashMap<>();
	
	public static void set(String key, Setting value) {
		settings.put(key, value);
		save();
	}
	
	public static void set(String key, String value) {
		set(key, new StringSetting(value));
		save();
	}
	
	public static String getString(String key) {
		if (!settings.containsKey(key)) return null;
		return ((StringSetting) settings.get(key)).value;
	}
	
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
				}
			}
		} catch (IOException e) {
			Log.send("Failed to read settings.");
			Log.trace(e);
		}
	}
	
	// -- //
	
	G_SettingsScreen settings_screen = G_SettingsScreen.INSTANCE;

	@Override public String   identifier() 		{ return "builtin;settings"; }

	@Override
	public void onLoad() throws IOException {
		load();
		Log.send(getString("test"));
		Log.send(getString("blag"));
		set("test", "foobar");
		set("blag", "gootar(?)");

		MainProgram.registerScreen(settings_screen);
		G_HomeScreen.addMenuOption(new Option(
				"Settings",
				() -> { MainProgram.change_screen(settings_screen.identifier()); }
				));
	}
	
	@Override
	public void onTick() {
		
	}

}
