package musicplayer.components.settings;

import java.util.LinkedHashSet;

import musicplayer.components.settings.types.SongControlsLayoutSetting;
import musicplayer.components.settings.types.SongControlsLayoutSetting.SongControlsIcon;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.GUIUtility;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_SongControlsLayout extends G_Element {
		
	class ControlsIcon extends G_Icon {
		
		class G_VisibilityIcon extends G_Icon {
			public G_VisibilityIcon(String name) { super(name); }
			boolean visible = false;
			@Override public void onClick() {
				setVisible(!visible);

				if (real_icon != null && setting != null) {
					real_icon.visible = visible;
					setLayout(setting);
					Settings.song_controls_layout(setting);
				}
				
			}
			private void setVisible(boolean vb) {
				visible = vb;
				if (!visible) {
					this.base_color = GraphicsAPI.TRANSLUCENT_WHITE;
				} else {
					this.base_color = GraphicsAPI.WHITE;
				}
			}
		}
		
		G_VisibilityIcon visibility = new G_VisibilityIcon("eye");
		
		{
			addSubElement(visibility);
			visibility.icon_size = 0.65f;
			visibility.halign(Alignment.MIDDLE);
			
			visibility.onClick();
		}
		
		@Override
		public void recalculate_size() {
			super.recalculate_size();
			visibility.recalculate_size();
			unpadded_height += visibility.height();
		}
		
		@Override
		public void layout(int left, int top, int right, int bottom) {
			super.layout(left, top, right, bottom-visibility.height());
			hover_rectangle = new Rectangle(
					hover_rectangle.left(),
					hover_rectangle.top(),
					hover_rectangle.right(),
					hover_rectangle.bottom()-visibility.height());
			visibility.layout(left, top+(height()-visibility.height()), right, bottom);
		}

		@Override
		public void draw(int depth) {
			super.draw(depth);
			if (dragging != this) visibility.draw(depth);
		}
		
		SongControlsIcon real_icon;
		G_List parent;		
		int index_in_parent;
		LinkedHashSet<SongControlsIcon> original_setting_list;
		public ControlsIcon(String name, G_List parent, SongControlsIcon real_icon, LinkedHashSet<SongControlsIcon> original_setting_list, int index) {
			super(name);
			this.parent = parent;
			this.index_in_parent = index;
			this.real_icon = real_icon;
			this.original_setting_list = original_setting_list;
			visibility.setVisible(real_icon.visible);
		}
		
		@Override public void onLeftMousePress() {
			parent.remove(this);
			dragging = this;
		}
		
		public void layout_dragging() {
			int xx = GraphicsAPI.mouseX()-(width()/2);
			int yy = GraphicsAPI.mouseY()-(height()/2);
			super.layout( xx,  yy, xx+(width()/2), yy+(height()/2));
		}
		
		G_List target_list = null;
		int target_index = -1;
		LinkedHashSet<SongControlsIcon> target_setting_list = null;
		public void setTarget(G_List list, int index, LinkedHashSet<SongControlsIcon> settings_list) {
			target_list = list;
			target_index = index;
			target_setting_list = settings_list;
		}

		public void trySetDown() {
			if (GraphicsAPI.left_click_released()) {
				if (target_list != null) {
					target_list.add(this, target_index);
					dragging = null;
					boolean first = target_index == 0;
					original_setting_list.remove(real_icon);
					if (first) {
						target_setting_list.addFirst(real_icon);
					} else {
						target_setting_list.add(real_icon);
					}
					setLayout(setting);
					Settings.song_controls_layout(setting);
				} else {
					parent.add(this, index_in_parent);
					dragging = null;
				}
			}
			target_list = null;
			target_setting_list = null;
			target_index = -1;
		}

	}
	
	ControlsIcon dragging = null;
	
	G_List left_icons = new G_List();
	G_List center_icons = new G_List();
	G_List right_icons = new G_List();
	
	SongControlsLayoutSetting setting;
	
	{
		addSubElement(left_icons);
		addSubElement(center_icons);
		addSubElement(right_icons);
		center_icons.halign(Alignment.MIDDLE);
	}
	
	public G_SongControlsLayout(SongControlsLayoutSetting setting) {
		setLayout(setting);
		this.setting = setting;
	}
	
	public void setLayout(SongControlsLayoutSetting layout) {
		left_icons.clear();
		center_icons.clear();
		right_icons.clear();
		
		left_icons.add(emptyIcon(left_icons, layout.left_icons));
		addIcons(left_icons, layout.left_icons);
		left_icons.add(emptyIcon(left_icons, layout.left_icons));

		center_icons.add(emptyIcon(center_icons, layout.middle_icons));
		addIcons(center_icons, layout.middle_icons);
		center_icons.add(emptyIcon(center_icons, layout.middle_icons));

		right_icons.add(emptyIcon(right_icons, layout.right_icons));
		addIcons(right_icons, layout.right_icons);
		right_icons.add(emptyIcon(right_icons, layout.right_icons));

	}

	private G_Element emptyIcon(G_List list, LinkedHashSet<SongControlsIcon> settings_list) {
		int index = list.length();
		return new G_Icon("hollow_square") {
			{
				base_color = GraphicsAPI.TRANSPARENT_WHITE;
			}
			@Override public boolean input() {
				if (dragging != null) {
					if (hover_rectangle.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
						GraphicsAPI.color(GraphicsAPI.TRANSLUCENT_WHITE);
						GraphicsAPI.rect(hover_rectangle, 300);
						dragging.setTarget(list, index, settings_list);
					}
				}
				return false;
			}
		};
	}

	private void addIcons(G_List icon_elements, LinkedHashSet<SongControlsIcon> icon_objects) {
		for (SongControlsIcon iobj : icon_objects) {
			icon_elements.add(new ControlsIcon(iobj.icon, icon_elements, iobj, icon_objects, icon_elements.length()));
		}
	}

	@Override
	public void recalculate_size() {
		left_icons.recalculate_size();
		center_icons.recalculate_size();
		right_icons.recalculate_size();
		this.unpadded_height = center_icons.height();
		this.unpadded_width = Settings.icon_size();
	}

	@Override
	public void layout(int left, int top, int right, int bottom) {
		left_icons		.layout(left, 							top, left +left_icons .width(), 	bottom);
		center_icons	.layout(left +left_icons.width(), 		top, right-right_icons.width(), 	bottom);
		right_icons		.layout(right-right_icons.width(), 		top, right, 						bottom);
		
		if (dragging != null) dragging.layout_dragging();
	}

	@Override
	public void draw(int depth) {
		if (dragging != null) dragging.trySetDown();
		left_icons.draw(depth);
		center_icons.draw(depth);
		right_icons.draw(depth);
		if (dragging != null) dragging.draw(depth+3);
	}

}
