package nowplaying.settings.data;

public abstract class SettingSlot {
	
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