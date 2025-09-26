package musicplayer.gui.special;

import musicplayer.MainProgram;
import musicplayer.components.settings.Settings;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Text;
import musicplayer.parts.MusicPlayer;
import musicplayer.utility.Rectangle;

@Deprecated
public class G_WindowDecorations extends G_Element {
	
	G_Text text = new G_Text();
	
	private static final int HEIGHT = 18;

	{
		this.allmargins(10);
		text.text(GraphicsAPI.title());
	}

	@Override
	public void recalculate_size() {
		this.unpadded_height = HEIGHT;
		this.unpadded_width = GraphicsAPI.width();
	}

	Rectangle layout_area = null;
	Rectangle draw_area = null;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		layout_area = new Rectangle(left, top, right, bottom);
		draw_area = new Rectangle(left+left_margin, top+top_margin, right-right_margin, bottom-bottom_margin);
	
		text.text(GraphicsAPI.title());
		text.layout(left, top, right, bottom);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.font_size(HEIGHT);
		text.draw(depth+2);
		GraphicsAPI.font_size(Settings.font_size());
	}

}
