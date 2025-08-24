package musicplayer.gui;

import java.util.ArrayList;


public class G_WrappedList extends G_Element {
	
	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	
	{
		sub_elements = elements;
	}

	@Override
	public void recalculate_size() {
		for (G_Element e : elements) e.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		int xx = left;
		int yy = top;
		for (G_Element e : elements) {
			if (xx + e.width() > right) {
				xx = left;
				yy += e.height();
			}
			e.layout(xx, yy, xx+e.width(), yy+e.height());
			xx += e.width();
		}

	}

	@Override
	public void draw(int depth) {
		for (G_Element e : elements) e.draw(depth+1);
	}

	public void add(G_Element e) {
		elements.add(e);
	}

}
