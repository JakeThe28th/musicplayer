package musicplayer.gui;

import java.util.ArrayList;

import musicplayer.components.settings.Settings;
import musicplayer.gui.screens.G_HomeScreen;

public class G_Grid extends G_Element {

	ArrayList<G_Element> elements = new ArrayList<G_Element>();
	{
		sub_elements = elements;
	}

	public void add(G_Element element) {
		elements.add(element);
	}
	
	// Overrides TARGET_COLUMN_COUNT if necessary
	public static int MAX_ITEM_SIZE = Settings.max_album_grid_size();
	
	// Overrides TARGET_COLUMN_COUNT if necessary
	public static int MIN_ITEM_SIZE = Settings.min_album_grid_size();
	
	public static int TARGET_COLUMN_COUNT = Settings.album_grid_colum_target();

	int columns = 3;
	int item_size = 10;
		
	@Override
	public void recalculate_size() {
		//for (G_Element e : elements) e.recalculate_size();
		this.unpadded_height = ( (int) Math.ceil(elements.size() / (float) columns) ) * item_size;
	}
	
	@Override
	public void layout(int left, int top, int right, int bottom) {

		columns = TARGET_COLUMN_COUNT;
		
		int xx = left + left_margin;
		int yy = top + top_margin;
		int width = ( (right-right_margin) - xx );

		G_HomeScreen.previous_grid_width = width;
		
		calculate_grid_dimensions(width);
				
		int current_column = 0;
		for (G_Element element : elements) {

			element.layout(xx, yy, xx+item_size, yy+item_size);
			
			current_column++;
			xx += item_size;
			if (current_column >= columns) {
				yy += item_size;
				xx = left + left_margin;
				current_column = 0;
			}
		}

	}

	public void calculate_grid_dimensions(int width) {
		
		if (width < 0) return;
		
		item_size = width / columns;
		while (item_size > MAX_ITEM_SIZE) {
			// item_size = MAX_ITEM_SIZE; <-- wouldn't fill the space properly
			// so instead, find the smallest amount of columns that allow for
			// an item size under the max
			columns++;
			item_size = width / columns;
		}
		
		while (item_size - 1 < MIN_ITEM_SIZE) {
			// item_size = MIN_ITEM_SIZE; <-- ALSO wouldn't fill the space properly
			// so instead, find the largest amount of columns that allow for
			// an item size over the minimum
			columns--;
			item_size = width / columns;
		}
	}

	@Override
	public void draw(int depth) {
		for (G_Element e : elements) e.draw(depth+1);
	}

	public boolean isEmpty() {
		return elements.size() == 0;
	}

	@Override
	public void foo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickAnimation() {
		// TODO Auto-generated method stub
		
	}

}
