package musicplayer.gui;

import java.io.IOException;

import org.joml.Vector4f;

import disaethia.engine.graphics.Camera;
import disaethia.engine.graphics.GLState;
import disaethia.engine.graphics.Window;
import disaethia.engine.graphics.records.WindowHint;
import disaethia.engine.rendering.QueuedRenderer;
import disaethia.libraries.snow2d.text.PixelTextRenderer;

/** It's probably overkill to use the GUI library here so,
 *  making it its' own thing... */
public class GraphicsHandler {
	
	static Window gui_window;
	static Camera gui_camera;
	static PixelTextRenderer gui_text;
	
	public static boolean is_open = true;
	
	static int width = 512;
	static int height = 8*96;
	
	public static void init() throws IOException {
		GLState.initializeGLFW();
		
		gui_text = new PixelTextRenderer();
		gui_window = new Window("MusicPlayer", width, height, new WindowHint[] { 
				WindowHint.decorated(false),
				WindowHint.transparentFramebuffer(true)
				});
		gui_camera = new Camera(gui_window);
		
		gui_window.clearColor(0, 0, 0, .82f);
		
		int console_window_width = gui_window.width()/2;
		int console_window_height = gui_window.height()/2;

		gui_camera.ortho(console_window_width, console_window_height);
		QueuedRenderer.setWorld(gui_camera.getMatrix());
		
		gui_text.size(8);

	}
	
	public static int width() { return width; }
	public static int height() { return height; }

	public static boolean isOpen() {
		return is_open;
	}
	
	public static void clear() {
		gui_window.clear();
	}
	
	public static void refresh() {
		QueuedRenderer.render();
		gui_window.tick();
	}
	
	static /* -- Drawing stuffs -- */
	
	Vector4f color = new Vector4f(1,1,1,1);
	
	public static void color(float r, float g, float b, float a) {
		color = new Vector4f(r,g,b,a);
		gui_text.color(color);
	}
	
	public static void text(int x, int y, int depth, String text) {
		gui_text.renderText(x, y, depth, text);
	}

	public static void rect(int left, int top, int right, int bottom, int depth) {
		
	}
	
	/** Saves the current scissor and then sets the new scissor to this. */
	public static void push_scissor(int left, int top, int right, int bottom) {
		
	}

	/** Reverts to the previous scissor box. */
	public static void pop_scissor() {
		// TODO Auto-generated method stub
		
	}
	
}
