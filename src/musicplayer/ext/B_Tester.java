package musicplayer.ext;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class B_Tester {

	public static void main(String[] args) throws FileNotFoundException, IOException { new B_Tester().run(); }
	public void run() throws FileNotFoundException, IOException {

		Graphics.setup();
		
		float mesh_width = 310.5f;
		float mesh_height = 310.5f;
		Mesh mesh = new Mesh(new float[] {
				mesh_width,  mesh_height, 0.0f,  // top right
				mesh_width,  0, 			0.0f,  // bottom right
			    0, 			  0, 			0.0f,  // bottom left
			    0,  		  mesh_height, 0.0f   // top left 
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
		
		Text textrenderer = new Text();
		
		// GEE WIZ I WONDER IF RECREATING A TEXTURE EVERY FRAME AND NOT FREEING IT USES RAM
		Texture khronos = new Texture("khronos.png");

		while (Graphics.isActive()) {
			
			RenderQueue.queue(mesh, new Matrix4f().translate(0, 0, 2), new Vector4f(1,1,1,1), khronos);

			textrenderer.text(10, 100, 10, "Hello, World!");
			
			RenderQueue.render();
		}
		
		Graphics.quit();
	}

}
