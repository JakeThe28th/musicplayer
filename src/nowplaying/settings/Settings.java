package nowplaying.settings;

import java.util.HashMap;

import nowplaying.NowPlayingMain;
import nowplaying.settings.types.BooleanSetting;

public class Settings {
	
	static abstract class SettingSlot {
		Setting 				default_value;
		Setting 				defined_value;
		public Setting value() {
			if (defined_value == null) return default_value;
			return defined_value;
		}
		public void defined_value(Setting value) {
			this.defined_value = value;
			this.onChange(value);
		}
		public SettingSlot(Setting default_value) {
			this.default_value = default_value;
		}
		public abstract void onChange(Setting new_value);
	}
	
	static HashMap<String, SettingSlot> settings = new HashMap<>();

	public static Setting 	get(String name) 				{ return settings.get(name).value(); 			  }
	public static void 		set(String name, Setting value) { 		 settings.get(name).defined_value(value);}
	
	static {
		settings.put("use_vsync", new SettingSlot(new BooleanSetting(true)) {
			@Override public void onChange(Setting new_value) {
				NowPlayingMain.window.setVsync(((BooleanSetting) new_value).value);
			}
		});
	}

}
