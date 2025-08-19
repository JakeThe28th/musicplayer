package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MusicPlayer;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
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
			int index = 0;
			for (G_Element e : elements) {
				if (yy > bottom || (yy+e.height()) < top) {
					e.hover_rectangle = new Rectangle(0,0,0,0);
					continue;
				}
				int bottom_y = yy+e.height();
				if (bottom_y > bottom) bottom_y = bottom;
				e.layout(left, yy, right, bottom_y);
				if (e instanceof G_Song) {
					((G_Song) e).index(index);
				}
				yy+=e.height();
				index++;
			}
		}
	}

	@Override
	public void draw(int depth) {
		
		if (scrollable && (height() > scissor_box.bottom()-scissor_box.top())) {
			Rectangle b = scissor_box;
			GraphicsAPI.color(MusicPlayer.DARKEST_COLOR);
			int scrollbar_size = (int) ((( b.bottom()-b.top() ) / (double) height()) * (b.bottom()-b.top()));
			int scrollbar_offset = (int) (scroll_y / (double) height());
			GraphicsAPI.rect(
					b.left()+5, 
					b.top()+5, 
					b.left()+20,
					b.bottom()-5,
					depth + 1);
			GraphicsAPI.color(MusicPlayer.ACCENT_COLOR);
			GraphicsAPI.rect(
					b.left()+10, 
					b.top()+scrollbar_offset + 10, 
					b.left()+15,
					b.top()+scrollbar_offset+scrollbar_size - 10,
					depth + 1);
		}
		
		if (scrollable) GraphicsAPI.push_scissor(scissor_box);
		for (G_Element e : elements) {
			e.draw(depth+1);
		}
		if (scrollable) GraphicsAPI.pop_scissor();
	}

}
