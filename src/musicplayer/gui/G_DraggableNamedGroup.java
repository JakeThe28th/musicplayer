package musicplayer.gui;

import org.joml.Vector4f;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import musicplayer.MainProgram;
import musicplayer.components.settings.Settings;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.parts.Album;
import musicplayer.parts.Library;
import musicplayer.parts.Playlist;
import musicplayer.utility.Rectangle;
import musicplayer.utility.Utility;

public class G_DraggableNamedGroup extends G_Element {
	
	String group_name;
	G_Text name = new G_Text();
	G_Icon up_button = new G_Icon("up") {
		@Override public void onClick() {
			Library.album_group_order = Utility.relative_swap(Library.album_group_order, group_name, -1);
			Library.save_group_order();
			G_HomeScreen.update_playlist_views();
		}
	};
	G_Icon down_button = new G_Icon("down") {
		@Override public void onClick() {
			Library.album_group_order = Utility.relative_swap(Library.album_group_order, group_name, 1);
			Library.save_group_order();
			G_HomeScreen.update_playlist_views();
		}
	};
	G_Icon delete = new G_Icon("giant_trash") {
		@Override public void onClick() {
			boolean remove = TinyFileDialogs.tinyfd_messageBox(
					" " + MainProgram.PROGRAM_TITLE, 
					"Really ungroup " + group_name + "?", 
					"yesno", 
					"warning", 
					false);
			
			if (remove) {
				if (G_HomeScreen.current_tab == G_HomeScreen.Tab.ALBUMS) {
					for (Album a : Library.listAlbums()) {
						if (group_name.equals(a.linked_playlist.metadata("group"))) {
							a.linked_playlist.metadata("group", null);
						}
					}
					Library.album_group_order = Utility.remove(group_name, Library.album_group_order);
					Library.save_group_order();
				} else {
					for (Playlist p : Library.listPlaylists()) {
						if (!p.is_album && group_name.equals(p.metadata("group"))) p.metadata("group", null);
					}
					Library.playlist_group_order = Utility.remove(group_name, Library.playlist_group_order);
					Library.save_group_order();
				}
				G_HomeScreen.update_playlist_views();
			}
		}
	};
	G_Element root;
	
	G_List move_icons = new G_List(up_button, down_button, delete).verticalify();
	
	int base_drag_width = 0;
	int drag_width = 0;
	int min_element_height = 40;
		
	{
		addSubElement(name);
		addSubElement(move_icons);
		up_button.icon_size = 0.75;
		down_button.icon_size = 0.75;
		delete.icon_size = 0.75;
		move_icons.valign(Alignment.MIDDLE);
		name.halign(Alignment.MIDDLE);
		base_drag_width = (int) (up_button.width()*2.75);
		drag_width = G_HomeScreen.editing() ? base_drag_width : 0;
	}
	
	public G_DraggableNamedGroup(String name, G_Element element) {
		if (name.equals("Default")) move_icons.remove(delete);
		group_name = name;
		this.name.text(name);
		root(element);
		
		if (name.startsWith("§")) {
			switch (name.charAt(1)) {
				case 'e': this.name.base_color = GraphicsAPI.YELLOW; break;
				case '6': this.name.base_color = GraphicsAPI.GOLD; break;
				case '[': this.name.base_color = Settings.FAVORITES_COLOR(); break;
			}
			if (name.length() > 2) {
				this.name.text(name.substring(2));
			}
		}
	}
	
	public void root(G_Element element) {
		if (this.root != null) removeSubElement(root);
		root = element;
		addSubElement(element);
	}
	
	public G_Element root() { return root; }


	@Override
	public void recalculate_size() {
		name.recalculate_size();
		move_icons.recalculate_size();
		root.recalculate_size();
				
		if (G_HomeScreen.editing()) {
			min_element_height = move_icons.height();
		} else {
			min_element_height = 0;
		}
		
		if (!G_HomeScreen.editing_transition_complete()) {
			min_element_height = (int) Utility.lerp(0, move_icons.height(), G_HomeScreen.current_edit_anim_time());
		}
		
		int element_height = ( min_element_height > root.height()) ? min_element_height : root.height();
		this.unpadded_height = name.height() + element_height;
		
		this.unpadded_width = drag_width + name.width();
	}
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		
		if (!G_HomeScreen.editing_transition_complete()) {
			up_button.base_color = new Vector4f(1, 1, 1, G_HomeScreen.current_edit_anim_time());
			down_button.base_color = new Vector4f(1, 1, 1, G_HomeScreen.current_edit_anim_time());
			delete.base_color = new Vector4f(1, 1, 1, G_HomeScreen.current_edit_anim_time());
			drag_width = (int) Utility.lerp(0, base_drag_width, G_HomeScreen.current_edit_anim_time());
		}

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		//this.hover_rectangle = new Rectangle(left, top, right, bottom);
		this.hover_rectangle = new Rectangle(0,0,0,0);
		
		name.layout(left+drag_width, top, right, top+name.height());
		
		if (G_HomeScreen.editing()) {
			move_icons.layout(left, top+name.height(), left+drag_width, bottom);
		} else {
			move_icons.layout(0,0,0,0);
		}
		
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
