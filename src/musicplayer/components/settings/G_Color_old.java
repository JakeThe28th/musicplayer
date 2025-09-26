package musicplayer.components.settings;

import org.joml.Vector4f;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Slider;
import musicplayer.gui.G_Text;
import musicplayer.utility.Rectangle;

public class G_Color_old extends G_Element {

	G_Slider 	red 		= new G_Slider();
	G_Slider 	green 		= new G_Slider();
	G_Slider 	blue 		= new G_Slider();
	G_Text 		red_text 	= new G_Text("Red");
	G_Text 		green_text 	= new G_Text("Green");
	G_Text 		blue_text 	= new G_Text("Blue");
	
	public static final int SLIDER_HEIGHT = 30;
	
	{ addSubElement(red); addSubElement(green); addSubElement(blue); 
	  addSubElement(red_text); addSubElement(green_text); addSubElement(blue_text); }

	@Override
	protected void i_recalculate_size() {
		for (G_Element e : sub_elements) {
			e.recalculate_size();
		}
		this.unpadded_height = SLIDER_HEIGHT * 5;
	}
	
	Rectangle color_rect;

	@Override
	protected void i_layout(int left, int top, int right, int bottom) {
		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;

		int yy = top;
		int x_offset = GraphicsAPI.size("Green").x + 10;
		
		yy+=SLIDER_HEIGHT;
		red.layout(left + x_offset, yy, right, yy+SLIDER_HEIGHT);
		red_text.layout(left, yy, left + x_offset, yy+SLIDER_HEIGHT);

		yy+=SLIDER_HEIGHT;
		green.layout(left + x_offset, yy, right, yy+SLIDER_HEIGHT);
		green_text.layout(left, yy, left + x_offset, yy+SLIDER_HEIGHT);

		yy+=SLIDER_HEIGHT;
		blue.layout(left + x_offset, yy, right, yy+SLIDER_HEIGHT);
		blue_text.layout(left, yy, left + x_offset, yy+SLIDER_HEIGHT);
		
		yy+=SLIDER_HEIGHT;
		color_rect = new Rectangle(left, yy, right, bottom);
		
	}

	@Override
	public void draw(int depth) {
		for (G_Element e : sub_elements) {
			e.draw(depth + 1);
		}
		
		GraphicsAPI.color(new Vector4f(
				((float) red.amount),
				((float) green.amount),
				((float) blue.amount),
				1));
		GraphicsAPI.rect(color_rect, depth + 1);
	}
	
}