package musicplayer.gui;

import java.util.ArrayList;

import org.joml.Vector4f;

import musicplayer.MainProgram;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.enums.Alignment;
import musicplayer.utility.Rectangle;

/**
 * Kind of a reversion, but I 
 * don't think this will need 
 * anything other than hard
 * -coded styling, and the
 * GUI library I'm making would 
 * just be... kind of overkill.
 */
public abstract class G_Element {
			
	public static final ArrayList<G_Element> EMPTY = new ArrayList<G_Element>();
	
	protected ArrayList<G_Element> sub_elements = new ArrayList<G_Element>();
	
	public void addSubElement(G_Element element) {
		if (element == null) new Error("wat").printStackTrace();
		sub_elements.add(element);
	}
	
	public void removeSubElement(G_Element element) {
		sub_elements.remove(element);
	}

	public int width() 		{ return left_margin + unpadded_width + right_margin; }
	public int height()  	{ return top_margin + unpadded_height + bottom_margin; };

	protected int 			unpadded_width 		= 0;
	protected int 			unpadded_height 	= 0;
	
	public int 			left_margin 		= 5;
	public int 			right_margin 		= 5;
	public int 			top_margin 			= 5;
	public int 			bottom_margin 		= 5;
	
	public Vector4f   	base_color			= new Vector4f(1,1,1,1);
	public Vector4f		hover_color			= GraphicsAPI.TRANSPARENT_WHITE;
	
	public void halign(Alignment align) 		{ this.horizontal_align = align; }
	public void valign(Alignment align) 		{ this.vertical_align = align; }

	protected Alignment   	horizontal_align	= Alignment.LEFT;
	protected Alignment   	vertical_align		= Alignment.LEFT;

	// Run before layout(), in case elements changed size
	public abstract void recalculate_size();

	// Run before drawing, calculates the placement of elements.
	public abstract void layout(int left, int top, int right, int bottom);
		
	// Actually draws the element
	public abstract void draw(int depth);
	
	// Interaction //
	protected Rectangle hover_rectangle = new Rectangle(0,0,0,0);
	
	/** is_hovered_with_held_left_click */
	private boolean can_click = false;
	
	public boolean input() {
				
		if (MainProgram.popups.size() > 0) return false;
		
		for (G_Element e : sub_elements) {
			if (e.input()) return true;
		}
		
		if (!GraphicsAPI.windowIsHovered()) return false;
			
		if (hover_rectangle.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
			if (can_click || (!GraphicsAPI.left_click_down() && !GraphicsAPI.left_click_released())) {
				GraphicsAPI.color(hover_color);
				if (GraphicsAPI.left_click_down()) { GraphicsAPI.color(GraphicsAPI.TRANSLUCENT_WHITE); }
				if (GraphicsAPI.left_click_released()) { onClick(); return true; }
				GraphicsAPI.rect(hover_rectangle, 0);
				onHover();
				return true;
			}
			if (GraphicsAPI.left_click_pressed()) { onLeftMousePress(); can_click = true; return true; }
		} else {
			can_click = false;
		}
		
		if (GraphicsAPI.left_click_released()) { can_click = false; }
		
		return false;
	}
	
	public void remove_input() {
		for (G_Element e : sub_elements) { e.remove_input(); }
		hover_rectangle = new Rectangle(0,0,0,0);
	}
	
	public void allmargins(int i) {
		left_margin = i;
		right_margin = i;
		bottom_margin = i;
		top_margin = i;
	}

	public void onClick() { }
	public void onLeftMousePress() { }
	
	public void onHover() { }
}
