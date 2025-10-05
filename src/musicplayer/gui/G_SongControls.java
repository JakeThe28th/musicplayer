package musicplayer.gui;

import org.lwjgl.glfw.GLFW;

import musicplayer.MainProgram;
import musicplayer.components.settings.Settings;
import musicplayer.components.settings.types.SongControlsLayoutSetting;
import musicplayer.components.settings.types.SongControlsLayoutSetting.SongControlsIcon;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;
import musicplayer.utility.Utility;

public class G_SongControls extends G_Element {
	
	boolean should_unpause_after_seek = true;
	
	// The song controls that remain at the bottom of the screen
	
	G_Text 		title			= new G_Text().text("No Song");
	G_Slider 	progress_bar	= new G_Slider() 
			{ 
			  @Override public void onDrag(double new_value) {
				if (current_song != null) {
					if (GraphicsAPI.left_click_pressed()) {
						should_unpause_after_seek = MusicPlayer.playing();
					}
					MusicPlayer.pause(false);
					MusicPlayer.seek((long) (MusicPlayer.songLength() * new_value));
					
					if (GraphicsAPI.left_click_released()) {
						if (should_unpause_after_seek) MusicPlayer.play();
					}
				}
			  } 
			  
			  @Override protected String amountFormatted() {
					return MusicPlayer.getTimecodeString();
			  }
			  
			};
		G_Icon 		previous 		= new G_Icon("previous")
			{ @Override public void onClick() { MusicPlayer.previous(); }};
		G_Icon 		stop 			= new G_Icon("stop")
			{ @Override public void onClick() { MusicPlayer.stop(); play_pause.icon_name = "play"; }};
		G_Icon 		play_pause 		= new G_Icon("play") 
			{ @Override public void onClick() { 
				if (MusicPlayer.playing()) {
					MusicPlayer.pause(); 
				} else {
					MusicPlayer.play();
				}
				}};
		G_Icon 		next 			= new G_Icon("next")
			{ @Override public void onClick() { MusicPlayer.next(); }};
		G_Icon 		volume 			= new G_Icon("volume")
		{ @Override public void onClick() { 
			show_volume_slider = !show_volume_slider;
			volume_slider_transition_timer = System.currentTimeMillis();
			}};
 public G_Icon 		shuffle 		= new G_Icon("shuffle") { 
	 @Override public void onClick() { MusicPlayer.cyclePlaybackMode(); }
	 @Override public void onHover() {
		 
		 GraphicsAPI.font_size((int) (Settings.font_size()*0.75f));
		 
		 String text = switch (MusicPlayer.playback_mode) {
			case MusicPlayer.LOOP_NONE -> "Play Once";
			case MusicPlayer.LOOP_SONG -> "Loop Song";
			case MusicPlayer.LOOP_LIST -> "Loop Playlist";
			case MusicPlayer.SHUFFLE   -> "Shuffle";
			default -> "Unknown playback mode.";
		};
		 
		 int padding = 5;
		 int height_padding = 5;
		 int x_middle = hover_rectangle.left() + ((hover_rectangle.right() - hover_rectangle.left()) / 2);
		 int text_height = GraphicsAPI.size(text).y;
		 int text_width = GraphicsAPI.size(text).x;
		 Rectangle rect = new Rectangle(
				 x_middle, 
				 hover_rectangle.top() - ((text_height + (padding*2)) + height_padding), 
				 x_middle, 
				 hover_rectangle.top() - (height_padding));
		 rect = rect.expand_horizontally(((text_width+(padding*2))/2));
		 GraphicsAPI.color(GraphicsAPI.BLACK75);
		 GraphicsAPI.rect(rect, 100);
		 GraphicsAPI.color(GraphicsAPI.WHITE);
		 GraphicsAPI.text(rect.left()+padding, rect.top()+padding, 120, text);
		 GraphicsAPI.font_size(Settings.font_size());
	 }
 };
			
	G_Slider 		volume_slider	= new G_Slider() 
		{ @Override public void onDrag(double new_value) {
			MusicPlayer.volume((float) new_value);
			Settings.volume((float) new_value);
		} };
		
		G_Icon 		pin    = new G_Icon("pin") { 
			boolean pinned = Settings.use_native_window_decorations();
			public void layout(int left, int top, int right, int bottom) {
				setPinned(!Settings.use_native_window_decorations());
				super.layout(left, top, right, bottom);
			}

			@Override public void onClick() { 
				setPinned(!pinned);
			}
			
			public void setPinned(boolean p) {
				if (p != pinned) {
					pinned = p;
					if (pinned) {
						icon_name = "x";
						Settings.use_native_window_decorations(false);
					} else {
						icon_name = "pin";
						Settings.use_native_window_decorations(true);
					}
				}
			}
		};

	// Overridden by makeIconsLayout(), but this is the old default if it's ever needed
	G_List		center_icons	= new G_List(previous, stop, play_pause, next);
	G_List		left_icons		= new G_List(volume);
	G_List		right_icons		= new G_List(pin, shuffle);
	
	public void makeIconsLayout(SongControlsLayoutSetting layout) {
		
		left_icons.clear();
		for (SongControlsIcon icon : layout.left_icons) {
			if (icon.visible) left_icons.add(makeIcon(icon));
		}
		
		center_icons.clear();
		for (SongControlsIcon icon : layout.middle_icons) {
			if (icon.visible) center_icons.add(makeIcon(icon));
		}
		
		right_icons.clear();
		for (SongControlsIcon icon : layout.right_icons) {
			if (icon.visible) right_icons.add(makeIcon(icon));
		}
	}	
	
	private G_Element makeIcon(SongControlsIcon icon) {
		switch (icon) {
			case NEXT: return next;
			case PAUSE: return play_pause;
			case PIN: return pin;
			case PREVIOUS: return previous;
			case SHUFFLE: return shuffle;
			case STOP: return stop;
			case VOLUME: return volume;
		}
		Log.send("Error: " + icon.name() + " is an unknown icon constant" + G_SongControls.class);
		return null;
	}

	{
		makeIconsLayout(Settings.song_controls_layout());
		
		base_color = Settings.LIGHT_COLOR();

		title.halign(Alignment.MIDDLE);
		right_margin = 30;
		left_margin = 30;
		top_margin = 10;

		center_icons.halign(Alignment.MIDDLE);
		
		title.force_scroll = Settings.always_scroll_current_song_title_in_song_controls();
		
		addSubElement(title);
		addSubElement(progress_bar);
		addSubElement(center_icons);
		addSubElement(left_icons);
		addSubElement(right_icons);
		addSubElement(volume_slider);
		
		volume_slider.amount = MusicPlayer.volume();

	}
	
	@Override
	public void recalculate_size() {
		title.recalculate_size();
		progress_bar.recalculate_size();
		center_icons.recalculate_size();
		left_icons.recalculate_size();
		right_icons.recalculate_size();
		volume_slider.recalculate_size();
		this.unpadded_height = title.height() + progress_bar.height() + center_icons.height();
		this.unpadded_height += volume_slider_height();
		this.unpadded_width = left_icons.width() + center_icons.width() + right_icons.width();
		
		GraphicsAPI.setMinimumWindowSize(this.width(), this.height(), GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
	}
	
	boolean show_volume_slider = false;
	long volume_slider_transition_timer = 0;
	long volume_slider_transition_time = 500;

	private int transition(long start_time, long length, boolean invert, int expanded_size, boolean smooth) {
		long time = System.currentTimeMillis() - start_time;
		float t = time / (float) length;
		if (t < 0) t = 0;
		if (t > 1) t = 1;
		if (smooth) t = (float) Utility.lerp(Utility.lerp(t, 1, t), 1, t);
		if (!invert) t = 1-t;
		int result = (int) (expanded_size * t);
		return result;
	}
	
	private int volume_slider_height() {
		if (!volume_slider_visible()) return 0;
		return transition(
				volume_slider_transition_timer, 
				volume_slider_transition_time,
				show_volume_slider,
				volume_slider.height(),
				true);
	}
	
	private int volume_slider_right(int left, int right) {
		if (!volume_slider_visible()) return 0;
		return left + transition(
				volume_slider_transition_timer, 
				volume_slider_transition_time,
				show_volume_slider,
				right-left,
				true);
	}
	
	private int volume_slider_dot_size() {
		long time = System.currentTimeMillis() - volume_slider_transition_timer;
		if (time > volume_slider_transition_time) return 7;
		return transition(
				volume_slider_transition_timer, 
				volume_slider_transition_time,
				show_volume_slider,
				7,
				true);
	}

	private boolean volume_slider_visible() {
		long time = System.currentTimeMillis() - volume_slider_transition_timer;
		if (time < volume_slider_transition_time) {
			return true;
		} else {
			return show_volume_slider;
		}
	}
	
	Rectangle draw_area;
	
	@Override
	public void layout(int left, int top, int right, int bottom) {
		draw_area = new Rectangle(left, top, right, bottom);

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		
		int yy = top;
		
			  title.layout(left, yy, right, yy + title.height());
		yy += title.height();
		
			  progress_bar.layout(left, yy, right, yy + progress_bar.height());
		yy += progress_bar.height();
		
			  volume_slider.layout(left, yy, volume_slider_right(left, right), yy+volume_slider_height());
			  if (!show_volume_slider) volume_slider.hover_rectangle = new Rectangle(-1,-1,-1,-1);
			  if (volume_slider_visible()) volume_slider.dot_size = volume_slider_dot_size();
		yy += volume_slider_height();
		
		int left_size = left_icons.width();
		int right_size = right_icons.width();
		
		if (Settings.force_song_conrols_buttons_centered()) {
			if (left_size > right_size) right_size = left_size;
			if (left_size < right_size) left_size = right_size;
		}
				
		left_icons		.layout(left, 					yy, left +left_size, 	bottom);
		center_icons	.layout(left +left_size, 		yy, right-right_size, 	bottom);
		right_icons		.layout(right-right_size, 		yy, right, 				bottom);	
	}
	
	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(draw_area, depth);
		
		if (current_song != null) {
			progress_bar.amount = MusicPlayer.songTime() / (float) MusicPlayer.songLength();
		}		
		
		title.draw(depth + 1);
		progress_bar.draw(depth + 1);
		center_icons.draw(depth+1);
		left_icons.draw(depth+1);
		right_icons.draw(depth+1);
		if (volume_slider_visible()) volume_slider.draw(depth + 1);
	}
	
	// -- ... -- //
	
	UUID current_song = null;

	public void current(UUID song) {
		title.text = Library.getSongFromAlbum(song).name();
		current_song = song;
		recalculate_size();
	}

	public void setPlaying(boolean b) {
		play_pause.icon_name = b ? "pause" : "play";
	}

	public void setForceScroll(boolean value) {
		title.force_scroll = value;
	}
	
}
