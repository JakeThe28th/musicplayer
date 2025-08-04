package musicplayer.ext;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import org.joml.Matrix4f;
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
	
	class TextureInfo {
		
		public static final int CHARCOUNT = 16; // character amount = 16 * 16 = 256
		
		boolean 		changed 	= true;
		BufferedImage 	texture 	= null;
		Texture		 	gltexture 	= null;
		int 			font_size;
		
		public Texture gltexture() {
			if (changed) {
			  try {
				gltexture = new Texture(texture);
				changed = false;
			  } catch (IOException e) { e.printStackTrace(); }
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
			
			Graphics2D g = texture.createGraphics();
				g.setColor(Color.white);
		    	g.setFont(font);
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
	
	int font_size = 24;
	//Font font = new Font("SansSerif", Font.PLAIN, font_size);
	Font font = new Font("Consolas", Font.PLAIN, font_size);
	
	HashMap<Integer, TextureInfo> 	textures = new HashMap<Integer, TextureInfo>();
	
	public Mesh mesh(char character) {
		
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
	
	public void text(int x, int y, int z, String text) {
		int xx = x;
		for (int i = 0; i < text.length(); i++) {
			character(xx, y, z, text.charAt(i));
			xx += font_size * 0.70;
		}
	}
	
	public void character(int x, int y, int z, char character) {
		Graphics.queue(
				mesh(character), 
				new Matrix4f().translate(x, y, z), 
				new Vector4f(1,1,1,1), 
				texture()
				);

	}
	
}
