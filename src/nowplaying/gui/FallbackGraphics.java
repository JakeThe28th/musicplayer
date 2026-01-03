package nowplaying.gui;

import org.joml.Vector2i;
import org.joml.Vector4f;

import frost3d.implementations.SimpleCanvas;
import frost3d.implementations.SimpleTextRenderer;
import nowplaying.NowPlayingMain;

public class FallbackGraphics {
	
	public static SimpleCanvas  canvas;
	
	public static void init() {
		canvas = new SimpleCanvas();
		SimpleTextRenderer tr = new SimpleTextRenderer();
		tr.anti_aliasing_enabled(true);
		canvas.textrenderer(tr);
		canvas.clear_color(0,0,0,1);
		canvas.color(new Vector4f(1,1,1,1));
	}

	public static void center_text(int depth, int y_offset, String string) {
		int width = NowPlayingMain.window.width;
		int height = NowPlayingMain.window.height;
		canvas.size(width, height);
		canvas.text((width/2)-(size(string).x/2), ((height/2)-(size(string).y/2)) + y_offset, depth, string);
	}

	public static void 		end_text() 			{ canvas.draw_frame(); NowPlayingMain.window.tick(); }
	public static Vector2i 	size(String text) 	{ return canvas.textrenderer().size(text); }

}
