package musicplayer.components.settings.types;

import java.util.regex.Pattern;

public class RangedIntegerSetting implements Setting {
	
	public int value;
	
	public int minimum;
	public int maximum;
	
	public RangedIntegerSetting(int value, int min, int max) {
		this.value = value;
		this.minimum = min;
		this.maximum = max;
	}
	public RangedIntegerSetting(String serialized) {
		String[] split = serialized.split(Pattern.quote("|"));
		
		this.value   = Integer.parseInt(split[0]);
		this.minimum = Integer.parseInt(split[1]);
		this.maximum = Integer.parseInt(split[2]);
	}
	
	@Override public String type() { return "ranged_integer"; }

	@Override
	public Setting deserialize(String serialized) {
		return new RangedIntegerSetting(serialized);
	}

	@Override
	public String serialize() {
		return value + "|" + minimum + "|" + maximum;
	}
	
	public double percentage() {
		double range = maximum - minimum;
		double compensated = value - minimum;
		return compensated / range;
	}

	public void percentage(double newv) {
		float range = maximum - minimum;
		this.value = (int) (minimum + (newv * range));
	}
	
}
