package nowplaying.gui;

import frost3d.utility.Rectangle;
import frost3d.utility.Utility;
import nowplaying.gui.abstracts.Screen;
import snowui.GUIInstance;
import snowui.elements.abstracts.GUIElement;
import snowui.utility.AnimationTimer;

public class GUIScreenContainer extends GUIElement {

	@Override public void recalculateSize(GUIInstance gui) { }
	@Override public void draw(GUIInstance gui, int depth) { }
	
	public GUIScreenContainer(Screen screen) {
		this.current = screen;
		this.registerSubElement(current);
	}

	Screen previous;
	Screen current;
	
	AnimationTimer timer = new AnimationTimer();
	
	{
		timer.setLengthMS(300);
	}
	
	@Override
	public void updateDrawInfo(GUIInstance gui) {
		Rectangle b = this.limit_rectangle();
		
		if (previous != null && timer.get() < 1) {
			int offset = (int) (Utility.lerp(timer.get(), 1, timer.get()) * b.width());
			previous.limit_rectangle(b.offset(offset,0));
			current.limit_rectangle(b.offset(offset-b.width(), 0));
		} else {
			current.limit_rectangle(b);
		}
		
		this.hover_rectangle(b);
			
	}
	
	@Override
	public void tickAnimation(GUIInstance gui) {
		if (previous != null && (timer.get() < 1 || timer.just_finished())) {
			this.should_update(true);
		} else if (this.previous != null) {
			this.removeSubElement(previous);
		}
	}
	
	public void current(Screen c) {
		this.previous = this.current;
		this.current = c;
		this.timer.start();
		this.registerSubElement(c);
	}

}
