package nowplaying.gui;

import java.io.IOException;
import java.text.ParseException;

import disaethia.io.nbt.NBTCompound;
import disaethia.io.nbt.NBTTag;
import frost3d.enums.IconType;
import frost3d.implementations.BitmapIconRenderer;
import frost3d.implementations.SimpleWindow;
import frost3d.utility.Log;
import nowplaying.gui.abstracts.Screen;
import nowplaying.gui.screens.GUIScreenContainer;
import nowplaying.gui.screens.HomeScreen;
import nowplaying.parts.data.UUID;
import nowplaying.settings.Settings;
import snowui.GUIInstance;
import snowui.coss.ComposingStyleSheet;
import snowui.elements.interfaces.FloatingElement;

public class UI {

	static GUIScreenContainer 	screen_container;
	static GUIInstance 			gui;
	
	public static void init(SimpleWindow window) throws IOException, ParseException {
		screen_container = new GUIScreenContainer(HomeScreen.instance());

		BitmapIconRenderer icons = new BitmapIconRenderer();
		icons.centered_scale(1f);
		
		gui = new GUIInstance(window, window.input());
			gui.fps.show_fps = false;
			gui.root(screen_container);
			gui.iconrenderer(icons);

		reloadStyle();
	}
	
	public static void tick(int width, int height) throws IOException, ParseException {
		if (Settings.auto_reload_theme()) reloadStyle();
		gui.size(width, height);
		gui.render();
	}
	
	// ................................... //
	
	public static void setFontSize(int size) {
		gui.style().setProperty("font_size", "size", String.valueOf(size));
		gui.force_update_all();
	}
	
	public static void setIconSize(int size) {
		gui.style().setProperty("icon_size", "size", String.valueOf(size));
		gui.force_update_all();
	}
	
	public static void reloadStyle() throws IOException, ParseException {
		try {
			gui.style(ComposingStyleSheet.from((NBTCompound) NBTTag.readUnnamedSNBTFromFile("default_style.snbt")));
			gui.style().setProperty("slider", "base_color", null);
			gui.style().setProperty("slider_handle", "base_color", null);
			gui.style().setProperty("text", "size", null);
			setFontSize(Settings.font_size());
			setIconSize((int) (Settings.font_size() * 1.555555555555555555555555555555555555555));
		} catch (Exception e) {
			Log.trace(e);
			showError("Failed to read Stylesheet.");
		}
	}

	public static void set_current_screen(Screen screen) {
		screen_container.current(screen);
	}

	public static void showError(String error_text) {
		// TODO Auto-generated method stub
		Log.send("TODO: Show Errors: " + error_text);
	}

	public static void set_current_song(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

	public static void setPlaying(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public static void set_shuffle_icon(IconType icon) {
		// TODO Auto-generated method stub
		
	}

	public static void addWindow(FloatingElement element) {
		gui.add_window(element);
	}
	
}
