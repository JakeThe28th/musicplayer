package musicplayer.gui;

import org.joml.Vector4f;

import musicplayer.graphics.API;

public class G_Slider extends G_Element {

	@Override
	public void recalculate_size() {
		// TODO Auto-generated method stub
	}
	
	Vector4f slider_color = new Vector4f(67 / 255f, 194 / 255f, 168 / 255f, 1);
	
	int left 		= 0;
	int right 		= 0;
	int y 			= 0;
	int thickness 	= 2;
	int draw_amount = 0;
	
	double amount = 0.5;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		this.left = left + this.left_margin;
		this.right = right - this.right_margin;
		this.y = top + ((bottom-top) / 2);
		this.draw_amount = (int) (amount * (this.right - this.left));

		amount = (Math.sin(System.currentTimeMillis()/1000.0) + 1) / 2 ;
	}

	@Override
	public void draw(int depth) {
		API.color(base_color);
		API.rect(left, y-thickness, right, y+thickness, depth);
		API.color(slider_color);
		API.rect(left, y-thickness, left + draw_amount, y+thickness, depth);
		API.dot(left + draw_amount, y, depth, 7);
	}

}
