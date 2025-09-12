package musicplayer.components.settings;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_Switch extends G_Element {
	
	boolean enabled = true;

	public G_Switch(boolean boolean1) {
		enabled = boolean1;
	}

	@Override
	public void recalculate_size() {
		this.unpadded_height = 20;
	}
	
	Rectangle draw_area;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		hover_rectangle = new Rectangle(left, top, right, bottom);
		
		draw_area = new Rectangle(left, top, right, bottom);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(Settings.LIGHT_COLOR());
		GraphicsAPI.rect(draw_area, depth);
		
		GraphicsAPI.color(Settings.DARK_COLOR());
		GraphicsAPI.rect(draw_area.decrease(1), depth+1);
		
		GraphicsAPI.color(Settings.SEMIDARK_COLOR());
		if (enabled) {
			GraphicsAPI.color(Settings.ACCENT_COLOR());
		}
		GraphicsAPI.rect(draw_area.thin_horizontally(draw_area.width()/3), depth+3);
	}
	
	@Override public void onClick() {
		enabled = !enabled;
		onChangeValue(enabled);
	}

	public void onChangeValue(boolean b) { }

}
