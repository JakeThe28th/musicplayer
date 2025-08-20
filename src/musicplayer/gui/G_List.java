package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Rectangle;

public class G_List extends G_Element {
		
	public G_List(G_Element...elements) {
		for (G_Element e : elements) {
			this.elements.add(e);
		}
	}
	
	public G_List verticalify() {
		vertical = true;
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
	
	{	sub_elements = elements;	}

	boolean vertical = false;


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
				
		if (!vertical) {
			int xx = left + left_margin + GUIUtility.getAlignmentOffset(left, right, width(), horizontal_align);
			for (G_Element e : elements) {
				e.layout(xx, top, xx+e.width(), bottom);
				xx+=e.width();
			}
		} else {
			
			int yy = top + top_margin + GUIUtility.getAlignmentOffset(top, bottom, height(), Alignment.LEFT);
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
				if (top_y < GraphicsAPI.scissor().top()) top_y = GraphicsAPI.scissor().top();
				if (bottom_y > GraphicsAPI.scissor().bottom()) bottom_y = GraphicsAPI.scissor().bottom();
				e.layout(left, yy, right, bottom_y);
				e.hover_rectangle = new Rectangle(
						e.hover_rectangle.left(), 
						(e.hover_rectangle.top() > GraphicsAPI.scissor().top()) ? e.hover_rectangle.top() : GraphicsAPI.scissor().top(), 
						e.hover_rectangle.right(), 
						e.hover_rectangle.bottom());
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
		for (G_Element e : elements) {
			if (e.hover_rectangle.left() == -1) continue;
			e.draw(depth+1);
		}
	}

}
