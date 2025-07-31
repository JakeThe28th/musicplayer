package musicplayer.gui;

/**
 * Kind of a reversion, but I 
 * don't think this will need 
 * anything other than hard
 * -coded styling, and the
 * GUI library I'm making would 
 * just be... kind of overkill.
 */
public interface G_Element {
	
	public void draw(int left, int top, int right, int bottom, int depth);
	
}
