package musicplayer.components.search;

import org.joml.Vector4f;

import musicplayer.components.search.SearchRecords.*;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Text;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_SearchTerm extends G_Element {
	
	{ allmargins(3); }
	
	static Vector4f[] colors = new Vector4f[] {
		new Vector4f(30 / 255f, 30 / 255f, 31 / 255f, 1),
		new Vector4f(129 / 255f, 217 / 255f, 227 / 255f, 1),
		new Vector4f(72 / 255f, 232 / 255f, 96 / 255f, 1),
		new Vector4f(232 / 255f, 184 / 255f, 72 / 255f, 1)
	};
	
	SearchTermType type;
	String extra;
	
	
	public G_SearchTerm(SearchTerm term) {
		type = term.type();
		extra = term.extra();
		segment_text = new G_Text[type.segments.length + (extra.isEmpty() ? 0 : 1)];
		for (int i = 0; i < segment_text.length; i++) {
			segment_text[i] = new G_Text();
			if (i < type.segments.length) segment_text[i].text = type.segments[i];
			if (i >= type.segments.length) segment_text[i].text = extra;
		}
	}

	@Override
	public void recalculate_size() {
		this.unpadded_height = GraphicsAPI.size("to find out").y + (top_margin) + (bottom_margin);
		
		int count = type.segments.length + (extra.isEmpty() ? 0 : 1);
		this.unpadded_width = (left_margin * count) + (right_margin * count);
		for (String segment : type.segments) this.unpadded_width += GraphicsAPI.size(segment).x;
		if (!extra.isEmpty()) this.unpadded_width += GraphicsAPI.size(extra).x;
		
		for (G_Text text : segment_text) text.allmargins(bottom_margin);;
		for (G_Text text : segment_text) text.recalculate_size();
		
	}
	
	G_Text[] segment_text;
	Rectangle[] segments;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		segments = new Rectangle[type.segments.length + (extra.isEmpty() ? 0 : 1)];
		
		int xx = left;
		
		//Log.send(type.segments.length + " : " + type.name());
		
		for (int i = 0; i < type.segments.length; i++) {
			int width = left_margin + right_margin + GraphicsAPI.size(type.segments[i]).x;
			
			segments[i] = new Rectangle(xx, top, xx+width, bottom);
			segment_text[i].layout(xx, top, xx+width, bottom);
			xx += width;
		}
		
		int width_so_far = xx - left;
		int area_width = right-left;
		int remaining_width = area_width - width_so_far;
		
		if (!extra.isEmpty()) {
			//int width = left_margin + right_margin + GraphicsAPI.size(extra).x;
			segments[segments.length-1] = new Rectangle(xx, top, xx+remaining_width, bottom);
			segment_text[segments.length-1].layout(xx, top, xx+remaining_width, bottom);

		}
	}

	@Override
	public void draw(int depth) {
	  for (int i = 0; i < segments.length; i++) {
		Rectangle r = segments[i];
		GraphicsAPI.color(colors[i]);
		GraphicsAPI.rect(r, depth);
		
		GraphicsAPI.color(GraphicsAPI.WHITE);
	  }
	  for (G_Text text : segment_text) text.draw(depth+1);
	}
}