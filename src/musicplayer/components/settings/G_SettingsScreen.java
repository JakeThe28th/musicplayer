package musicplayer.components.settings;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.components.settings.types.BooleanSetting;
import musicplayer.components.settings.types.ColorSetting;
import musicplayer.components.settings.types.Setting;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.utility.Rectangle;

public  class G_SettingsScreen extends G_Element implements Screen {
	
	G_Icon 		home 			= new G_Icon("home")
	{ @Override public void onClick() { 
		MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
	}};
	
	G_Text color_header = new G_Text().text("Colors");
	G_Text boolean_header = new G_Text().text("Toggles");

	G_List booleans = new G_List();
	G_List colors = new G_List();
	{ 
		colors.verticalify();
		booleans.verticalify();

		for (String key : Settings.settings.keySet()) {
			Setting setting = Settings.get(key);
			if (setting instanceof ColorSetting) {
				colors.add(new G_HexColor(Settings.getColor(key)) { 
					@Override public void onChangeColor(Vector4f col) { Settings.setColor(key, col); } 
				}); 
			}
			if (setting instanceof BooleanSetting) {
				
				booleans.add(new G_ElementPair(
						new G_Text().text(key),
						new G_Switch(Settings.getBoolean(key)) { 
							@Override public void onChangeValue(boolean b) { Settings.setBoolean(key, b); } 
						},
						0.75f
						)); 
			}
		}
		
		color_header.halign(Alignment.MIDDLE);
		boolean_header.halign(Alignment.MIDDLE);

		home.halign(Alignment.MIDDLE);
	}
	
	{ 
		addSubElement(colors); 
		addSubElement(home); 
		addSubElement(booleans);
		addSubElement(color_header);
		addSubElement(boolean_header);
	}

	public static final G_SettingsScreen INSTANCE = new G_SettingsScreen();

	@Override
	public void recalculate_size() {
		colors.recalculate_size();
		home.recalculate_size();
		booleans.recalculate_size();
		color_header.recalculate_size();
		boolean_header.recalculate_size();
	}
	
	Rectangle section_break;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		
		int yy = top;
		home.layout(left, top, right, top+home.height());
		
		yy += home.height();
		color_header.layout(left, yy, right, yy+color_header.height());
		
		yy += color_header.height();
		colors.layout(left, yy, right, yy+colors.height());
		yy += colors.height();

		yy += 5;
		section_break = new Rectangle(left + 50, yy, right - 50, yy+2);
		yy += 5;

		boolean_header.layout(left, yy, right, yy+boolean_header.height());
		
		yy+= boolean_header.height();
		booleans.layout(left, yy, right, yy+booleans.height());
	}

	@Override
	public void draw(int depth) {
		colors.draw(depth);
		home.draw(depth);
		booleans.draw(depth);
		color_header.draw(depth);
		boolean_header.draw(depth);
		GraphicsAPI.color(Settings.LIGHT_COLOR());
		GraphicsAPI.rect(section_break, depth);
	}

	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return "builtin;settings";}
	
}