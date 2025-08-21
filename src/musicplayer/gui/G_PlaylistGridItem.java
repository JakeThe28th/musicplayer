package musicplayer.gui;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.Texture;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_PlaylistGridItem extends G_Element {
	
	Texture cover;
	Playlist playlist;
	
	G_Text name = new G_Text();;
	
	{
		sub_elements.add(name);
	}
	
	public G_PlaylistGridItem(Playlist playlist) {
		this.playlist = playlist;
		name.text(playlist.name());
	}

	@Override
	public void recalculate_size() {
		// TODO Auto-generated method stub
		name.recalculate_size();
	}

	Rectangle area;
	Rectangle name_area;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		area = new Rectangle(left+left_margin, top+top_margin, right-right_margin, bottom-bottom_margin);
		
		int text_top = (bottom-bottom_margin)-name.height();
		name_area = new Rectangle(left+left_margin, text_top, right-right_margin, bottom-bottom_margin);
		name.layout(name_area.left(), name_area.top(), name_area.right(), name_area.bottom());
		
		Rectangle b = GraphicsAPI.scissor();
		this.hover_rectangle = new Rectangle(
				left, 
				(top > b.top()) ? top : b.top(), 
				right,
				(bottom < b.bottom()) ? bottom : b.bottom());
	}

	@Override
	public void draw(int depth) {
		if (cover == null) {
			cover = playlist.cover();
			//Log.send("cover " +  cover);
		}
		GraphicsAPI.color(GraphicsAPI.WHITE);
		GraphicsAPI.rect(area, depth, cover);
		
		GraphicsAPI.color(GraphicsAPI.TRANSLUCENT_BLACK);
		GraphicsAPI.rect(name_area, depth + 1);
		
		name.draw(depth+2);
	}
	
	@Override
	public void onClick() {
		MusicPlayer.set_current_view_playlist(playlist.identifier());
		MainProgram.change_screen(G_PlaylistScreen.IDENTIFIER);
		MusicPlayer.scroll_to_current();
	}

}
