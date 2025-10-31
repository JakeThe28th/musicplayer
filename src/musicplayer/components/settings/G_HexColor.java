package musicplayer.components.settings;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.components.settings.types.ColorSetting;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.IconType;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_Text;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Rectangle;

public class G_HexColor extends G_Element {
	
	public G_HexColor(Vector4f color) {
		this.color = color;
	}

	G_Text 		color_text 	= new G_Text("");
	Vector4f 	color 		= new Vector4f();
	G_Icon 		copy		= new G_Icon(IconType.GENERIC_CLIPBOARD_COPY) {
		@Override
		public void onClick() {
			StringSelection selection = new StringSelection(ColorSetting.toHex(color));
        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clipboard.setContents(selection, selection);
		}
	};
	
	G_Icon 		paste		= new G_Icon(IconType.GENERIC_CLIPBOARD_PASTE) {
		@Override
		public void onClick() {
	        try {
		        String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		        if (data.length() == 7 && data.startsWith("#") && data.substring(1).matches("-?[0-9a-fA-F]+")) {
		        	internal_color(ColorSetting.readHex(data));
		        } else if (data.length() == 6 && data.matches("-?[0-9a-fA-F]+")) {
		        	internal_color(ColorSetting.readHex("#" + data));
		        } else {
		        	MainProgram.showError("Clipboard is not a hex color");
		        }
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static final int SLIDER_HEIGHT = 30;
	
	{ color_text.halign(Alignment.MIDDLE);; addSubElement(color_text); addSubElement(copy); addSubElement(paste); }

	@Override
	public void recalculate_size() {
		for (G_Element e : sub_elements) {
			e.recalculate_size();
		}
		color_text.text = ColorSetting.toHex(color);
		this.unpadded_height = color_text.height();
		this.unpadded_width = color_text.width() + copy.width() + paste.width();
	}

	protected void internal_color(Vector4f color) {
		this.color = color;
		onChangeColor(color);
	}

	Rectangle color_rect;

	@Override
	public void layout(int left, int top, int right, int bottom) {
		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		this.hover_rectangle = new Rectangle(0,0,0,0);

		int pad_sides = copy.width() + paste.width();
		color_text.layout(left+pad_sides, top, right-pad_sides, bottom);
		copy.layout(right-pad_sides, top, right-paste.width(), bottom);
		paste.layout(right-paste.width(), top, right, bottom);

		color_rect = new Rectangle(left, top, right, bottom);
		
		if ( (color.x + color.y + color.z) / 3f < 0.5f ) {
			color_text.base_color = GraphicsAPI.WHITE;
		} else {
			color_text.base_color = GraphicsAPI.BLACK;
		}
		
	}

	@Override
	public void draw(int depth) {
		
		GraphicsAPI.color(color);
		GraphicsAPI.rect(color_rect, depth + 1);
		
		for (G_Element e : sub_elements) {
			e.draw(depth + 2);
		}
	}
	
	public void onChangeColor(Vector4f newcolor) { }
	
}