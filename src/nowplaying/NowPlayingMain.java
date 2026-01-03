package nowplaying;

import java.io.IOException;
import java.text.ParseException;

import frost3d.GLState;
import frost3d.data.BuiltinShaders;
import frost3d.implementations.SimpleWindow;
import nowplaying.gui.UI;

public class NowPlayingMain {

	public static int 			window_width 	= 512;
	public static int 			window_height 	= 8*96;
	public static SimpleWindow  window;

	public static void main(String[] args) throws IOException, ParseException {
		
		GLState.initializeGLFW();
		window = new SimpleWindow(window_width, window_height, "music thingy");
		BuiltinShaders.init();
		
		UI.init(window);
		
		while (!window.should_close()) {
			UI.tick(window.width(), window.height());
			window.tick();
		}	
		
		GLState.endGLFW();
	}

}
