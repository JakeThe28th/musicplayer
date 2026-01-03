package nowplaying.settings.data.types;

import nowplaying.settings.data.Setting;

public class RangedIntegerSetting implements Setting {
	
	public int minimum;
	public int maximum;
	public int current;

	public RangedIntegerSetting(int val, int min, int max) { 
		minimum = min;
		maximum = max;
		current = val;
	}

	@Override
	public String serialize() { return String.valueOf(current); }

	@Override
	public RangedIntegerSetting deserialize(String serialized) { 
		return new RangedIntegerSetting(Integer.valueOf(serialized), minimum, maximum);
	}

	public float percent() {
		return (current - minimum) / ((float) (maximum-minimum));
	}

	public Setting copyWithPercent(float v) {
		return new RangedIntegerSetting(fromPercent(v), minimum, maximum);
	}

	public int fromPercent(float v) {
		return (int) (minimum + (v * (maximum-minimum)));
	}

}