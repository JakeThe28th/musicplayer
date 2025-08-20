package musicplayer.gui;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;
import musicplayer.utility.Utility;

public class G_Scrollable extends G_Element {
	
	public int scroll_target = -1;
	
	public G_Scrollable(G_Element r) {
		root(r);
	}
	
	private G_Element root;
	public void root(G_Element new_root) {
		removeSubElement(root);
		addSubElement(new_root);
		root = new_root;
	}
	
	
	Rectangle scissor_box = null;
	public double scroll_y = 0;
	
	@Override
	public void recalculate_size() {
		root.recalculate_size();
		// TODO Auto-generated method stub
		
	}
	
	public int fixed_sheight;
	public int sheight;

	Rectangle scroll_area;
	Rectangle scroll_bar;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		scissor_box = new Rectangle(left, top, right, bottom);
			  sheight = 	     	  (scissor_box.bottom()-scissor_box.top());
		fixed_sheight = root.height()-(scissor_box.bottom()-scissor_box.top());
		
		left += 25;
		top -= scroll_y;
		
		// Scissor is set so that the sub-element knows
		// to cull stuff during layout, even if nothing is
		// actually drawn yet
		GraphicsAPI.push_scissor(scissor_box);
		root.layout(left, top, right, bottom);
		GraphicsAPI.pop_scissor();
		
		calculate_scrollbar_position();

	}

	int scrollbar_height;
	void calculate_scrollbar_position() {
		
		int hh2 = fixed_sheight;
		if (scroll_y < 0) scroll_y = 0;
		if (scroll_y > hh2) scroll_y = hh2;
		if (scroll_target > hh2) scroll_target = hh2;
		
		if (root.height() <= scissor_box.height() && !allow_lower_align_when_not_full) scroll_y = 0; 
		
		double hh = root.height();
		double real_height = sheight;
		if (hh < real_height) hh = real_height;
		
		int scrollbar_size = (int) ((( real_height ) / hh) * (real_height));
		scrollbar_height = scrollbar_size;
		int scrollbar_offset = (int) ((scroll_y / hh) * ( real_height ));
		scroll_area = new Rectangle(
				scissor_box.left()+5, 
				scissor_box.top()+5, 
				scissor_box.left()+20,
				scissor_box.bottom()-5);
		
		scroll_bar = new Rectangle(
				scissor_box.left()+10, 
				scissor_box.top()+scrollbar_offset + 10, 
				scissor_box.left()+15,
				scissor_box.top()+scrollbar_offset+scrollbar_size - 10
				);
		
	}
	
	@Override
	public void draw(int depth) {
		GraphicsAPI.push_scissor(scissor_box);

		GraphicsAPI.color(MainProgram.DARKEST_COLOR);
		GraphicsAPI.rect(scroll_area, depth + 1);
		GraphicsAPI.color(MainProgram.ACCENT_COLOR);
		if (scroll_area.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
			if (scroll_bar.contains(scroll_bar.left()+1, GraphicsAPI.mouseY())) {
				GraphicsAPI.color(GraphicsAPI.WHITE);
			}
		}
		GraphicsAPI.rect(scroll_bar, depth + 1);
		
		root.draw(depth+1);
		
		GraphicsAPI.pop_scissor();
	}


	boolean scrolling;
	boolean grabbed_handle;
	int initial_mouse_y;
	double initial_scroll_y;
	
	boolean allow_lower_align_when_not_full = false; //im so tireeed

	@Override
	public boolean input() {
		
		if (MainProgram.popups.size() > 0) return false;
		
		if (scroll_area.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
			if (GraphicsAPI.left_click_pressed()) {
				scrolling = true;
				if (scroll_bar.contains(scroll_bar.left()+1, GraphicsAPI.mouseY())) {
					grabbed_handle = true;
					initial_mouse_y = GraphicsAPI.mouseY();
					initial_scroll_y = scroll_y;
				}
			}
			
		}
		if (scrolling) {
			
			scroll_target = -1;
			
			if (!grabbed_handle) {
				initial_mouse_y = scissor_box.top();
				initial_scroll_y = 0;
			}
			int hh = fixed_sheight;
			int mouse_difference = GraphicsAPI.mouseY()-initial_mouse_y;
			
			// scroll bar coordinate * ratio = real coordinate
			float ratio = hh / (float) ((scroll_area.bottom()-scroll_area.top())-scrollbar_height);
			scroll_y = initial_scroll_y + (mouse_difference * ratio);
			//Log.send(mouse_difference, ratio, root.height(), (scroll_area.bottom()-scroll_area.top()));
			
			calculate_scrollbar_position();

			if (GraphicsAPI.left_click_released()) {
				scrolling = false;
				grabbed_handle = false;
			}
				
			return true;
		}
		
		if (GraphicsAPI.scrollY() != 0) {
			scroll_y -= GraphicsAPI.scrollY();
			scroll_target = -1;
		}
		
		if (scroll_target != -1) {
			scroll_y = Utility.lerp(scroll_y, scroll_target, 0.15);
			calculate_scrollbar_position();
		}
		
		calculate_scrollbar_position();

		
		return super.input();
	}

}