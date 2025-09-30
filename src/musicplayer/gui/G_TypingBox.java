package musicplayer.gui;

import org.joml.Vector4f;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.graphics.KeybindAPI;
import musicplayer.utility.Log;
import musicplayer.utility.Rectangle;

public class G_TypingBox extends G_Element {
	
	boolean typing = true;
	
	protected String rawtext = "";
	protected G_Text text = new G_Text();
	
	Vector4f selected_color = new Vector4f(0.8f,0.8f,1f,1);
	
	{
		text.text = "";
		text.base_color = GraphicsAPI.BLACK;
		addSubElement(text);
		hover_color = new Vector4f(0,0,0,0.2f);
		base_color = GraphicsAPI.WHITE;
	}

	@Override
	public void recalculate_size() {
		text.recalculate_size();
		this.unpadded_height = text.height();
		this.unpadded_width = text.width();
	}
	
	Rectangle background = new Rectangle(0,0,0,0);
	Rectangle typing_indicator = new Rectangle(0,0,0,0);

	@Override
	public void layout(int left, int top, int right, int bottom) {

		left += left_margin;
		right -= right_margin;
		top += top_margin;
		bottom -= bottom_margin;
		
		hover_rectangle = new Rectangle(left, top, right, bottom);
		
		background = new Rectangle(left, top, right, bottom);
		
		text.layout(left, top, right, bottom);
		
		int ww = text.width() + 4;
		if (System.currentTimeMillis() / 750 % 2 == 0 && typing) {
			typing_indicator = new Rectangle(ww,top+8,ww+2,bottom-8);
		} else {
			typing_indicator = new Rectangle(ww,0,ww+4,0);
		}
	}

	@Override
	public void draw(int depth) {
		GraphicsAPI.color(base_color);
		GraphicsAPI.rect(background, depth);
		text.draw(depth+2);
		
		GraphicsAPI.color(GraphicsAPI.BLACK);
		GraphicsAPI.rect(typing_indicator, depth+1);
	}
	
	@Override
	public boolean input() {
		base_color = GraphicsAPI.WHITE;
		if (typing) {
			base_color = selected_color;
			KeybindAPI.lock_keybinds = true;
			if (!rawtext.equals(GraphicsAPI.input_string())) {
				rawtext = GraphicsAPI.input_string();
				text.text = GraphicsAPI.input_string();
				onChangeText(rawtext);
			}
			if (KeybindAPI.shouldStopTyping()) {
				typing = false;
			}
		}
		return super.input();
	}
	
	@Override
	public void onClick() {
		GraphicsAPI.input_string(rawtext);
		typing = true;
	}
	
	public void onChangeText(String new_text) {
		
	}

}
