package musicplayer.gui;

import java.util.ArrayList;

public class G_List extends G_Element {
	
	public G_List(G_Element...elements) {
		for (G_Element e : elements) {
			this.elements.add(e);
		}
	}
	
	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	
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
		}
		
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		int xx = left + left_margin + GUIUtility.getAlignmentOffset(left, right, width(), horizontal_align);
		for (G_Element e : elements) {
			e.layout(xx, top, xx+30, bottom);
			xx+=e.width();
		}
	}

	@Override
	public void draw(int depth) {
		for (G_Element e : elements) {
			e.draw(depth+1);
		}
	}

}
