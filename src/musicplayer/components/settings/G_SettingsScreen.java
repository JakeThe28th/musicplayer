package musicplayer.components.settings;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
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
		
		colors.add(new G_HexColor(MainProgram.ACCENT_COLOR) { 
			@Override public void onChangeColor(Vector4f col) { MainProgram.ACCENT_COLOR = col; } 
		}); 
		
		colors.add(new G_HexColor(MainProgram.LIGHT_COLOR) { 
			@Override public void onChangeColor(Vector4f col) { MainProgram.LIGHT_COLOR = col; } 
		}); 
		
		colors.add(new G_HexColor(MainProgram.DARK_COLOR) { 
			@Override public void onChangeColor(Vector4f col) { 
				MainProgram.DARK_COLOR = col;
				GraphicsAPI.clearColor(MainProgram.DARK_COLOR.x, MainProgram.DARK_COLOR.y, MainProgram.DARK_COLOR.z, 0.8f);
				} 
		}); 
		
		colors.add(new G_HexColor(MainProgram.DARKER_COLOR) { 
			@Override public void onChangeColor(Vector4f col) { MainProgram.DARKER_COLOR = col; } 
		}); 
		
		colors.add(new G_HexColor(MainProgram.DARKEST_COLOR) { 
			@Override public void onChangeColor(Vector4f col) { MainProgram.DARKEST_COLOR = col; } 
		}); 
		
		colors.add(new G_HexColor(MainProgram.SEMIDARK_COLOR) { 
			@Override public void onChangeColor(Vector4f col) { MainProgram.SEMIDARK_COLOR = col; } 
		}); 
		
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