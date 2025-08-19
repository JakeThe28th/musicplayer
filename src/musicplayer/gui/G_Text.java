package musicplayer.gui;

import org.joml.Vector2i;

import musicplayer.graphics.API;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Log;

public class G_Text extends G_Element {
	
	String 	text 	= "Unset Text";
	int 	x 		= 0;
	int 	y 		= 0;
	
	public G_Text text(String new_text) { text = new_text; recalculate_size(); return this; }

	@Override
	public void recalculate_size() {
		Vector2i size = API.size(text);
		this.unpadded_width = size.x;
		this.unpadded_height = size.y;
	}
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		int xoffset = 0;

		if (this.horizontal_align == Alignment.MIDDLE) {
			xoffset = ((right-left) - width()) / 2;
		}
		
		if (this.horizontal_align == Alignment.RIGHT) {
			xoffset = ((right-left) - width());
		}
		
		x = left + left_margin + xoffset;
		y = top + top_margin;
		
	}

	@Override
	public void draw(int depth) {
		API.color(base_color);
		API.text(x, y, depth, text);
	}


}
