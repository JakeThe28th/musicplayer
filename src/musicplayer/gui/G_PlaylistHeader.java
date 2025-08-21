package musicplayer.gui;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.extra.Popup;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.parts.MusicPlayer;
import musicplayer.utility.Rectangle;

public class G_PlaylistHeader extends G_Element {
	
	G_Text 		title			= new G_Text().text("No Playlist");
	G_Icon 		home 			= new G_Icon("home")
		{ @Override public void onClick() { 
			MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
		}};
	G_Icon 		menu 			= new G_Icon("hamburger")
			{ @Override public void onClick() {
				
				Option[] options = new Option[G_PlaylistScreen.playlist_menu_options.size()];
				for (int i = 0; i <  G_PlaylistScreen.playlist_menu_options.size(); i++) {
					options[i] = G_PlaylistScreen.playlist_menu_options.get(i);
				}
				
				MainProgram.popups.add(
						new Popup(
								x + width(), 
								y + height(), 
								Alignment.RIGHT, 
								Alignment.LEFT, 
								options
							)
						);
			} };
	
	
	
	public void set_playlist(String name) {
		title.text = name;
		title.recalculate_size();
	}

	{
		base_color = MainProgram.LIGHT_COLOR;
		
		right_margin = 30;
		left_margin = 30;
		top_margin = 10;

		addSubElement(title);
		addSubElement(home);
		addSubElement(menu);
		
		title.text = MusicPlayer.playlist;
		title.halign(Alignment.MIDDLE);
		
		recalculate_size();
	}

	@Override
	public void recalculate_size() {
		title.recalculate_size();
		home.recalculate_size();
		menu.recalculate_size();
		
		this.unpadded_height = home.height() + 5;
	}
	
	Rectangle draw_area;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		draw_area = new Rectangle(left, top, right, bottom);

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		home	.layout(left, 						top, left +home .width(), 	bottom);
		title	.layout(left +home.width(), 		top, right-menu.width(), 	bottom);
		menu	.layout(right-menu.width(), 		top, right, 				bottom);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(draw_area, depth);
		title.draw(depth+1);
		home.draw(depth+1);
		menu.draw(depth+1);
	}

}
