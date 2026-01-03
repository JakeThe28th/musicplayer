package nowplaying.settings.data.enums;

public enum SettingCategory {
	MISC("Miscellaneous"),
	THEME("Theme"),
	APPEARANCE("Appearance"),
	BEHAVIOR("Functionality"),
	PERFORMANCE("Performance"),
	;
	String friendly_name;
	SettingCategory(String name) { friendly_name = name; }
	public String friendlyname() { return friendly_name; }
}
