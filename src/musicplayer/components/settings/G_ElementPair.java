package musicplayer.components.settings;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.utility.Rectangle;

public class G_ElementPair extends G_Element {
	
	G_Element a;
	G_Element b;
	float percent;
	
	public G_ElementPair(G_Element aa, G_Element bb, float percent) {
		a(aa);
		b(bb);
		this.percent = percent;
	}

	private void a(G_Element aa) {
		if (this.a != null) removeSubElement(a);
		this.a = aa;
		addSubElement(a);
	}
	
	private void b(G_Element bb) {
		if (this.b != null) removeSubElement(b);
		this.b = bb;
		addSubElement(b);
	}

	@Override
	public void recalculate_size() {
//		a.recalculate_size();
//		b.recalculate_size();
		
		this.unpadded_height = a.height();
		if (b.height() > this.unpadded_height) this.unpadded_height = b.height();
		
		this.unpadded_width = a.width();
		if (b.width() > this.unpadded_width) this.unpadded_width = b.width();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		float width = right - left;
		int left_width = (int) (width * percent);
		
		this.hover_rectangle = new Rectangle(0,0,0,0);
		
		a.layout(left, top, left+left_width, bottom);
		b.layout(left+left_width, top, right, bottom);

	}

	@Override
	public void draw(int depth) {
		a.draw(depth);
		b.draw(depth);
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
