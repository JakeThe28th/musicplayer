package musicplayer.gui;

import java.util.ArrayList;

public class G_Grid extends G_Element {

	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	{
		sub_elements = elements;
	}

	public void add(G_Element element) {
		elements.add(element);
	}
	
	int columns = 3;
	int item_size = 10;
	
	@Override
	public void recalculate_size() {
		for (G_Element e : elements) e.recalculate_size();
		this.unpadded_height = ( (int) Math.ceil(elements.size() / (float) columns) ) * item_size;
	}
	
	@Override
	public void layout(int left, int top, int right, int bottom) {

		int xx = left + left_margin;
		int yy = top + top_margin;
		item_size = ( (right-right_margin) - xx ) / columns;
		
		int current_column = 0;
		for (G_Element element : elements) {

			element.layout(xx, yy, xx+item_size, yy+item_size);
			
			current_column++;
			xx += item_size;
			if (current_column >= columns) {
				yy += item_size;
				xx = left + left_margin;
				current_column = 0;
			}
		}

	}

	@Override
	public void draw(int depth) {
		for (G_Element e : elements) e.draw(depth+1);
	}
	

}
