package musicplayer.utility;

import musicplayer.graphics.GraphicsAPI;

public record Rectangle(int left, int top, int right, int bottom) {
	public boolean contains(int x, int y) {
		return (x > left && x < right) && (y > top && y < bottom);
	} }
