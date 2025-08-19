package musicplayer.gui;

import musicplayer.graphics.API;
import musicplayer.gui.enums.Alignment;

public class G_SongControls extends G_Element {
	
	// The song controls that remain at the bottom of the screen
	
	G_Text 		title			= new G_Text().text("No Song");
	G_Slider 	progress_bar	= new G_Slider();
	G_List		icons			= new G_List();
	
	{
		title.halign(Alignment.MIDDLE);
		progress_bar.right_margin = 30;
		progress_bar.left_margin = 30;
		
		G_Icon ic;
			ic = new G_Icon();
			ic.icon_name = "pause";
			icons.elements.add(ic);
			ic = new G_Icon();
			ic.icon_name = "play";
			icons.elements.add(ic);
			ic = new G_Icon();
			ic.icon_name = "shuffle";
			icons.elements.add(ic);
		icons.halign(Alignment.MIDDLE);
		
	}
	
	@Override
	public void recalculate_size() {
		title.recalculate_size();
		progress_bar.recalculate_size();
		icons.recalculate_size();
	}
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		int yy = top;
		
			  title.layout(left, yy, right, yy + title.height());
		yy += title.height();
		
			  progress_bar.layout(left, yy, right, yy + progress_bar.height());
		yy += progress_bar.height();
		
		icons.layout(left, yy, right, bottom);
		
	}
	@Override
	public void draw(int depth) {
		title.draw(depth + 1);
		progress_bar.draw(depth + 1);
		icons.draw(depth+1);
	}	
	
}
