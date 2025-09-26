package musicplayer.gui;

import org.joml.Vector2i;
import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.components.settings.Settings;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_Slider extends G_Element {
		
	// TODO instead of adding top/bottom for vertical probably just swap out x and y  in draw and in mouse check

	@Override
	public void recalculate_size() {
		this.unpadded_height = 8;
	}
	
	Vector4f slider_color = Settings.ACCENT_COLOR();
	
	int left 		= 0;
	int right 		= 0;
	int y 			= 0;
	int thickness 	= 2;
	int draw_amount = 0;
	
	int dot_size	= 7;
	
	public double amount = 0.5;
	
	boolean dragging = false;
	boolean bounded = true;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		this.left = left + this.left_margin;
		this.right = right - this.right_margin;
		if (this.left > right) this.left = right;
		if (this.right < left) this.right = left;
		this.y = top + ((bottom-top) / 2);
		this.draw_amount = (int) (amount * (this.right - this.left));

		//amount = (Math.sin(System.currentTimeMillis()/1000.0) + 1) / 2 ;
		// int cx = left + draw_amount;
		hover_rectangle = new Rectangle(this.left, y-dot_size, this.right, y+dot_size);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(left, y-thickness, right, y+thickness, depth);
		GraphicsAPI.color(slider_color);
		GraphicsAPI.rect(left, y-thickness, left + draw_amount, y+thickness, depth);
		GraphicsAPI.dot(left + draw_amount, y, depth, dot_size);
		
		// Draw number when hovering
		if (dragging || hover_rectangle.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
			GraphicsAPI.color(GraphicsAPI.TRANSLUCENT_BLACK);
			String text = amountFormatted();
			Vector2i size = GraphicsAPI.size(text);
			int width = size.x+10;
			Rectangle hoverpreview = new Rectangle(
					(left + draw_amount) - (width/2),
					y-(size.y()+10),
					(left + draw_amount) + (width/2),
					y
					);
			GraphicsAPI.rect(hoverpreview, depth+20);
			GraphicsAPI.color(GraphicsAPI.WHITE);
			GraphicsAPI.text(hoverpreview.left()+5, hoverpreview.top()+5, depth+30, text);
		}
	}
	
	protected String amountFormatted() {
		return String.format("%.2f", amount);
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
