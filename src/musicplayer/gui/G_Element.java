package musicplayer.gui;

import java.util.ArrayList;

import org.joml.Vector4f;

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

	public int width() 		{ return left_margin + unpadded_width + right_margin; }
	public int height()  	{ return top_margin + unpadded_height + bottom_margin; };

	protected int 			unpadded_width 		= 0;
	protected int 			unpadded_height 	= 0;
	
	protected int 			left_margin 		= 5;
	protected int 			right_margin 		= 5;
	protected int 			top_margin 			= 5;
	protected int 			bottom_margin 		= 5;
	
	protected Vector4f   	base_color			= new Vector4f(1,1,1,1);
	
	public void halign(Alignment align) 		{ this.horizontal_align = align; }
	
	protected Alignment   	horizontal_align	= Alignment.LEFT;
	
	// Run before layout(), in case elements changed size
	public abstract void recalculate_size();

	// Run before drawing, calculates the placement of elements.
	public abstract void layout(int left, int top, int right, int bottom);
		
	// Actually draws the element
	public abstract void draw(int depth);
	
	// Returns all sub-elements of this element
	public abstract ArrayList<G_Element> sub_elements();
	
	// Interaction //
	Rectangle hover_rectangle = new Rectangle(0,0,0,0);
	
	public boolean input() {
		for (G_Element e : sub_elements()) {
			if (e.input()) return true;
		}
		
		if (hover_rectangle.contains(GraphicsAPI.mouseX(), GraphicsAPI.mouseY())) {
			GraphicsAPI.color(GraphicsAPI.TRANSPARENT_WHITE);
			if (GraphicsAPI.left_click_down()) { GraphicsAPI.color(GraphicsAPI.TRANSLUCENT_WHITE); }
			if (GraphicsAPI.left_click_released()) { onClick(); return true; }

			GraphicsAPI.rect(hover_rectangle, 0);
		}
		
		return false;
	}

	public void onClick() {
		
	}
	
}
