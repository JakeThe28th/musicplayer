package musicplayer.components.settings.types;

public class BooleanSetting implements Setting {
	
	public boolean value;

	public BooleanSetting(String value) {
		deserialize(value);
	}

	public BooleanSetting(boolean value) {
		this.value = value;
	}
	
	@Override public String 	serialize() 	{ return value + ""; }
	@Override public String 	type() 			{ return "boolean"; }
	@Override public Setting 	deserialize(String serialized) {
		this.value = serialized.equals(true+"");
		return this;
	}

}
