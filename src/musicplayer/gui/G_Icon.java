package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Rectangle;

public class G_Icon extends G_Element {
	
	@Override public ArrayList<G_Element> sub_elements() { return G_Element.EMPTY; }
	
	public G_Icon(String name) {
		icon_name = name;
	}
	
	String icon_name = "stop";
	int icon_size = 20;

	@Override
	public void recalculate_size() {
		unpadded_width = icon_size;
		unpadded_height = icon_size;
	}
	
	int x = 0;
	int y = 0;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		x = left + left_margin;
		y = top + top_margin;
		hover_rectangle = new Rectangle(left, top, left+width(), top+height());
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.icon(x, y, depth, icon_name, icon_size);
	}
	
}
