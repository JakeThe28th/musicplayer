package musicplayer.components.settings.interfaces;

import musicplayer.components.settings.types.Setting;

public interface SettingChangeCallback {
	
	public void onChange(String setting_name, Setting new_value);

}
