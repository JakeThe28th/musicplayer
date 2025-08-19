package musicplayer.gui;

import musicplayer.graphics.API;
import musicplayer.gui.enums.Alignment;

public class G_SongControls extends G_Element {
	
	// The song controls that remain at the bottom of the screen
	
	G_Text 		title			= new G_Text().text("No Song");
	G_Slider 	progress_bar	= new G_Slider();
	G_List		icons;
	
	{
		title.halign(Alignment.MIDDLE);
	}
	
	@Override
	public void recalculate_size() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		int yy = top;
		
			  title.layout(left, yy, right, yy + title.height());
		yy += title.height();
		
			  progress_bar.layout(left, yy, right, yy + progress_bar.height());
		yy += progress_bar.height();
		
	}
	@Override
	public void draw(int depth) {
		title.draw(depth + 1);
		progress_bar.draw(depth + 1);
	}	
	
}
