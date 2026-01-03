package nowplaying;

import java.io.IOException;
import java.text.ParseException;

import disaethia.io.nbt.NBTCompound;
import disaethia.io.nbt.NBTTag;
import frost3d.GLState;
import frost3d.data.BuiltinShaders;
import frost3d.implementations.BitmapIconRenderer;
import frost3d.implementations.SimpleWindow;
import nowplaying.gui.GUIScreenContainer;
import nowplaying.gui.screens.HomeScreen;
import nowplaying.settings.Settings;
import nowplaying.settings.gui.SettingsScreen;
import snowui.GUIInstance;
import snowui.coss.ComposingStyleSheet;

public class NowPlayingMain {

	public static int 			window_width 	= 512;
	public static int 			window_height 	= 8*96;
	public static SimpleWindow  window;
	
	public static GUIScreenContainer screen_container;
	
	static GUIInstance gui;
	
	public static void main(String[] args) throws IOException, ParseException {
		
		GLState.initializeGLFW();
		window = new SimpleWindow(window_width, window_height, "music thingy");
		BuiltinShaders.init();
		
		gui = new GUIInstance(window, window.input());
		
		gui.fps.show_fps = false;
		
		//gui.root(HomeScreen.instance());
		
		//gui.root(SettingsScreen.instance());
		
		screen_container = new GUIScreenContainer(HomeScreen.instance());
		gui.root(screen_container);
		
		BitmapIconRenderer icons = new BitmapIconRenderer();
		gui.iconrenderer(icons);
		icons.centered_scale(1f);
		
		reloadStyle();

//		TODO: update everything to use background_color
//		
//		also commit that to snowui
//		
		while (!window.should_close()) {
			
			reloadStyle();

			gui.size(window.width(), window.height());
			
			gui.render();
			
			window.tick();
		}
		
	}

	public static void setFontSize(int size) {
		gui.style().setProperty("font_size", "size", String.valueOf(size));
		gui.force_update_all();
	}
	
	public static void setIconSize(int size) {
		gui.style().setProperty("icon_size", "size", String.valueOf(size));
		gui.force_update_all();
	}
	
	public static void reloadStyle() throws IOException, ParseException {
		gui.style(ComposingStyleSheet.from((NBTCompound) NBTTag.readUnnamedSNBTFromFile("default_style.snbt")));
		gui.style().setProperty("slider", "base_color", null);
		gui.style().setProperty("slider_handle", "base_color", null);
		gui.style().setProperty("text", "size", null);
		setFontSize(Settings.font_size());
		setIconSize((int) (Settings.font_size() * 1.555555555555555555555555555555555555555));
	}

}
