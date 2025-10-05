package musicplayer.components.settings.types;

import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import org.joml.Vector4f;

import musicplayer.utility.Log;

public class SongControlsLayoutSetting implements Setting {
	
	public static enum SongControlsIcon {
		VOLUME("volume", true),
		PREVIOUS("previous", true),
		STOP("stop", true),
		PAUSE("pause", true),
		NEXT("next", true),
		PIN("pin", true),
		SHUFFLE("shuffle", true),;
		public String icon;
		public boolean visible;
		SongControlsIcon(String icon, boolean visible) {
			this.icon = icon;
			this.visible = visible;
		}
		
	}
	
	public LinkedHashSet<SongControlsIcon> left_icons;
	public LinkedHashSet<SongControlsIcon> middle_icons;
	public LinkedHashSet<SongControlsIcon> right_icons;

	public SongControlsLayoutSetting() {
		this.left_icons = new LinkedHashSet<SongControlsIcon>();
			left_icons.add(SongControlsIcon.VOLUME);
			
		this.middle_icons = new LinkedHashSet<SongControlsIcon>();
			middle_icons.add(SongControlsIcon.PREVIOUS);
			middle_icons.add(SongControlsIcon.STOP);
			middle_icons.add(SongControlsIcon.PAUSE);
			middle_icons.add(SongControlsIcon.NEXT);

		this.right_icons = new LinkedHashSet<SongControlsIcon>();
			right_icons.add(SongControlsIcon.PIN);
			right_icons.add(SongControlsIcon.SHUFFLE);

	}
	
	public SongControlsLayoutSetting(
			LinkedHashSet<SongControlsIcon> l, 
			LinkedHashSet<SongControlsIcon> m, 
			LinkedHashSet<SongControlsIcon> r) {
		this.left_icons = l;
		this.middle_icons = m;
		this.right_icons = r;
	}

	public SongControlsLayoutSetting(String serialized) {
		deserialize(serialized);
	}
	
	@Override
	public Setting deserialize(String serialized) {
		
		this.left_icons = new LinkedHashSet<SongControlsIcon>();
		this.middle_icons = new LinkedHashSet<SongControlsIcon>();
		this.right_icons = new LinkedHashSet<SongControlsIcon>();

		String[] areas = serialized.split(Pattern.quote(";"));
		for (int i = 0; i < 3; i++) {
			LinkedHashSet<SongControlsIcon> set = null;
			if (i == 0) set = left_icons;
			if (i == 1) set = middle_icons;
			if (i == 2) set = right_icons;
			String[] icons = areas[i].split(Pattern.quote("&"));
			for (String icondata : icons) {
				String[] icon = icondata.split(Pattern.quote("|"));
				SongControlsIcon icon_obj = SongControlsIcon.valueOf(icon[0]);
				icon_obj.icon = icon[1];
				icon_obj.visible = icon[2].equals("true");
				set.add(icon_obj);
			}
		}

		return this;
	}

	@Override
	public String serialize() {
		return serialize(left_icons) + serialize(middle_icons) + serialize(right_icons);
	}
	
	private String serialize(LinkedHashSet<SongControlsIcon> icons) {
		String returned = "";
		for (SongControlsIcon i : icons) {
			returned += i.name() + "|" + i.icon + "|" + i.visible + "&";
		}
		return returned + ";";
	}

	@Override public String type() { return "song_controls_layout"; }

}
