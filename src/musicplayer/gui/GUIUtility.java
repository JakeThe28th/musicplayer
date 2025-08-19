package musicplayer.gui;

import musicplayer.gui.enums.Alignment;

public class GUIUtility {
	
	// EX: less = left, greater = right, size = width
	public static int getAlignmentOffset(int less, int greater, int size, Alignment align) {
		if (align == Alignment.MIDDLE) {
			return ((greater-less) - size) / 2;
		}
		if (align == Alignment.RIGHT) {
			return ((greater-less) - size);
		}
		return 0;
	}

}
