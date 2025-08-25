package musicplayer.gui.extra;

import java.util.ArrayList;

import org.joml.Vector2i;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.GenericInterface;
import musicplayer.utility.GenericSingleObjectInterface;
import musicplayer.utility.Rectangle;

public class Popup {
	
	public record Option(String name, GenericSingleObjectInterface run, Object optional) {
		
		public Option(String name, GenericInterface run) {
			this(name, (o) -> { run.run(); }, null);
		}

		public static Option[] from(ArrayList<Option> optionarray) {
			Option[] options = new Option[optionarray.size()];
			for (int i = 0; i <  optionarray.size(); i++) {
				options[i] = optionarray.get(i);
			}
			return options;
	}};
	
	ArrayList<Option> options = new ArrayList<Option>();
	
	int x;
	int y;
	Alignment horizontal_alignment = Alignment.RIGHT;
	Alignment vertical_alignment = Alignment.LEFT;
	
	public Popup(int xx, int yy, Alignment halign, Alignment valign, Option...options) {
		x = xx;
		y = yy;
		horizontal_alignment = halign;
		vertical_alignment = valign;
		for (Option o : options) {
			this.options.add(o);
		}
	}

	public boolean draw(int depth) {
				
		int width = 0;
		int height = 0;
		for (Option o : options ) {
			Vector2i size = GraphicsAPI.size(o.name);
			width = (size.x > width) ? size.x : width;
			height += size.y;
		}
		height += 10;
		width += 10;
		
		int xx = x;
		int yy = y;
		if (horizontal_alignment == Alignment.RIGHT) xx -= width;
		if (vertical_alignment == Alignment.RIGHT) yy -= height;

		GraphicsAPI.color(GraphicsAPI.BLACK75);
		Rectangle area = new Rectangle(xx, yy, xx+width, yy+height);
		GraphicsAPI.rect(area, depth);
		
		yy += 5;
		GraphicsAPI.color(GraphicsAPI.WHITE);
		for (Option o : options ) {
			Vector2i size = GraphicsAPI.size(o.name);
			GraphicsAPI.text(xx + 5, yy , depth+1, o.name);
			if (new Rectangle(xx, yy, xx+width, yy+size.y).isHovered()) {
				GraphicsAPI.color(GraphicsAPI.TRANSLUCENT_WHITE);
				GraphicsAPI.rect(xx, yy, xx+width, yy+size.y, depth);
				GraphicsAPI.color(GraphicsAPI.WHITE);
				if (GraphicsAPI.left_click_released()) {
					o.run().run(o.optional());
					MainProgram.popups.remove(this);
				}
			}
			yy+=size.y;
			
		}

		if (GraphicsAPI.left_click_pressed() && !area.isHovered()) {
			return true;
		} else {
			return false;
		}
	}


}
