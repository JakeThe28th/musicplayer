package musicplayer.gui;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.components.settings.Settings;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.IconType;
import musicplayer.graphics.Texture;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_PlaylistGridItem extends G_Element {
	
	Texture cover;
	Playlist playlist;
	
	protected Vector4f hover_item_background_color = GraphicsAPI.TRANSLUCENT_BLACK;
	

	private boolean is_favorited() {
		if (playlist == null) return false;
		return G_HomeScreen.FAVORITES_GROUP.equals(playlist.metadata("group"));
	}
	
	G_Icon favorite = new G_Icon(Settings.use_heart_as_favorite_icon() ? IconType.FAVORITE_HEART : IconType.FAVORITE_STAR) {
		@Override public void onClick() {
			if (!is_favorited()) {
				playlist.metadata("group", G_HomeScreen.FAVORITES_GROUP);
			} else {
				playlist.metadata("group", null);
			}
			G_HomeScreen.update_playlist_views();
			updateFavoriteIcon();

		}
		@Override public void draw(int depth) {
			if (hovering) {
				super.draw(depth+1);
			}
		}
	};
	
	G_List icons = new G_List(favorite).verticalify();
	
	G_Text name = new G_Text();;
	
	float hover_opacity = 0;
	
	{
		favorite.icon_size = 0.75f;
		favorite.base_color = Settings.FAVORITES_COLOR();
		
		favorite.base_color = new Vector4f(
				favorite.base_color.x, 
				favorite.base_color.y, 
				favorite.base_color.z,
				hover_opacity);
		
		sub_elements.add(name);
		addSubElement(icons);
	}
	
	public G_PlaylistGridItem(Playlist playlist) {
		this.playlist = playlist;
		name.text(playlist.name());
		
		updateFavoriteIcon();
	}


	private void updateFavoriteIcon() {
		if (is_favorited()) {
			favorite.icon_name = Settings.use_heart_as_favorite_icon() ? IconType.FAVORITE_HEART : IconType.FAVORITE_STAR;
		} else {
			favorite.icon_name = Settings.use_heart_as_favorite_icon() ? IconType.FAVORITE_HEART_OUTLINE : IconType.FAVORITE_STAR_OUTLINE;
		}
	}


	@Override
	public void recalculate_size() {
		// TODO Auto-generated method stub
//		name.recalculate_size();
//		icons.recalculate_size();
		icons.allmargins(0);
	}

	Rectangle area;
	Rectangle name_area;
	private boolean hovering = false;
	Rectangle icons_rectangle;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		area = new Rectangle(left+left_margin, top+top_margin, right-right_margin, bottom-bottom_margin);
		
		int text_top = (bottom-bottom_margin)-name.height();
		name_area = new Rectangle(left+left_margin, text_top, right-right_margin, bottom-bottom_margin);
		name.layout(name_area.left(), name_area.top(), name_area.right(), name_area.bottom());
		
		icons.layout(area.right()-icons.width(), area.top(), area.right(), text_top);
		icons_rectangle = new Rectangle(area.right()-icons.width(), area.top(), area.right(), area.top()+icons.height());

		Rectangle b = GraphicsAPI.scissor();
		this.hover_rectangle = new Rectangle(
				left, 
				(top > b.top()) ? top : b.top(), 
				right,
				(bottom < b.bottom()) ? bottom : b.bottom());
		
		
	}

	@Override
	public void draw(int depth) {
		cover = playlist.glcover();
		GraphicsAPI.color(GraphicsAPI.WHITE);
		GraphicsAPI.rect(area, depth, cover);
		
		GraphicsAPI.color(GraphicsAPI.TRANSLUCENT_BLACK);
		GraphicsAPI.rect(name_area, depth + 1);
		
		if (hovering) {
			GraphicsAPI.color(hover_item_background_color);
			GraphicsAPI.rect(icons_rectangle, depth);
		}
		
		name.draw(depth+2);
		icons.draw(depth+3);
	}
	
	@Override
	public void onClick() {
		MusicPlayer.set_current_view_playlist(playlist.identifier());
		MainProgram.change_screen(G_PlaylistScreen.IDENTIFIER);
		MusicPlayer.scroll_to_current();
	}


	@Override
	public void foo() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void tickAnimation() {
		hovering  = false;
		
		if (hover_rectangle.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
			hovering = true;
			if (hover_opacity < 1) {
				hover_opacity += 4/60f;
				if (hover_opacity > 1) hover_opacity = 1;
				favorite.base_color = new Vector4f(
						favorite.base_color.x, 
						favorite.base_color.y, 
						favorite.base_color.z,
						hover_opacity);
				this.hover_item_background_color = new Vector4f(0,0,0,hover_opacity/2);
			}
		} else {
			if (hover_opacity > 0) {
				hovering = true;
				hover_opacity -= 4/60f;
				if (hover_opacity < 0) hover_opacity = 0;
				favorite.base_color = new Vector4f(
						favorite.base_color.x, 
						favorite.base_color.y, 
						favorite.base_color.z,
						hover_opacity);
				this.hover_item_background_color = new Vector4f(0,0,0,hover_opacity/2);
			}
		}
		
	}

}
