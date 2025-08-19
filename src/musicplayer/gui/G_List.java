package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_List extends G_Element {
	
	@Override public ArrayList<G_Element> sub_elements() { return elements; }
	
	public G_List(G_Element...elements) {
		for (G_Element e : elements) {
			this.elements.add(e);
		}
	}
	
	public G_List verticalify() {
		vertical = true;
		return this;
	}
	

	public G_List scrollable(boolean b) {
		scrollable = b;
		return this;
	}
	
	public void add(G_Element e) {
		if (e instanceof G_Song) {
			((G_Song) e).index(elements.size());
		}
		this.elements.add(e);
		this.recalculate_size();
	}
	
	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	
	boolean vertical = false;
	boolean scrollable = false;
	
	Rectangle scissor_box = null;

	private double scroll_y = 0;

	@Override
	public void recalculate_size() {
		for (G_Element e : elements) {
			e.recalculate_size();
		}
		
		if (!vertical) {
			this.unpadded_height = 0;
			this.unpadded_width = 0;
			for (G_Element e : elements) {
				this.unpadded_height = (e.height() > this.unpadded_height ? e.height() : this.unpadded_height);
				this.unpadded_width += e.width();
			}
		} else {
			this.unpadded_height = 0;
			this.unpadded_width = 0;
			for (G_Element e : elements) {
				this.unpadded_width = (e.width() > this.unpadded_width ? e.width() : this.unpadded_width);
				this.unpadded_height += e.height();
			}
		}
		
	}
	
	Rectangle scroll_area;
	Rectangle scroll_bar;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		scissor_box = new Rectangle(left, top, right, bottom);
		
		if (!vertical) {
			int xx = left + left_margin + GUIUtility.getAlignmentOffset(left, right, width(), horizontal_align);
			for (G_Element e : elements) {
				e.layout(xx, top, xx+e.width(), bottom);
				xx+=e.width();
			}
		} else {
			
			if (scrollable) {
				left += 30;
			}
			
			int yy = top + top_margin + GUIUtility.getAlignmentOffset(top, bottom, height(), Alignment.LEFT);
			    yy-=scroll_y;
			int index = 0;
			for (G_Element e : elements) {
				if (yy+e.height() < top || (yy) > bottom) {
					e.hover_rectangle = new Rectangle(-1,-1,-1,-1);
					index++;
					yy+=e.height();
					continue;
				}
				int bottom_y = yy+e.height();
				int top_y = yy;
				if (top_y < top) top_y = top;
				if (bottom_y > bottom) bottom_y = bottom;
				e.layout(left, yy, right, bottom_y);
				e.hover_rectangle = new Rectangle(
						e.hover_rectangle.left(), 
						(e.hover_rectangle.top() > top) ? e.hover_rectangle.top() : top, 
						e.hover_rectangle.right(), 
						e.hover_rectangle.bottom());
				if (e instanceof G_Song) {
					((G_Song) e).index(index);
				}
				yy+=e.height();
				index++;
			}
			
			if (scrollable) {
				calculate_scrollbar_position();
			}
		}
	}
	
	int scrollbar_height;
	void calculate_scrollbar_position() {
		Rectangle b = scissor_box;
		double hh = height();
		double real_height = b.bottom()-b.top();
		if (hh < real_height) hh = real_height;
		
		int scrollbar_size = (int) ((( real_height ) / hh) * (real_height));
		scrollbar_height = scrollbar_size;
		int scrollbar_offset = (int) ((scroll_y / hh) * ( real_height ));
		scroll_area = new Rectangle(
				b.left()+5, 
				b.top()+5, 
				b.left()+20,
				b.bottom()-5);
		
		scroll_bar = new Rectangle(
				b.left()+10, 
				b.top()+scrollbar_offset + 10, 
				b.left()+15,
				b.top()+scrollbar_offset+scrollbar_size - 10
				);
	}

	@Override
	public void draw(int depth) {
		if (scrollable) GraphicsAPI.push_scissor(scissor_box);
		if (scrollable) {
			GraphicsAPI.color(MainProgram.DARKEST_COLOR);
			GraphicsAPI.rect(scroll_area, depth + 1);
			GraphicsAPI.color(MainProgram.ACCENT_COLOR);
			if (scroll_area.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
				if (scroll_bar.contains(scroll_bar.left()+1, GraphicsAPI.mouseY())) {
					GraphicsAPI.color(GraphicsAPI.WHITE);
				}
			}
			GraphicsAPI.rect(scroll_bar, depth + 1);
		}
		for (G_Element e : elements) {
			if (e.hover_rectangle.left() == -1) continue;
			e.draw(depth+1);
		}
		if (scrollable) GraphicsAPI.pop_scissor();
	}
	
	boolean scrolling;
	boolean grabbed_handle;
	int initial_mouse_y;
	double initial_scroll_y;
	
	@Override
	public boolean input() {
		if (scrollable) {
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
				
				
				Rectangle b = scissor_box;
				if (!grabbed_handle) {
					initial_mouse_y = b.top();
					initial_scroll_y = 0;
				}
				int hh = height()-(b.bottom()-b.top());
				int mouse_difference = GraphicsAPI.mouseY()-initial_mouse_y;
				
				// scroll bar coordinate * ratio = real coordinate
				float ratio = hh / (float) ((scroll_area.bottom()-scroll_area.top())-scrollbar_height);
				scroll_y = initial_scroll_y + (mouse_difference * ratio);
				//Log.send(mouse_difference, ratio, height(), (scroll_area.bottom()-scroll_area.top()));
				
				if (scroll_y < 0) scroll_y = 0;
				if (scroll_y > hh) scroll_y = hh;
				
				calculate_scrollbar_position();
				
				if (GraphicsAPI.left_click_released()) {
					scrolling = false;
					grabbed_handle = false;
				}
					
				return true;
			}
		}
		return super.input();
	}

}
