package musicplayer.components.settings.types;

import org.joml.Vector4f;

public class ColorSetting implements Setting {
	
	public Vector4f value;

	public ColorSetting(Vector4f value) {
		this.value = value;
	}
	
	public ColorSetting(String value) {
		this.value = readHex(value);
	}
	
	@Override public String 	serialize() 	{ return toHex(value); }
	@Override public String 	type() 			{ return "color"; }
	@Override public Setting 	deserialize(String serialized) {
		this.value = readHex(serialized);
		return this;
	}
	
	public static Vector4f readHex(String hex) {
		return new Vector4f(  
				Integer.valueOf( hex.substring( 1, 3 ), 16 ) / 255f,
				Integer.valueOf( hex.substring( 3, 5 ), 16 ) / 255f,
				Integer.valueOf( hex.substring( 5, 7 ), 16 ) / 255f,
				1);
	}
	
	public static String toHex(Vector4f value) {
		return "#" 
				+ String.format("%02x", (int) (value.x * 255))
				+ String.format("%02x", (int) (value.y * 255))
				+ String.format("%02x", (int) (value.z * 255));
				
	}

}
