package musicplayer.extensions.builtin;

import java.io.IOException;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.extensions.Extension;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Slider;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.utility.Rectangle;

public class ProgramSettings extends Extension  {
	
	static class G_Color extends G_Element {

		G_Slider 	red 		= new G_Slider();
		G_Slider 	green 		= new G_Slider();
		G_Slider 	blue 		= new G_Slider();
		G_Text 		red_text 	= new G_Text("Red");
		G_Text 		green_text 	= new G_Text("Green");
		G_Text 		blue_text 	= new G_Text("Blue");
		
		public static final int SLIDER_HEIGHT = 30;
		
		{ addSubElement(red); addSubElement(green); addSubElement(blue); 
		  addSubElement(red_text); addSubElement(green_text); addSubElement(blue_text); }

		@Override
		public void recalculate_size() {
			for (G_Element e : sub_elements) {
				e.recalculate_size();
			}
			this.unpadded_height = SLIDER_HEIGHT * 5;
		}
		
		Rectangle color_rect;

		@Override
		public void layout(int left, int top, int right, int bottom) {
			left += left_margin;
			right -= right_margin;
			top += top_margin;
			bottom -= bottom_margin;

			int yy = top;
			int x_offset = GraphicsAPI.size("Green").x + 10;
			
			yy+=SLIDER_HEIGHT;
			red.layout(left + x_offset, yy, right, yy+SLIDER_HEIGHT);
			red_text.layout(left, yy, left + x_offset, yy+SLIDER_HEIGHT);

			yy+=SLIDER_HEIGHT;
			green.layout(left + x_offset, yy, right, yy+SLIDER_HEIGHT);
			green_text.layout(left, yy, left + x_offset, yy+SLIDER_HEIGHT);

			yy+=SLIDER_HEIGHT;
			blue.layout(left + x_offset, yy, right, yy+SLIDER_HEIGHT);
			blue_text.layout(left, yy, left + x_offset, yy+SLIDER_HEIGHT);
			
			yy+=SLIDER_HEIGHT;
			color_rect = new Rectangle(left, yy, right, bottom);

		}

		@Override
		public void draw(int depth) {
			for (G_Element e : sub_elements) {
				e.draw(depth + 1);
			}
			
			GraphicsAPI.color(new Vector4f(
					((float) red.amount),
					((float) green.amount),
					((float) blue.amount),
					1));
			GraphicsAPI.rect(color_rect, depth + 1);
		}
		
	}
	
	static class G_SettingsScreen extends G_Element implements Screen {
		
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
	
	G_SettingsScreen settings = G_SettingsScreen.INSTANCE;

	@Override public String   identifier() 		{ return "builtin;settings"; }

	@Override
	public void onLoad() throws IOException {
		MainProgram.registerScreen(settings);
		G_HomeScreen.addMenuOption(new Option(
				"Settings",
				() -> { MainProgram.change_screen(settings.identifier()); }
				));
	}

}
