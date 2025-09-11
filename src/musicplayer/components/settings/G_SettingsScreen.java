package musicplayer.components.settings;

import musicplayer.MainProgram;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.Screen;

public  class G_SettingsScreen extends G_Element implements Screen {
	
	G_Icon 		home 			= new G_Icon("home")
	{ @Override public void onClick() { 
		MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
	}};
	
	G_List colors = new G_List();
	{ 
		colors.verticalify();
		colors.add(new G_Color()); 
		home.halign(Alignment.MIDDLE);
	}
	
	{ addSubElement(colors); addSubElement(home); }

	public static final G_SettingsScreen INSTANCE = new G_SettingsScreen();

	@Override
	public void recalculate_size() {
		colors.recalculate_size();
		home.recalculate_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		
		int yy = top;
		home.layout(left, top, right, top+home.height());
		
		yy += home.height();
		colors.layout(left, yy, right, bottom);
	}

	@Override
	public void draw(int depth) {
		colors.draw(depth);
		home.draw(depth);
	}

	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return "builtin;settings";}
	
}