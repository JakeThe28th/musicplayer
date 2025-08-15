package musicplayer.ext;

import java.awt.image.BufferedImage;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Shapes {
	
	static Texture white = newWhiteTexture();

	private static Texture newWhiteTexture() {
		BufferedImage white = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		white.setRGB(0, 0, Integer.MAX_VALUE);
		return new Texture(white);
	}
	
	static Mesh unit_square = new Mesh(
		new float[] {
		1,  1, 0.0f,  // top right
		1,  0, 0.0f,  // bottom right
	    0,  0, 0.0f,  // bottom left
	    0,  1, 0.0f   // top left 
		},
		new float[] {
		1,1,
		1,0,
		0,0,
		0,1},
		new int[] {
		    0, 1, 3,   // first triangles
		    1, 2, 3    // second triangle
		});
	
	// -- Drawing stuff -- //
	
	static Vector4f color = new Vector4f(1,1,1,1);
	
	public static void rect(int left, int top, int right, int bottom, int depth) {
		Matrix4f transform = new Matrix4f().translate(left, top, depth).scale(right-left, bottom-top, 1);
		RenderQueue.queue(unit_square, transform, color, white);
	}

}
