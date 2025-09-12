package musicplayer.utility;

import musicplayer.graphics.GraphicsAPI;

public record Rectangle(int left, int top, int right, int bottom) {
	public boolean contains(int x, int y) {
		return (x > left && x < right) && (y > top && y < bottom);
	}

	// i should have made this earlier
	public int height() {
		return bottom - top;
	}

	public int width() {
		return right - left;
	}
	
	public boolean isHovered() {
		return contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY());
	}

	public Rectangle decrease(int i) {
		return new Rectangle(left + i, top + i, right - i, bottom - i);
	}

	public Rectangle thin_horizontally(int i) {
		return new Rectangle(left + i, top, right - i, bottom);
	} 
	
	}
