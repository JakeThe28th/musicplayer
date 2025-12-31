package nowplaying.settings.types;

import nowplaying.settings.Setting;

public class BooleanSetting implements Setting {
	
	public boolean value;

	public BooleanSetting(boolean value) { this.value = value; }

	@Override
	public String serialize() { return String.valueOf(value); }

	@Override
	public void deserialize(String serialized) { value = serialized.equals("true"); }

}
