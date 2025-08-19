package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.MusicPlayer;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_SongControls extends G_Element {
	
	@Override public ArrayList<G_Element> sub_elements() { return subelements; }
	ArrayList<G_Element> subelements = new ArrayList<G_Element>();
	
	boolean should_unpause_after_seek = true;
	
	// The song controls that remain at the bottom of the screen
	
	G_Text 		title			= new G_Text().text("No Song");
	G_Slider 	progress_bar	= new G_Slider() 
			{ @Override public void onDrag(double new_value) {
				if (current_song != null) {
					if (GraphicsAPI.left_click_pressed()) {
						should_unpause_after_seek = Library.playing();
					}
					Library.pause();
					
					Library.seek((long) (Library.songLength() * new_value));
					
					if (GraphicsAPI.left_click_released()) {
						if (should_unpause_after_seek) Library.play();
					}
					
				}
			} };
		G_Icon 		previous 		= new G_Icon("previous");
		G_Icon 		stop 			= new G_Icon("stop")
			{ @Override public void onClick() { Library.stop(); play_pause.icon_name = "play"; }};
		G_Icon 		play_pause 		= new G_Icon("play") 
			{ @Override public void onClick() { 
				if (Library.playing()) {
					icon_name = "play";
					Library.pause(); 
				} else {
					icon_name = "pause";
					Library.play();
				}
				}};
		G_Icon 		next 			= new G_Icon("next");
		G_Icon 		volume 			= new G_Icon("volume");
		G_Icon 		shuffle 		= new G_Icon("shuffle");
	G_List		center_icons	= new G_List(previous, stop, play_pause, next);
	G_List		left_icons		= new G_List(volume);
	G_List		right_icons		= new G_List(shuffle);
	
	{
		base_color = MusicPlayer.LIGHT_COLOR;

		title.halign(Alignment.MIDDLE);
		right_margin = 30;
		left_margin = 30;
		top_margin = 10;

		center_icons.halign(Alignment.MIDDLE);
		
		subelements.add(title);
		subelements.add(progress_bar);
		subelements.add(center_icons);
		subelements.add(left_icons);
		subelements.add(right_icons);
	}
	
	@Override
	public void recalculate_size() {
		title.recalculate_size();
		progress_bar.recalculate_size();
		center_icons.recalculate_size();
		left_icons.recalculate_size();
		right_icons.recalculate_size();
		
		this.unpadded_height = title.height() + progress_bar.height() + center_icons.height();
	}
	
	Rectangle draw_area;
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		draw_area = new Rectangle(left, top, right, bottom);

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		
		int yy = top;
		
			  title.layout(left, yy, right, yy + title.height());
		yy += title.height();
		
			  progress_bar.layout(left, yy, right, yy + progress_bar.height());
		yy += progress_bar.height();
		
		
		left_icons		.layout(left, 							yy, left +left_icons .width(), 	bottom);
		center_icons	.layout(left +left_icons.width(), 		yy, right-right_icons.width(), 	bottom);
		right_icons		.layout(right-right_icons.width(), 		yy, right, 						bottom);	
	}
	
	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(draw_area, depth);
		
		if (current_song != null) {
			progress_bar.amount = Library.songTime() / (float) Library.songLength();
		}		
		
		title.draw(depth + 1);
		progress_bar.draw(depth + 1);
		center_icons.draw(depth+1);
		left_icons.draw(depth+1);
		right_icons.draw(depth+1);
	}
	
	// -- ... -- //
	
	UUID current_song = null;

	public void current(UUID song) {
		title.text = Library.get(song).name();
		current_song = song;
		recalculate_size();
	}

	public void setPlaying(boolean b) {
		play_pause.icon_name = b ? "pause" : "play";
	}	
	
}
