package musicplayer.graphics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class B_Tester {

	public static void main(String[] args) throws FileNotFoundException, IOException { new B_Tester().run(); }
	@SuppressWarnings("deprecation")
	public void run() throws FileNotFoundException, IOException {

		Graphics.setup(400, 400, "Graphicics tesntmeoewo");
		
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
		
		Shapes.color = new Vector4f(1, 0.5f, 0.5f, 1);

		while (Graphics.isActive()) {
			
			//Shapes.rect(10, 10, Window.window_width-10, Window.window_height-10, 0);
			
			Vector2i size = textrenderer.size("Hello, World!");
			//Shapes.rect(10, 10, 10+size.x, 10+size.y, 0);
			//textrenderer.text(10, 10, 10, "Hello, World!");
			
			//RenderQueue.queue(mesh, new Matrix4f().translate(0, 0, 2), new Vector4f(1,1,1,1), khronos);

			Icons.vector_icon(20, 30, 0, "stop", 10);
			Icons.vector_icon(40, 30, 0, "volume", 10);
			
			Icons.bitmap_icon(20, 60, 0, "control_stop", 10);
			Icons.bitmap_icon(40, 60, 0, "control_volume", 60);

			//textrenderer.text(10, 100, 10, "Hello, World!");
			
			RenderQueue.render();
		}
		
		Graphics.quit();
	}

}
