package musicplayer.components.settings.types;

public class StringSetting implements Setting {
	
	public String value;

	public StringSetting(String value) {
		this.value = value;
	}
	
	@Override public String 	serialize() 	{ return value   ; }
	@Override public String 	type() 			{ return "string"; }
	@Override public Setting 	deserialize(String serialized) {
		this.value = serialized;
		return this;
	}

}
