package musicplayer.components.settings;

import org.joml.Vector4f;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import musicplayer.MainProgram;
import musicplayer.components.settings.types.BooleanSetting;
import musicplayer.components.settings.types.ColorSetting;
import musicplayer.components.settings.types.RangedIntegerSetting;
import musicplayer.components.settings.types.Setting;
import musicplayer.components.settings.types.SongControlsLayoutSetting;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Scrollable;
import musicplayer.gui.G_Slider;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_SettingsScreen extends G_Element implements Screen {
		
	public static final G_SettingsScreen INSTANCE = new G_SettingsScreen();
	@Override public G_Element instance() { return INSTANCE; }
	@Override public String identifier() { return "builtin;settings";}
	
	G_RealSettingsScreen real_screen = new G_RealSettingsScreen();
	G_Scrollable settings_scrollable = new G_Scrollable(real_screen);
	
	{ this.addSubElement(settings_scrollable); }

	@Override public void recalculate_size() 					{ settings_scrollable.recalculate_size(); }
	@Override public void layout(int l, int t, int r, int b) 	{ settings_scrollable.layout(l, t, r, b); }
	@Override public void draw(int depth) 						{ settings_scrollable.draw(depth); 		  }
	
	public void reload() { INSTANCE.real_screen.reload(); }

}

class G_RealSettingsScreen extends G_Element {
	
	G_Icon 		home 			= new G_Icon("home")
	{ @Override public void onClick() { 
		MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
	}};
	
	G_Text color_header = new G_Text().text("Colors");
	G_Text boolean_header = new G_Text().text("Toggles");
	G_Text slider_header = new G_Text().text("Sliders");
	G_Text misc_header = new G_Text().text("Miscellaneous");

	G_List booleans;
	G_List colors;
	G_List ranged_integers;
	
	G_SongControlsLayout song_controls_layout;
	
	G_Text export_colors_text = new G_Text() {
		@Override public void onClick() {
			String export_path = TinyFileDialogs.tinyfd_saveFileDialog(
					"Export Colors", 
					"colors.txt", 
					null,
					MainProgram.PROGRAM_NAME + " setting files");
			
			if (export_path != null) {
				Settings.save(export_path, Settings.onlyColorSettings());
			}
		}
	};
	
	G_Text import_colors_text = new G_Text() {
		@Override public void onClick() {
			String import_path = TinyFileDialogs.tinyfd_openFileDialog(
					"Import Colors", 
					"colors.txt", 
					null,
					MainProgram.PROGRAM_NAME + " setting files",
					false);
			
			if (import_path != null) {
				Settings.load(import_path);
			}
		}
	};
	
	G_ElementPair export_import_colors = new G_ElementPair(export_colors_text, import_colors_text, 0.5f);
	
	{ 
		export_colors_text.can_click = true;
		export_colors_text.text("Export Colors");
		
		import_colors_text.can_click = true;
		import_colors_text.text("Import Colors");
		
		reload(); 
	}
	
	public void reload() {
		sub_elements.clear();
		
		booleans = new G_List();
		colors = new G_List();
		ranged_integers = new G_List();
		
		song_controls_layout = null;
		
		// -- -=- -- //
		
		colors.verticalify();
		booleans.verticalify();
		ranged_integers.verticalify();
		
		colors.add(export_import_colors);
		
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
			if (setting instanceof SongControlsLayoutSetting) {
				if (song_controls_layout != null) {
					removeSubElement(song_controls_layout);
				}
				song_controls_layout = new G_SongControlsLayout((SongControlsLayoutSetting) setting);
				addSubElement(song_controls_layout);
			}
		}
		
		color_header.halign(Alignment.MIDDLE);
		boolean_header.halign(Alignment.MIDDLE);
		slider_header.halign(Alignment.MIDDLE);
		misc_header.halign(Alignment.MIDDLE);

		home.halign(Alignment.MIDDLE);
		
		addSubElement(colors); 
		addSubElement(home); 
		addSubElement(booleans);
		addSubElement(color_header);
		addSubElement(boolean_header);
		addSubElement(slider_header);
		addSubElement(ranged_integers);
		addSubElement(misc_header);

	}

	@Override
	public void recalculate_size() {
		colors.recalculate_size();
		home.recalculate_size();
		booleans.recalculate_size();
		color_header.recalculate_size();
		boolean_header.recalculate_size();
		ranged_integers.recalculate_size();
		slider_header.recalculate_size();
		if (song_controls_layout != null) song_controls_layout.recalculate_size();

		this.unpadded_height = 
				  colors.height() 
				+ home.height() 
				+ booleans.height() 
				+ color_header.height() 
				+ boolean_header.height() 
				+ ranged_integers.height() 
				+ slider_header.height()
				+ misc_header.height()
				+ (song_controls_layout != null ? song_controls_layout.height() : 0)
				+ 10;
	}
	
	Rectangle color_toggle_section_break;
	Rectangle toggle_slider_section_break;
	Rectangle misc_section_break;

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
		
		// Misc
		
		yy += 5;
		misc_section_break = new Rectangle(left + 50, yy, right - 50, yy+2);
		yy += 5;

		misc_header.layout(left, yy, right, yy+misc_header.height());
		
		yy+= misc_header.height();
		
		if (song_controls_layout != null) {
			song_controls_layout.layout(left, yy, right, yy+song_controls_layout.height());
			yy += song_controls_layout.height();
		}
		

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
		misc_header.draw(depth);
		if (song_controls_layout != null) song_controls_layout.draw(depth);
		GraphicsAPI.color(Settings.LIGHT_COLOR());
		GraphicsAPI.rect(color_toggle_section_break, depth);
		GraphicsAPI.rect(toggle_slider_section_break, depth);
		GraphicsAPI.rect(misc_section_break, depth);

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

}