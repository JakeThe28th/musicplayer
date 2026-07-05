package nowplaying.gui.elements;

import nowplaying.parts.data.Playlist;
import snowui.GUIInstance;
import snowui.coss.enums.Color;
import snowui.elements.abstracts.GUIElement;

public class GUIPlaylistCover extends GUIElement {
	
	Playlist playlist;
	
	public GUIPlaylistCover(Playlist playlist) {
		this.playlist = playlist;
	}

	@Override
	public void recalculateSize(GUIInstance gui) {
		// N/A (but added a default size for testing)
		unpadded_height = 90;
		unpadded_width  = 90;
	}

	@Override
	public void updateDrawInfo(GUIInstance gui) {
		// TODO Auto-generated method stub
		hover_rectangle(padded_limit_rectangle());
	}

	@Override
	public void draw(GUIInstance gui, int depth) {
		// TODO Auto-generated method stub
		gui.canvas().color(Color.WHITE.val());
		gui.canvas().rect(padded_limit_rectangle(), depth, playlist.glcover());
	}

}
