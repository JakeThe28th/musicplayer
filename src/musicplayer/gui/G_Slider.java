package musicplayer.gui;

import java.util.ArrayList;

import org.joml.Vector4f;

import musicplayer.MusicPlayer;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Rectangle;

public class G_Slider extends G_Element {
	
	@Override public ArrayList<G_Element> sub_elements() { return G_Element.EMPTY; }
	
	// TODO instead of adding top/bottom for vertical probably just swap out x and y  in draw and in mouse check

	@Override
	public void recalculate_size() {
		this.unpadded_height = 8;
	}
	
	Vector4f slider_color = MusicPlayer.ACCENT_COLOR;
	
	int left 		= 0;
	int right 		= 0;
	int y 			= 0;
	int thickness 	= 2;
	int draw_amount = 0;
	
	int dot_size	= 7;
	
	double amount = 0.5;
	
	boolean dragging = false;
	boolean bounded = true;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		this.left = left + this.left_margin;
		this.right = right - this.right_margin;
		this.y = top + ((bottom-top) / 2);
		this.draw_amount = (int) (amount * (this.right - this.left));

		//amount = (Math.sin(System.currentTimeMillis()/1000.0) + 1) / 2 ;
		int cx = left + draw_amount;
		hover_rectangle = new Rectangle(this.left, y-dot_size, this.right, y+dot_size);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(left, y-thickness, right, y+thickness, depth);
		GraphicsAPI.color(slider_color);
		GraphicsAPI.rect(left, y-thickness, left + draw_amount, y+thickness, depth);
		GraphicsAPI.dot(left + draw_amount, y, depth, dot_size);
	}
	
	@Override
	public boolean input() {
		if (hover_rectangle.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
			GraphicsAPI.color(GraphicsAPI.TRANSPARENT_WHITE);
			if (GraphicsAPI.left_click_pressed()) { dragging = true; onDrag(amount); return true; }
			GraphicsAPI.rect(hover_rectangle, 0);
		}
		if (dragging) {
			amount = (GraphicsAPI.mouseX()-left) / ((float) (right-left)); 
			if (bounded) {
				if (amount > 1) amount = 1;
				if (amount < 0) amount = 0;
			}
			onDrag(amount);
			if (GraphicsAPI.left_click_released()) { dragging = false; }
			return true;
		}
		return false;
	}
	
	public void onDrag(double new_value) { }

}
