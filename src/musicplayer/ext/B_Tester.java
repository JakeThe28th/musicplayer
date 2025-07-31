package musicplayer.ext;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class B_Tester {

	public static void main(String[] args) throws FileNotFoundException, IOException { new B_Tester().run(); }
	public void run() throws FileNotFoundException, IOException {

		B_Graphics.setup();
		
		float mesh_width = 310.5f;
		float mesh_height = 310.5f;
		B_Mesh mesh = new B_Mesh(new float[] {
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

		while (B_Graphics.isActive()) {
			B_Graphics.queue(mesh, new Matrix4f(), new Vector4f(1,1,1,1), new B_Texture("khronos.png"));
			B_Graphics.render();
		}
		
		B_Graphics.quit();
	}

}
