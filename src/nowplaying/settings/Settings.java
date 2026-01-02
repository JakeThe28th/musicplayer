package nowplaying.settings;

import java.util.HashMap;

import nowplaying.NowPlayingMain;
import nowplaying.settings.data.Setting;
import nowplaying.settings.data.SettingSlot;
import nowplaying.settings.data.types.BooleanSetting;

public class Settings {
	
	public static HashMap<String, SettingSlot> settings = new HashMap<>();
	public static HashMap<String, String> friendly_names = new HashMap<>();

	public static Setting 	get(String name) 				{ return settings.get(name).value(); 			 }
	public static void 		set(String name, Setting value) { 		 settings.get(name).defined_value(value);}
	
	public static String	getname(String name) 			{ return friendly_names.get(name);				 }
	public static void 	    setname(String name, String f)  { 	     friendly_names.put(name, f); 			 }

	static {
		settings.put("use_vsync", new SettingSlot(new BooleanSetting(true)) {
			@Override public void onChange(Setting new_value) {
				NowPlayingMain.window.setVsync(((BooleanSetting) new_value).value);
			}
		});
		setname("use_vsync", "Enable Vsync");
	}

}
