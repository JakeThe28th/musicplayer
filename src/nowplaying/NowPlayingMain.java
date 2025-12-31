package nowplaying;

import java.io.IOException;
import java.text.ParseException;

import disaethia.io.nbt.NBTCompound;
import disaethia.io.nbt.NBTTag;
import frost3d.GLState;
import frost3d.data.BuiltinShaders;
import frost3d.implementations.SimpleWindow;
import nowplaying.gui.screens.HomeScreen;
import nowplaying.settings.gui.SettingsScreen;
import snowui.GUIInstance;
import snowui.coss.ComposingStyleSheet;

public class NowPlayingMain {

	public static int 			window_width 	= 512;
	public static int 			window_height 	= 8*96;
	public static SimpleWindow  window;
	
	public static void main(String[] args) throws IOException, ParseException {
		
		GLState.initializeGLFW();
		window = new SimpleWindow(window_width, window_height, "music thingy");
		BuiltinShaders.init();
		
		GUIInstance gui = new GUIInstance(window, window.input());
		
		//gui.root(HomeScreen.instance());
		
		gui.root(SettingsScreen.instance());
		
		while (!window.should_close()) {
			
			gui.style(ComposingStyleSheet.from((NBTCompound) NBTTag.readUnnamedSNBTFromFile("default_style.snbt")));
			
			gui.size(window.width(), window.height());
			
			gui.render();
			
			window.tick();
		}
		
	}

}
