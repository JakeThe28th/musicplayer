package musicplayer.graphics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DEBUG_IMPORT {

	public static void main(String[] args) throws IOException {

		// foreach ($file in Get-ChildItem) { ffmpeg -i $file "out\$file.wav" }
		
		Files.list(Paths.get("C:\\Users\\sumi\\Downloads\\mus\\out")).forEach((f) -> {
			
			String name = f.getFileName().toString();
			String root = "library\\albums\\deltarune\\";
			
			try {
				Path path = Paths.get(root + name + "\\" + name);
				path.getParent().toFile().mkdirs();
				Files.writeString(Paths.get(path.getParent().toString() + "\\info.txt"),
						"name=" + name + "\n" +
						"file=" + name + "\n");
				Files.copy(f, path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}

}
