package musicplayer.components.settings.types;

import org.joml.Vector4f;

public class ColorSetting implements Setting {
	
	public Vector4f value;

	public ColorSetting(Vector4f value) {
		this.value = value;
	}
	
	public ColorSetting(String value) {
		deserialize(value);
	}
	
	@Override public String 	serialize() 	{ 
		if (value.w == 1) {
			return toHex(value); 
		} else {
			return toRGBA(value);
		}
	}
	@Override public String 	type() 			{ return "color"; }
	@Override public Setting 	deserialize(String serialized) {
		if (serialized.startsWith("#")) {
			this.value = readHex(serialized);
		}
		if (serialized.startsWith("css_rgba(")) {
			this.value = readRGBA(serialized);
		}
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
	
	public static Vector4f readRGBA(String rgba) {
		String values = rgba.substring(rgba.indexOf('(')+1, rgba.length()-1);
		String[] split = values.split(",");
		return new Vector4f(
				Float.parseFloat(split[0].strip())/255f,
				Float.parseFloat(split[1].strip())/255f,
				Float.parseFloat(split[2].strip())/255f,
				Float.parseFloat(split[3].strip())
				);
	}
	
	public static String toRGBA(Vector4f value) {
		return "css_rgba(" 
				+ (int) (value.x * 255) + ", "
				+ (int) (value.y * 255) + ", "
				+ (int) (value.z * 255) + ", "
				+ value.w
				+ ")";
				
	}

}
