package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.MusicPlayer;
import musicplayer.utility.Rectangle;

public class G_PlaylistHeader extends G_Element {
	
	G_Text 		title			= new G_Text().text("No Playlist");
	G_Icon 		home 			= new G_Icon("home");
	G_Icon 		menu 			= new G_Icon("hamburger");
	
	public void set_playlist(String name) {
		title.text = name;
		title.recalculate_size();
	}

	ArrayList<G_Element> subelements = new ArrayList<G_Element>();
	{
		base_color = MainProgram.LIGHT_COLOR;
		
		right_margin = 30;
		left_margin = 30;
		top_margin = 10;

		subelements.add(title);
		subelements.add(home);
		subelements.add(menu);
		
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

	@Override
	public ArrayList<G_Element> sub_elements() {
		return subelements;
	}

}
