package musicplayer.gui;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Rectangle;

public class G_Icon extends G_Element {
		
	public G_Icon(String name) {
		icon_name = name;
	}
	
	public String icon_name = "stop";
	int icon_size = 20;

	@Override
	public void recalculate_size() {
		unpadded_width = icon_size;
		unpadded_height = icon_size;
	}
	
	protected int x = 0;
	protected int y = 0;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		int xoffset = GUIUtility.getAlignmentOffset(left, right, width(), horizontal_align);
		x = left + left_margin + xoffset;
		int yoffset = GUIUtility.getAlignmentOffset(top, bottom, height(), vertical_align);
		y = top + top_margin + yoffset;
		hover_rectangle = new Rectangle(left+xoffset, top+yoffset, left+width()+xoffset, top+height()+yoffset);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.icon(x, y, depth, icon_name, icon_size);
	}
	
}
