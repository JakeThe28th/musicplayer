package musicplayer.gui;

import musicplayer.graphics.API;

public class G_Icon extends G_Element {
	
	public G_Icon(String name) {
		icon_name = name;
	}
	
	{
//		this.left_margin 	= 5;
//		this.right_margin 	= 5;
//		this.top_margin 	= 5;
//		this.bottom_margin 	= 5;
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
	}

	@Override
	public void draw(int depth) {
		API.color(base_color);
		API.icon(x, y, depth, icon_name, icon_size);
	}

}
