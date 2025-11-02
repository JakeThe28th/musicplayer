package musicplayer.gui;

import java.util.ArrayList;


public class G_WrappedList extends G_Element {
	
	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	
	{
		sub_elements = elements;
	}

	@Override
	public void recalculate_size() {
		// for (G_Element e : elements) e.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		int real_top = top;

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		int xx = left;
		int yy = top;
		int element_height =  elements.size() != 0 ? elements.get(0).height() : 0;
		int max_empty_space_on_right = 100;
		boolean advance = false;
		for (G_Element e : elements) {
			
			int draw_right = xx + e.width();
			if (draw_right > right) {
				if ((xx+max_empty_space_on_right) > right) {
					xx = left;
					yy += element_height;
					draw_right = xx + e.width();
				} else {
					advance = true;
					draw_right = right;
				}
			}
			
			e.layout(xx, yy, draw_right, yy+element_height);
			xx += e.width();
			if (advance) {
				xx = left;
				yy += element_height;
				advance = false;
			}
		}
		
		this.unpadded_height = (yy+element_height+bottom_margin)-real_top;
		this.unpadded_height -= element_height/2;
	}

	@Override
	public void draw(int depth) {
		for (G_Element e : elements) e.draw(depth+1);
	}

	public void add(G_Element e) {
		elements.add(e);
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
