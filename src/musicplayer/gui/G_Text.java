package musicplayer.gui;

import org.joml.Vector2i;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_Text extends G_Element {
		
	String 	text 		= "Unset Text";
	String  text_concat = "Unset T...";
	int 	x 			= 0;
	int 	y 			= 0;
	
	boolean can_wrap = true;
	
	public G_Text text(String new_text) { text = new_text; recalculate_size(); return this; }

	@Override
	public void recalculate_size() {
		Vector2i size = GraphicsAPI.size(text);
		this.unpadded_width = size.x;
		this.unpadded_height = size.y;
	}
	
	Rectangle area;
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		int xoffset = GUIUtility.getAlignmentOffset(left, right, width(), horizontal_align);
		x = left + left_margin + xoffset;
		y = top + top_margin;
		
		if (can_wrap) {
			area = new Rectangle(left, top, right, bottom);
			if (text.length() <= 3) {
				text_concat = text;
			} else {
				text_concat = text;
				while (GraphicsAPI.size(text_concat).x > area.width() && text_concat.length() >= 4) {
					text_concat = text_concat.substring(0, text_concat.length()-1);
				}
				text_concat = text_concat.substring(0, text_concat.length()-3);
				text_concat += "...";
			}
		}
		
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		
		if (can_wrap && ( unpadded_width > area.width() && area.isHovered() ) ) {
			GraphicsAPI.setFadeColumn(area.left(), area.left()+30, area.right(), area.right()-30);
			// scroll text
			int time = (int) Math.floorMod((System.currentTimeMillis() / 20), unpadded_width + 30);
			GraphicsAPI.text(area.left()+time, y, depth, text);
			GraphicsAPI.text((area.left()-(unpadded_width + 30))+time, y, depth, text);
			GraphicsAPI.resetFadeColumn();
		} else if (can_wrap && (unpadded_width > (area.width()))) {
			GraphicsAPI.text(area.left(), y, depth, text_concat);
		} else {
			GraphicsAPI.text(x, y, depth, text);
		}
		
	}

}
