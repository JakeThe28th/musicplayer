package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.graphics.API;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;

public class G_SongControls extends G_Element {
	
	@Override public ArrayList<G_Element> sub_elements() { return subelements; }
	ArrayList<G_Element> subelements = new ArrayList<G_Element>();
	
	// The song controls that remain at the bottom of the screen
	
	G_Text 		title			= new G_Text().text("No Song");
	G_Slider 	progress_bar	= new G_Slider();
	G_List		center_icons	= new G_List(
										new G_Icon("previous"),
										new G_Icon("stop"),
										new G_Icon("play")
										{ @Override public void onClick() {
											Library.play();
										}},
										new G_Icon("next"));
	
	G_List		left_icons		= new G_List(
										new G_Icon("volume"));
	
	G_List		right_icons		= new G_List(
										new G_Icon("shuffle"));
	
	{
		title.halign(Alignment.MIDDLE);
		right_margin = 30;
		left_margin = 30;
		
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
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		int yy = top;
		
		left += left_margin;
		right -= right_margin;
		
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
		title.draw(depth + 1);
		progress_bar.draw(depth + 1);
		center_icons.draw(depth+1);
		left_icons.draw(depth+1);
		right_icons.draw(depth+1);
	}	
	
}
