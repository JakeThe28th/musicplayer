package musicplayer.gui;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.parts.Playlist;
import musicplayer.utility.Rectangle;

public class G_PlaylistGridItem extends G_Element {

	public G_PlaylistGridItem(Playlist playlist) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void recalculate_size() {
		// TODO Auto-generated method stub
		
	}

	Rectangle area;
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		area = new Rectangle(left+left_margin, top+top_margin, right-right_margin, bottom-bottom_margin);
	}

	@Override
	public void draw(int depth) {
		// TODO Auto-generated method stub
		GraphicsAPI.rect(area, depth);
	}

}
