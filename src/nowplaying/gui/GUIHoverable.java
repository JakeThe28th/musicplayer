package nowplaying.gui;

import snowui.GUIInstance;
import snowui.coss.enums.PredicateKey;
import snowui.elements.abstracts.GUIElement;

public class GUIHoverable extends GUIElement {
	
	GUIElement root;
	public GUIElement root() { return root; }
	
	{ identifier("hoverable"); }
	
	public GUIHoverable(GUIElement root) {
		this.root = root;
		this.registerSubElement(root);
		this.root.set(PredicateKey.DISABLED, true);
	}

	@Override
	public void recalculateSize(GUIInstance gui) {
		this.unpadded_width = root.width();
		this.unpadded_height = root.height();
	}

	@Override
	public void updateDrawInfo(GUIInstance gui) {
		root.limit_rectangle(this.aligned_limit_rectangle());
		this.hover_rectangle(this.aligned_limit_rectangle());
	}

	@Override
	public void draw(GUIInstance gui, int depth) {
		if (style().base_color().color().w >= 0.01f) {
			gui.canvas().color(style().base_color().color());
			gui.canvas().rect(this.hover_rectangle(), depth);
		}
	}

}
