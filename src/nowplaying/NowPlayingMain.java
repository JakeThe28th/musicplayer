package nowplaying;

import java.io.IOException;
import java.text.ParseException;

import javax.imageio.ImageIO;

import frost3d.GLState;
import frost3d.data.BuiltinShaders;
import frost3d.implementations.SimpleWindow;
import nowplaying.extensions.ExtensionAPI;
import nowplaying.gui.FallbackGraphics;
import nowplaying.gui.UI;
import nowplaying.parts.Library;
import nowplaying.parts.MusicPlayer;
import nowplaying.parts.data.Album;
import nowplaying.parts.data.Playlist;

public class NowPlayingMain {
	
	public static final String VERSION = "0.5.0a";
	public static final String PROGRAM_NAME = "NOW PLAYING";
	public static final String PROGRAM_TITLE = PROGRAM_NAME + " " + VERSION;

		   static int 			window_initial_width 	= 512;
		   static int 			window_initial_height 	= 8*96;
	public static SimpleWindow  window;
	
	public static void title(String string) { window.title(string); }

	public static void main(String[] args) throws IOException, ParseException {
		
		ImageIO.setUseCache(false);
		
		GLState.initializeGLFW();
			window = new SimpleWindow(window_initial_width, window_initial_height, "music thingy");
			BuiltinShaders.init();
			FallbackGraphics.init();
			
		try {
			// Startup sequence
			title(PROGRAM_TITLE);
			
			FallbackGraphics.center_text(0, 0, "Initializing Audio...");
			FallbackGraphics.end_text();
			MusicPlayer.initAudioDevice();
			
			FallbackGraphics.center_text(0, 0, "Initializing Extensions (1/2) ...");
			FallbackGraphics.end_text();
			ExtensionAPI.init();
	
			FallbackGraphics.center_text(0, 0, "Loading songs...");
			FallbackGraphics.end_text();
			Library.init();
			
			FallbackGraphics.center_text(0, 0, "Initializing Extensions (2/2) ...");
			ExtensionAPI.postLibraryLoaded();
			FallbackGraphics.end_text();

			FallbackGraphics.center_text(0, 0, "Initializing GUI");
			FallbackGraphics.end_text();
			UI.init(window);
			
			// Where Most Of The Program Actually Happens
			while (!window.should_close()) {
				UI.tick(window.width(), window.height());
				window.tick();
			}	
		} finally {
			// Exit sequence
			FallbackGraphics.center_text(10, 0, "Saving playlists");
			FallbackGraphics.end_text();
			for (Playlist p : Library.listPlaylists()) { p.save(); }

			FallbackGraphics.center_text(10, 0, "Saving albums");
			FallbackGraphics.end_text();
			for (Album a : Library.listAlbums()) { a.linked_playlist.save(); }

			FallbackGraphics.center_text(10, 0, "Closing extensions");
			FallbackGraphics.end_text();
			ExtensionAPI.end();
			
			FallbackGraphics.center_text(10, 0, "Closing audio device");
			FallbackGraphics.end_text();
			MusicPlayer.endAudioDevice();
			
			GLState.endGLFW();
		}

	}

}
