package musicplayer.components.settings;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.components.settings.types.BooleanSetting;
import musicplayer.components.settings.types.ColorSetting;
import musicplayer.components.settings.types.RangedIntegerSetting;
import musicplayer.components.settings.types.Setting;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Slider;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public  class G_SettingsScreen extends G_Element implements Screen {
	
	G_Icon 		home 			= new G_Icon("home")
	{ @Override public void onClick() { 
		MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
	}};
	
	G_Text color_header = new G_Text().text("Colors");
	G_Text boolean_header = new G_Text().text("Toggles");
	G_Text slider_header = new G_Text().text("Sliders");

	G_List booleans;
	G_List colors;
	G_List ranged_integers;
	
	{ reload(); }
	
	public void reload() {
		sub_elements.clear();
		
		booleans = new G_List();
		colors = new G_List();
		ranged_integers = new G_List();
		
		// -- -=- -- //
		
		colors.verticalify();
		booleans.verticalify();
		ranged_integers.verticalify();
		
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
						0.5f
						)); 
			}
			if (setting instanceof RangedIntegerSetting) {
				G_Slider slider = new G_Slider() { 
					@Override public void onDrag(double newvalue) {
						RangedIntegerSetting value = Settings.getRangedInteger(key);
						value.percentage(newvalue);
						Settings.setRangedInteger(key, value);
					} 
					@Override protected String amountFormatted() {
						return Settings.getRangedInteger(key).value + "";
					}
				};
				slider.amount = Settings.getRangedInteger(key).percentage();
				
				ranged_integers.add(new G_ElementPair(
						new G_Text().text(key),
						slider,
						0.5f
						)); 
			}
		}
		
		color_header.halign(Alignment.MIDDLE);
		boolean_header.halign(Alignment.MIDDLE);
		slider_header.halign(Alignment.MIDDLE);
		
		home.halign(Alignment.MIDDLE);
		
		addSubElement(colors); 
		addSubElement(home); 
		addSubElement(booleans);
		addSubElement(color_header);
		addSubElement(boolean_header);
		addSubElement(slider_header);
		addSubElement(ranged_integers);
	}

	public static final G_SettingsScreen INSTANCE = new G_SettingsScreen();

	@Override
	public void recalculate_size() {
		colors.recalculate_size();
		home.recalculate_size();
		booleans.recalculate_size();
		color_header.recalculate_size();
		boolean_header.recalculate_size();
		ranged_integers.recalculate_size();
		slider_header.recalculate_size();
	}
	
	Rectangle color_toggle_section_break;
	Rectangle toggle_slider_section_break;

	int colors_width;
	Rectangle color_preview_area;
	
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
		
		colors_width = (int) ((right-left) * 0.75);
		colors.layout(left, yy, left+colors_width, yy+colors.height());
		
		// preview stuff
		int color_preview_width = (right-(left+colors_width))-10;
		float ratio = 1.75f;
		int color_preview_height = (int) (color_preview_width * ratio);
		if (color_preview_height > colors.height() - 20) {
			color_preview_height = colors.height() - 20;
			color_preview_width = (int) (color_preview_height / ratio);
		}
		int color_preview_y = yy + (colors.height()/2);
		int color_preview_x = left+colors_width + ((right-(left+colors_width))/2);

		color_preview_area = new Rectangle(
				color_preview_x - (color_preview_width/2),
				color_preview_y-(color_preview_height/2),
				color_preview_x + (color_preview_width/2),
				color_preview_y+(color_preview_height/2)
				);
		// preview stuff
		
		yy += colors.height();

		// Booleans
		
		yy += 5;
		color_toggle_section_break = new Rectangle(left + 50, yy, right - 50, yy+2);
		yy += 5;

		boolean_header.layout(left, yy, right, yy+boolean_header.height());
		
		yy+= boolean_header.height();
		booleans.layout(left, yy, right, yy+booleans.height());
		yy += booleans.height();
		
		// Sliders
		
		yy += 5;
		toggle_slider_section_break = new Rectangle(left + 50, yy, right - 50, yy+2);
		yy += 5;

		slider_header.layout(left, yy, right, yy+slider_header.height());
		
		yy+= slider_header.height();
		
		ranged_integers.layout(left, yy, right, yy+ranged_integers.height());
		yy += ranged_integers.height();

	}

	@Override
	public void draw(int depth) {
		colors.draw(depth);
		home.draw(depth);
		booleans.draw(depth);
		color_header.draw(depth);
		boolean_header.draw(depth);
		slider_header.draw(depth);
		ranged_integers.draw(depth);
		GraphicsAPI.color(Settings.LIGHT_COLOR());
		GraphicsAPI.rect(color_toggle_section_break, depth);
		GraphicsAPI.rect(toggle_slider_section_break, depth);
		
		// preview stuff vvv
		
		GraphicsAPI.color(Settings.DARKEST_COLOR());
		GraphicsAPI.rect(color_preview_area, depth+1);
		Rectangle box = color_preview_area.decrease(2);
		// top playlist bar, song controls
		GraphicsAPI.color(Settings.LIGHT_COLOR());
		GraphicsAPI.rect(box, depth+3);
		// text on those
		GraphicsAPI.color(GraphicsAPI.WHITE);
		Rectangle top_box = box.internal(0, 0, 1, 0.15f);
		GraphicsAPI.rect(top_box.thin_vertically(0.35).internal(0.05f, 0, 0.1f, 1), depth+4);
		GraphicsAPI.rect(top_box.thin_vertically(0.35).internal(0.13f, 0, 0.95f, 1), depth+4);

		// background
		GraphicsAPI.color(Settings.DARK_COLOR());
		GraphicsAPI.rect(box.thin_vertically(0.15), depth+5);
		// scrollbar
		GraphicsAPI.color(Settings.DARKEST_COLOR());
		GraphicsAPI.rect(box.thin_vertically(0.17).internal(0.01f, 0, 0.1f, 1), depth+5);
		GraphicsAPI.color(Settings.ACCENT_COLOR());
		GraphicsAPI.rect(box.thin_vertically(0.19).internal(0.04f, 0, 0.08f, 0.5f), depth+6);
		// alternating list colors
		
		int amount = 5;
		int hh = (box.thin_vertically(0.17).height()) / amount;
		int yy = box.thin_vertically(0.17).top();
		Rectangle scb = box.internal(0.1f, 0, 1, 1).thin_horizontally(0.1);
		for (int i = 0; i < amount; i++) {
			if (i % 2 == 0) {
				GraphicsAPI.color(Settings.DARKER_COLOR());
				GraphicsAPI.rect(scb.left(), yy+6, scb.right(), yy+hh-6, depth+7);
			}
			GraphicsAPI.color(Settings.SEMIDARK_COLOR());
			GraphicsAPI.rect(scb.internal(0.80f, 0, 1, 1).left(), yy+9, scb.right()-10, yy+hh-9, depth+7);
			GraphicsAPI.color(GraphicsAPI.WHITE);
			GraphicsAPI.rect(scb.left()+4, yy+12, scb.internal(0.80f, 0, 1, 1).left()-10, yy+hh-12, depth+8);
			yy += hh;
		}
		
	}

	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return "builtin;settings";}

}