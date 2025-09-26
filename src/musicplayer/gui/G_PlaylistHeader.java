package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.components.settings.Settings;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.extra.Popup;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.gui.screens.G_PlaylistScreen.PlaylistOptionIcon;
import musicplayer.parts.Library;
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
			ArrayList<Option> option_arrays = new ArrayList<Option>();
				option_arrays.addAll(G_PlaylistScreen.playlist_menu_options);
			if (MusicPlayer.current_view_playlist().is_album) {
				option_arrays.addAll(G_PlaylistScreen.album_menu_options); }
			
			Option[] options = Option.from(option_arrays);
			MainProgram.popups.add(new Popup(x + width(), y + height(), Alignment.RIGHT, Alignment.LEFT, options));
		} };
		
	public G_List right_icons 			= new G_List(menu);
	
	public void set_playlist(String name) {
		title.text = Library.getPlaylist(name).name();
		
		removeSubElement(right_icons);
		right_icons = new G_List();
		addSubElement(right_icons);
		for (PlaylistOptionIcon icon : G_PlaylistScreen.icons) {
			if (icon.show_while_locked() || !MusicPlayer.current_view_playlist().locked) {
				right_icons.add(icon.icon());
			}
		}
		right_icons.add(menu);
		
		title.recalculate_size();
	}

	{
		base_color = Settings.LIGHT_COLOR();
		
		right_margin = 30;
		left_margin = 30;
		top_margin = 10;

		addSubElement(title);
		addSubElement(home);
		addSubElement(right_icons);
		
		title.text = MusicPlayer.playlist;
		title.halign(Alignment.MIDDLE);
		
		title.left_margin = 10;
		title.right_margin = 10;
		
		recalculate_size();
	}

	@Override
	protected void i_recalculate_size() {
		title.recalculate_size();
		home.recalculate_size();
		right_icons.recalculate_size();
		
		this.unpadded_height = home.height() + 5;
	}
	
	Rectangle draw_area;

	@Override
	protected void i_layout(int left, int top, int right, int bottom) {
		draw_area = new Rectangle(left, top, right, bottom);
		
		int side_width = home.width();
		if (right_icons.width() > side_width) side_width = right_icons.width();

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		home		.layout(left, 						top, left + side_width, 	bottom);
		title		.layout(left + side_width, 		top, right-side_width, 	bottom);
		right_icons	.layout(right- side_width, 		top, right, 				bottom);
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(draw_area, depth);
		title.draw(depth+1);
		home.draw(depth+1);
		right_icons.draw(depth+1);
	}

}
