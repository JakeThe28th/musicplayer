package musicplayer.gui;

public class G_Song implements G_Element {
	
	public G_Song(String n) {
		name = n;
	}
	
	String name;

	public void draw(int left, int top, int right, int bottom, int depth) {
		
		int text_width = GraphicsHandler.size(name).x;

		if (text_width > (right-left)) {
			text_width += 30;
			// scroll text
			int time = (int) Math.floorMod((System.currentTimeMillis() / 20), text_width);
			GraphicsHandler.text(left+time, top, depth, name);
			GraphicsHandler.text((left-text_width)+time, top, depth, name);

		} else {
			GraphicsHandler.text(left, top, depth, name);
		}
		
		
	}

	@Override
	public int height() {
		return GraphicsHandler.size(name).y;
	}

}
