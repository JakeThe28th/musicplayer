package musicplayer.components.settings.types;

import java.util.regex.Pattern;

public interface Setting {
	public String serialize();
	public String type();
	public Setting deserialize(String serialized);
	
	public static String escape(String unfiltered) {
		return unfiltered.replaceAll(Pattern.quote("("), "\\\\(").replaceAll(Pattern.quote(")"), "\\\\)");
	}
	public static String unescape(String filtered) {
		return filtered.replaceAll(Pattern.quote("\\("), "(").replaceAll(Pattern.quote("\\)"), ")");
	}
	
}