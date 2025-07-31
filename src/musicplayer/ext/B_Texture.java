package musicplayer.ext;

import static org.lwjgl.opengl.GL40.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

public class B_Texture {
	
	int texture;
	
	public B_Texture(String filename) throws FileNotFoundException, IOException {
		
		// https://learnopengl.com/Getting-started/Textures
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture)
		;
		// set the texture wrapping/filtering options (on the currently bound texture object)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);	
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		// load and generate the texture
		BufferedImage image = ImageIO.read(new FileInputStream(filename));
		int width = image.getWidth();
		int height = image.getHeight();
		
		byte[] raw_gldata = new byte[(width*height)*4];
		int[] argb = image.getRGB(0, 0, width, height, null, 0, width);
		
		for (int i = 0; i < argb.length; i++) {
			raw_gldata[(i*4)+0] = (byte) ((argb[i] >> 16));
			raw_gldata[(i*4)+1] = (byte) ((argb[i] >>  8));
			raw_gldata[(i*4)+2] = (byte) ((argb[i]      ));
			raw_gldata[(i*4)+3] = (byte) ((argb[i] >> 24));
	    }
		
		// image is flipped vertically, so un-flip it
		byte[] gldata = new byte[raw_gldata.length];
		for (int row = 0; row < height; row++) {
			int new_row = ((height-row)-1)	* (width*4);
			int old_row = (row)				* (width*4);
			for (int column = 0; column < width; column++) {
				int offset = (column*4);
				gldata[new_row + offset+0] = raw_gldata[old_row + offset+0];
				gldata[new_row + offset+1] = raw_gldata[old_row + offset+1];
				gldata[new_row + offset+2] = raw_gldata[old_row + offset+2];
				gldata[new_row + offset+3] = raw_gldata[old_row + offset+3];
		    }
	    }
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, toByteBuffer(gldata));
		glGenerateMipmap(GL_TEXTURE_2D);
		
	}
	
	/** Why can't i just use ByteBuffer.wrap(data)? No one knows. */
	private ByteBuffer toByteBuffer(byte[] data) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

}
