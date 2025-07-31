package musicplayer.gui;

import java.util.ArrayList;

public class G_ScrollableList {
	
	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	
	int scroll = 0;
	
	public static final int ITEM_HEIGHT = 10;
	public static final int SCROLL_BAR_WIDTH = 10;

	
	public void draw(int left, int top, int right, int bottom, int depth) {
		GraphicsHandler.push_scissor(left, top, right, bottom);
		// Draw list
		int yy = scroll;
		for (G_Element element : elements) {
			element.draw(left+SCROLL_BAR_WIDTH, top+yy, right-SCROLL_BAR_WIDTH, top+yy+ITEM_HEIGHT, depth + 1);
			yy+=ITEM_HEIGHT;
		}
		// Draw scrollbar
		// TODO
		GraphicsHandler.pop_scissor();
	}

	public void add(G_Element element) { elements.add(element); }

}
