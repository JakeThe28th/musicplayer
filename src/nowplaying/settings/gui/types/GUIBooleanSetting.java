package nowplaying.settings.gui.types;

import frost3d.utility.Rectangle;
import nowplaying.gui.abstracts.GUISizelessElement;
import snowui.GUIInstance;
import snowui.coss.enums.PredicateKey;

public class GUIBooleanSetting extends GUISizelessElement {
	
	{ identifier("setting_boolean"); }
	
	public GUIBooleanSetting(boolean v) {
		this.set(PredicateKey.SELECTED, v);
	}

	Rectangle indicator;
	
	@Override
	public void updateDrawInfo(GUIInstance gui) {
		Rectangle b = this.padded_limit_rectangle();
		this.hover_rectangle(b);
		indicator = b.thin_horizontally((float) (1f/3f));
	}

	@Override
	public void draw(GUIInstance gui, int depth) {
		gui.canvas().color(style().base_color().color());
		gui.canvas().rect(indicator, depth);
	}
	
	@Override public void onSingleClick() {
		boolean enabled = !get(PredicateKey.SELECTED);
		this.set(PredicateKey.SELECTED, enabled);
		onChangeValue(enabled);
	}

	public void onChangeValue(boolean b) { }

}
