package musicplayer.components.settings.types;

public class FloatSetting implements Setting {
	
	public float value;
	
	public FloatSetting(float v) {
		value = v;
	}
	
	public FloatSetting(String serialized) {
		this(Float.parseFloat(serialized));
	}

	@Override public String type() { return "float"; }

	@Override
	public String serialize() {
		return value + "";
	}

	@Override
	public Setting deserialize(String serialized) {
		return new FloatSetting(serialized);
	}

}
