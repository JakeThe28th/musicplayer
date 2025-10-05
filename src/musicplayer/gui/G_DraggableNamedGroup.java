package musicplayer.gui;

import org.joml.Vector4f;

import musicplayer.components.settings.Settings;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.parts.Library;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;
import musicplayer.utility.Utility;

public class G_DraggableNamedGroup extends G_Element {
	
	String group_name;
	G_Text name = new G_Text();
	G_Icon up_button = new G_Icon("previous") {
		@Override public void onClick() {
			Library.album_group_order = Utility.relative_swap(Library.album_group_order, group_name, -1);
			Library.save_group_order();
			G_HomeScreen.update_playlist_views();
		}
	};
	G_Icon down_button = new G_Icon("next") {
		@Override public void onClick() {
			Library.album_group_order = Utility.relative_swap(Library.album_group_order, group_name, 1);
			Library.save_group_order();
			G_HomeScreen.update_playlist_views();
		}
	};
	G_Element root;
	
	G_List move_icons = new G_List(up_button, down_button).verticalify();
	
	int base_drag_width = 40;
	int drag_width = 0;
	int min_element_height = 40;
		
	{
		addSubElement(name);
		addSubElement(move_icons);
		up_button.icon_size = 0.75;
		down_button.icon_size = 0.75;
		move_icons.valign(Alignment.MIDDLE);
		name.halign(Alignment.MIDDLE);
		base_drag_width = (int) (up_button.width()*2.75);
		drag_width = base_drag_width;
	}
	
	public G_DraggableNamedGroup(String name, G_Element element) {
		group_name = name;
		this.name.text(name);
		root(element);
	}
	
	private void root(G_Element element) {
		if (this.root != null) removeSubElement(root);
		root = element;
		addSubElement(element);
	}


	@Override
	public void recalculate_size() {
		name.recalculate_size();
		move_icons.recalculate_size();
		root.recalculate_size();
		
		int element_height = ( min_element_height > root.height()) ? min_element_height : root.height();
		this.unpadded_height = name.height() + element_height;
		
		this.unpadded_width = drag_width + name.width();
	}
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		if (!G_HomeScreen.editing_transition_complete()) {
			up_button.base_color = new Vector4f(1, 1, 1, G_HomeScreen.current_edit_anim_time());
			down_button.base_color = new Vector4f(1, 1, 1, G_HomeScreen.current_edit_anim_time());
			drag_width = (int) Utility.lerp(0, base_drag_width, G_HomeScreen.current_edit_anim_time());
		}

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		//this.hover_rectangle = new Rectangle(left, top, right, bottom);
		this.hover_rectangle = new Rectangle(0,0,0,0);
		
		name.layout(left+drag_width, top, right, top+name.height());
		move_icons.layout(left, top+name.height(), left+drag_width, bottom);
		
		//Log.send(top, bottom);
		root.layout(left+drag_width, top+name.height(), right, bottom);
		
	}
	
	public int drag_area_width() {
		return drag_width;
	}
	
	@Override
	public void draw(int depth) {
		root.draw(depth+1);
		name.draw(depth+2);
		if (G_HomeScreen.editing()) {
			move_icons.draw(depth+2);
		}
	}

	
	
	

}
