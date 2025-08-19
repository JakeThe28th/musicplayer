package musicplayer.gui;

import java.util.ArrayList;

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
				left += 20;
			}
			
			int yy = top + top_margin + GUIUtility.getAlignmentOffset(top, bottom, height(), Alignment.LEFT);
			for (G_Element e : elements) {
				e.layout(left, yy, right, yy+e.height());
				yy+=e.height();
			}
		}
	}

	@Override
	public void draw(int depth) {
		if (scrollable) GraphicsAPI.push_scissor(scissor_box);
		for (G_Element e : elements) {
			e.draw(depth+1);
		}
		if (scrollable) GraphicsAPI.pop_scissor();
	}

}
