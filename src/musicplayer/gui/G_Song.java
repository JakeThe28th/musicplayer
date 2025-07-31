package musicplayer.gui;

public class G_Song implements G_Element {
	
	public G_Song(String n) {
		name = n;
	}
	
	String name;

	public void draw(int left, int top, int right, int bottom, int depth) {
		GraphicsHandler.text(left, top, depth, name);
	}

}
