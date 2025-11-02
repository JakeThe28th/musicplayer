package musicplayer.gui;

import java.util.ArrayList;
import java.util.function.Predicate;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Rectangle;

public class G_List extends G_Element {
			
	public G_List(G_Element...elements) {
		for (G_Element e : elements) {
			this.elements.add(e);
		}
		this.should_recalculate_size = true;
	}
	
	public G_List verticalify() {
		vertical = true;
		return this;
	}
	
	public void add(G_Element e) {
		this.elements.add(e);
		this.should_recalculate_size = true;
	}
	
	public void add(G_Element e, int index) {
		this.elements.add(index, e);
		this.should_recalculate_size = true;
	}
	
	public void set(G_Element e, int index) {
		this.elements.set(index, e);
		this.should_recalculate_size = true;
	}
	
	public void setraw(G_Element e, int index) {
		this.elements.set(index, e);
	}
	
	public void removeIf(Predicate<? super G_Element> predicate) {
		this.elements.removeIf(predicate);
		this.should_recalculate_size = true;
	}
	
	public int length() 			{ return elements.size();     }
	public G_Element get(int index) { return elements.get(index); }
	
	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	
	{	sub_elements = elements;	}

	boolean vertical = false;


	@Override
	public void recalculate_size() {
//		for (G_Element e : elements) {
//			e.recalculate_size();
//		}
		
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
	
	public int top = 0;
	public int left = 0;

	@Override
	public void layout(int left, int top, int right, int bottom) {	
		if (!vertical) {
			this.top = top;
			int xx = left + left_margin + GUIUtility.getAlignmentOffset(left, right, width(), horizontal_align);
			this.left = xx;
			for (int index = 0; index < elements.size(); index++) {
				G_Element e = elements.get(index);
				if (e instanceof G_Song) ((G_Song) e).draw_index = index;
				e.layout(xx, top, xx+e.width(), bottom);
				xx+=e.width();
			}
		} else {
			
			int yy = top + top_margin + GUIUtility.getAlignmentOffset(top, bottom, height(), Alignment.LEFT);
			this.top = yy;
			this.left = left;
			for (int index = 0; index < elements.size(); index++) {
				G_Element e = elements.get(index);
				
				if (yy+e.height() < top || (yy) > bottom 
				 || yy+e.height() < GraphicsAPI.scissor().top() || (yy) > GraphicsAPI.scissor().bottom()) {
					e.hover_rectangle = new Rectangle(-1,-1,-1,-1);
					yy+=e.height();
					continue;
				}
				int bottom_y = yy+e.height();
				int top_y = yy;
				if (top_y < GraphicsAPI.scissor().top()) top_y = GraphicsAPI.scissor().top();
				if (bottom_y > GraphicsAPI.scissor().bottom()) bottom_y = GraphicsAPI.scissor().bottom();
				if (e instanceof G_Song) ((G_Song) e).draw_index = index;
				e.layout(left, yy, right, bottom_y);
				e.hover_rectangle = new Rectangle(
						e.hover_rectangle.left(), 
						(e.hover_rectangle.top() > GraphicsAPI.scissor().top()) ? e.hover_rectangle.top() : GraphicsAPI.scissor().top(), 
						e.hover_rectangle.right(), 
						e.hover_rectangle.bottom());
				yy+=e.height();
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

	public G_Element element(int i) {
		return elements.get(i);
	}

	public void remove(G_Element e) {
		elements.remove(e);
	}

	public void clear() {
		elements.clear();
	}
	
	@Override // also cull input
	public boolean input() {
		
		if (MainProgram.popups.size() > 0) return false;
		
		for (G_Element e : sub_elements) {
			if (e.hover_rectangle.left() == -1) continue;
			if (e.input()) return true;
		}
		
		return false;
		
	}

	@Override
	public void foo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickAnimation() {
		// TODO Auto-generated method stub
		
	}

}
