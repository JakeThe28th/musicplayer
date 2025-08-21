package musicplayer.graphics;

import static org.lwjgl.opengl.GL40.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.lwjgl.BufferUtils;

public class Texture {
	
	int texture;
	
	public Texture(String filename) throws FileNotFoundException, IOException {
		this(ImageIO.read(new FileInputStream(filename)));
	}
	
	public Texture(BufferedImage image) {
		this(bufferedImageToTextureData(image), image.getWidth(), image.getHeight());
	}
	
	private static byte[] bufferedImageToTextureData(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		byte[] gldata = new byte[(width*height)*4];
		int[] argb = image.getRGB(0, 0, width, height, null, 0, width);
		
		for (int i = 0; i < argb.length; i++) {
			gldata[(i*4)+0] = (byte) ((argb[i] >> 16));
			gldata[(i*4)+1] = (byte) ((argb[i] >>  8));
			gldata[(i*4)+2] = (byte) ((argb[i]      ));
			gldata[(i*4)+3] = (byte) ((argb[i] >> 24));
	    }
		
		return gldata;
	}

	public Texture(byte[] gldata, int width, int height) {
		
		// https://learnopengl.com/Getting-started/Textures
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		
		// set the texture wrapping/filtering options (on the currently bound texture object)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);	
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		// add the image data to the texture
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

	public void free() {
		glDeleteTextures(texture);
	}

}
