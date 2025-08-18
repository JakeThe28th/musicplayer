package musicplayer.graphics;

import static org.lwjgl.opengl.GL40.*;

class Mesh {
	
	/*
	 * Since meshes are used only to draw shapes and text, 
	 * we probably won't need to modify them at all after
	 * making them. So I'm just not implementing that...
	 */
	
	int vao = -1;

	int vbo = -1; 	// Vertices
	int tbo = -1; 	// Texture coordinates
	int ibo = -1; 	// Indices
	int cbo = -1; 	// Colors (TODO)
	
	int count = -1;

	public Mesh(float[] vertices, float[] texcoords, int[] indices) {
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = bufferFloats(vertices, 0, 3);
		tbo = bufferFloats(texcoords, 1, 2);
		
		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);  
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		
		count = indices.length;
		
		glBindVertexArray(0);
	}
	
	private static int bufferFloats(float[] data, int location, int size) {
		int abo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, abo);  
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		
		glVertexAttribPointer(location, size, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(location);  
		
		return abo;
	}
	
	public int vao() { return vao; }
	public void bind() { glBindVertexArray(vao); }
	public int count() { return count; }

}
