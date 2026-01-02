package nowplaying.gui.abstracts;

import snowui.GUIInstance;
import snowui.elements.abstracts.GUIElement;

public abstract class GUISizelessElement extends GUIElement {
	
	@Override
	public void recalculateSize(GUIInstance gui) {
		this.unpadded_height = 3;
		this.unpadded_width = 3;
	}

}
