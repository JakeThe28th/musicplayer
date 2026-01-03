package nowplaying.settings.data.types;

import nowplaying.settings.data.Setting;

public class BooleanSetting implements Setting {
	
	public boolean value;

	public BooleanSetting(boolean value) { this.value = value; }

	@Override
	public String serialize() { return String.valueOf(value); }

	@Override
	public BooleanSetting deserialize(String serialized) { return new BooleanSetting(serialized.equals("true")); }

}
