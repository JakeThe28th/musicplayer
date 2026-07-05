package nowplaying.gui.elements;

import java.util.ArrayList;

import frost3d.enums.IconType;
import nowplaying.parts.data.Playlist;
import nowplaying.settings.Settings;
import snowui.GUIInstance;
import snowui.elements.abstracts.GUIElement;
import snowui.elements.base.GUIIcon;
import snowui.elements.base.GUIList;
import snowui.elements.base.GUIText;

public class GUIPlaylistGroup extends GUIElement {
	
	ArrayList<GUIPlaylistCover> playlists = new ArrayList<>();
	
	GUIList title_elements = new GUIList();
	
	{
		identifier("playlist_group");
		
		registerSubElement(title_elements);
			title_elements.identifier("playlist_group_title_elements");
			title_elements.horizontalify();
			title_elements.add(new GUIText("Title"));
			title_elements.add(new GUIIcon(IconType.GENERIC_ARROW_DOWN));

	}
	
	public void addPlaylist(Playlist playlist) {
		playlists.add(new GUIPlaylistCover(playlist));
		registerSubElement(playlists.getLast());
	}

	@Override
	public void recalculateSize(GUIInstance gui) {
		this.unpadded_height = ( (int) Math.ceil(playlists.size() / (float) columns) ) * item_size;
		this.unpadded_height += title_elements.height();
		this.unpadded_width = columns * item_size;
	}

	@Override
	public void updateDrawInfo(GUIInstance gui) {

		hover_rectangle(padded_limit_rectangle());
		
		int xx 		= padded_limit_rectangle().left	 ();
		int yy 		= padded_limit_rectangle().top	 ();
		int width 	= padded_limit_rectangle().width ();
		
		title_elements.limit_rectangle(xx, yy, padded_limit_rectangle().right(), yy + title_elements.height());
		yy += title_elements.height();
		
		calculate_grid_dimensions(width);
				
		int current_column = 0;
		for (GUIPlaylistCover playlist : playlists) {
			playlist.limit_rectangle(xx, yy, xx + item_size, yy + item_size);
			current_column++;
			xx += item_size;
			if (current_column >= columns) {
				yy += item_size;
				xx = padded_limit_rectangle().left();
				current_column = 0;
			}
		}
		
	}

	@Override
	public void draw(GUIInstance gui, int depth) {
		
	}
	
	// ------- calculate grid dimensions stuff (copied from old code) ------- //

	public static int MAX_ITEM_SIZE 		= Settings.max_album_grid_size		(); // Overrides TARGET_COLUMN_COUNT if necessary
	public static int MIN_ITEM_SIZE 		= Settings.min_album_grid_size		(); // Overrides TARGET_COLUMN_COUNT if necessary
	public static int TARGET_COLUMN_COUNT 	= Settings.album_grid_colum_target	();

	int columns = 3;
	int item_size = 10;
	
	public void calculate_grid_dimensions(int width) {
		
		int last_item_size = item_size;
		int last_columns = columns;
		
		if (width <= 0) return;
		
		columns = TARGET_COLUMN_COUNT;
		
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
			if (columns == 1) break;
			columns--;
			item_size = width / columns;
		}
		
		if (item_size != last_item_size || columns != last_columns) {
			this.should_recalculate_size(true);
		}
		
	}

}