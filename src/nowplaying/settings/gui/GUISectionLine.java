package nowplaying.settings.gui;

import snowui.GUIInstance;
import snowui.elements.abstracts.GUIElement;

public class GUISectionLine extends GUIElement {
	
	{ identifier("section_line"); }

	@Override 
	public void recalculateSize(GUIInstance gui) {
		this.unpadded_height = style().size().pixels();
		this.unpadded_width = style().size().pixels();
	}

	@Override
	public void updateDrawInfo(GUIInstance gui) {
		this.hover_rectangle(this.padded_limit_rectangle());
	}

	@Override
	public void draw(GUIInstance gui, int depth) {
		gui.canvas().color(style().base_color().color());
		gui.canvas().rect(this.padded_limit_rectangle(), depth);
	}

}
