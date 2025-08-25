package musicplayer.gui;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Rectangle;

public class G_TypingBox extends G_Element {
	
	
	G_Text text = new G_Text();
	
	{
		text.text = "Hello";
		text.base_color = GraphicsAPI.BLACK;
		addSubElement(text);
	}

	@Override
	public void recalculate_size() {
		text.recalculate_size();
		this.unpadded_height = text.height();
		this.unpadded_width = text.width();
	}
	
	Rectangle background = new Rectangle(0,0,0,0);

	@Override
	public void layout(int left, int top, int right, int bottom) {

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		background = new Rectangle(left, top, right, bottom);
		
		text.layout(left, top, right, bottom);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(background, depth);
		text.draw(depth+2);
	}

}
