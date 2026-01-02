package nowplaying.gui.elements;

import org.joml.Vector2i;

import frost3d.interfaces.F3DTextRenderer;
import frost3d.utility.Rectangle;
import snowui.GUIInstance;
import snowui.coss.enums.PredicateKey;
import snowui.elements.abstracts.GUIElement;

public class GUIRollingText extends GUIElement {
	
	{ identifier("text"); }
	
	String text = "Unset Text";
	String text_concat = "Unset T...";

	public boolean can_wrap = true;
	public boolean force_scroll = false;
	
	public GUIRollingText(String text) {
		this.text = text;
	}

	@Override
	public void recalculateSize(GUIInstance gui) {
		gui.canvas().textrenderer().font_size(style().size().pixels());
		Vector2i size = gui.canvas().textrenderer().size(text);
		this.unpadded_width = size.x;
		this.unpadded_height = size.y;
	}

	@Override
	public void updateDrawInfo(GUIInstance gui) {
		Rectangle area = this.aligned_limit_rectangle();
		this.hover_rectangle(area);
		
		gui.canvas().textrenderer().font_size(style().size().pixels());

		if (can_wrap) {
			if (text.length() <= 3) {
				text_concat = text;
			} else {
				text_concat = text;
				F3DTextRenderer textrenderer = gui.canvas().textrenderer();
				while (textrenderer .size(text_concat).x > area.width() && text_concat.length() >= 4) {
					text_concat = text_concat.substring(0, text_concat.length()-1);
				}
				text_concat = text_concat.substring(0, text_concat.length()-3);
				text_concat += "...";
			}
		}
		
	}

	@Override
	public void draw(GUIInstance gui, int depth) {
		
		gui.canvas().color(style().base_color().color());
		gui.canvas().textrenderer().font_size(style().size().pixels());

		Rectangle area = this.aligned_limit_rectangle();
		
		if (can_wrap && ( unpadded_width > area.width() && (force_scroll || get(PredicateKey.HOVERED)) )) {
			setFadeColumn(area.left(), area.left()+30, area.right(), area.right()-30);
			// scroll text
			int time = (int) Math.floorMod((-(System.currentTimeMillis()-hover_start_time) / 20), unpadded_width + 30);
			gui.canvas().text(area.left()+time, area.top(), depth, text);
			gui.canvas().text((area.left()-(unpadded_width + 30))+time, area.top(), depth, text);
			resetFadeColumn();
		} else if (can_wrap && (unpadded_width > (area.width()))) {
			gui.canvas().text(area.left(), area.top(), depth, text_concat);
		} else {
			gui.canvas().text(area.left(), area.top(), depth, text);
		}
		
	}
	
	long hover_start_time;
	
	@Override
	public void onHover() {
		if (!get(PredicateKey.HOVERED)) hover_start_time = System.currentTimeMillis();
	}
	
	// I need to implement the dynamic shader recompilation stuff for the gui for these,
	// so i'll do this later...
	
	private void setFadeColumn(int left_min, int left_max, int right_min, int right_ma) {
		// TODO
	}

	private void resetFadeColumn() {
		// TODO
	}


}
