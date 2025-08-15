package musicplayer.ext;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class Text {
	
	/* It would probably be more efficient
	 * to only have one mesh and refer to
	 * each character with an index, and
	 * just change the texture coordinates 
	 * with uniforms instead of baking them
	 * in the mesh, but that'd overcomplicate 
	 * the shader, so the RAM cost will just 
	 * have to be eaten. */
	
	int font_size = 20;
	//Font font = new Font("SansSerif", Font.PLAIN, font_size);
	Font font = new Font("Consolas", Font.PLAIN, font_size);
	
	HashMap<Integer, TextureInfo> 	textures = new HashMap<Integer, TextureInfo>();
	
	/* Offset for drawing so it's drawn at the 'top left corner' of the character, 
	 * relative to the font size. Characters will probably have different heights 
	 * and stuff, the important part is that the location is consistent, so it 
	 * won't match the corner of some or most characters */
	public static final float CORNER_X_OFFSET = -(12f / 24f);
	public static final float CORNER_Y_OFFSET = -(16f / 24f);
	
	/* The height of a typical character in this font, relative to the font size.
	 * Again, consistency is important. Some characters like g or Q have parts
	 * that go below the usual baseline of a character, so this value is gonna
	 * be taller than most characters need. */
	public static final float CHARACTER_HEIGHT = 1.1f;
	
	/* The width of each character in this font, relative to the font size.
	 * Consolas is monospace, so the width is the same for every character. 
	 * Kerning can be dealt with later...  */
	public static final float CHARACTER_WIDTH = 0.60f;

	public Vector2i size(String text) {
		int xx = 0;
		for (int i = 0; i < text.length(); i++) {
			xx += font_size * CHARACTER_WIDTH;
		}
		return new Vector2i(xx, (int) (font_size * CHARACTER_HEIGHT));
	}	
	
	Vector4f color = new Vector4f(1,1,1,1);
	public void color(Vector4f new_color) { color = new_color; }
	
	/** Draw one line of text, where 0, 0 is the 'top left' of the character*<br>
	 *  *Not exactly the top left, but close enough... */
	public void text(int x, int y, int z, String text) {
		int xx = x + (int) (CORNER_X_OFFSET * font_size);
			y  = y + (int) (CORNER_Y_OFFSET * font_size);
		for (int i = 0; i < text.length(); i++) {
			character(xx, y, z, text.charAt(i));
			xx += font_size * CHARACTER_WIDTH;
		}
	}
	
	/** Draw one character of text */
	protected void character(int x, int y, int z, char character) {
		RenderQueue.queue(
				mesh(character), 
				new Matrix4f().translate(x, y, z), 
				color, 
				texture()
				);
	}
	
	private Mesh mesh(char character) {
		if (textures.get(font_size) == null) {
			textures.put(font_size, new TextureInfo(font_size));
		}
		TextureInfo info = textures.get(font_size);
		if (info.meshes.get(character) == null) {
			info.make(character);
		}
		return info.meshes.get(character);
	}

	private Texture texture() {
		if (textures.get(font_size) == null) {
			textures.put(font_size, new TextureInfo(font_size));
		}
		TextureInfo info = textures.get(font_size);
		return info.gltexture();
	}
		
	/** Stores texture and mesh information for a
	 *  texture sheet of characters for a specific
	 *  font size */
	class TextureInfo {
		
		public static final int CHARCOUNT = 16; // character amount = 16 * 16 = 256
		
		boolean 		changed 	= true;
		BufferedImage 	texture 	= null;
		Texture		 	gltexture 	= null;
		int 			font_size;
		
		public Texture gltexture() {
			if (changed) {
				if (gltexture != null) gltexture.free();
				gltexture = new Texture(texture);
				changed = false;
			}
			return gltexture;
		}
		
		HashMap<Character, Mesh> meshes = new HashMap<Character, Mesh>(); 
		int character_index = 0;
		
		public TextureInfo(int font_size) {
			texture = new BufferedImage(CHARCOUNT*(font_size*2), CHARCOUNT*(font_size*2), BufferedImage.TYPE_INT_ARGB);
			this.font_size = font_size;
		}

		public void make(char character) {
			changed = true;
			int char_x = character_index % CHARCOUNT;
			int char_y = character_index / CHARCOUNT;
			
			// Each character's area is (font_size*2) * (font_size*2)
			// The characters are drawn at 25% from the bottom and 25% from the left
			int offset = font_size/2;
			int unit_size = font_size*2;
			
			int unit_x = (unit_size*char_x);
			int unit_y = (unit_size*char_y);
			
			int real_x = unit_x + offset;
			int real_y = unit_y + (unit_size-offset);
			
			Font f = font.deriveFont(font_size);
			
			Graphics2D g = texture.createGraphics();
				g.setColor(Color.white);
		    	g.setFont(f);
		    	g.drawString(character+"", real_x, real_y);
		    	
		    int w = texture.getWidth();
		    int h = texture.getHeight();
		    
	    	Mesh mesh = new Mesh(new float[] {
	    			unit_size,  unit_size, 	0.0f,  // top right
	    			unit_size,  0, 			0.0f,  // bottom right
				    0, 			0, 			0.0f,  // bottom left
				    0,  		unit_size, 	0.0f   // top left 
					},
					new float[] {
					((float) (unit_x + unit_size)) / w, (((float) (unit_y + unit_size)) / h),
					((float) (unit_x + unit_size)) / w, (((float) (unit_y            )) / h),
					((float) (unit_x            )) / w, (((float) (unit_y            )) / h),
					((float) (unit_x            )) / w, (((float) (unit_y + unit_size)) / h),
					},
					new int[] {
					    0, 1, 3,   // first triangles
					    1, 2, 3    // second triangle
					});
	    	
	    	meshes.put(character, mesh);
	    	
	    	character_index++;

		}
		
	}

}
