package musicplayer.utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import musicplayer.extensions.Extension;

public class Utility {

	public static double lerp(double x, double y, double t) {
		return (1 - t) * x + t * y;
	}
	
	public static ArrayList<Object> loadClassesFromFolder(String path) throws IOException {
		ArrayList<Object> classes = new ArrayList<Object>();
        URLClassLoader classloader = new URLClassLoader(new URL[] { Paths.get(path).toUri().toURL() });
        
        for (File file : new File(path).listFiles()) {
        	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		    compiler.run(null, null, null, file.getAbsolutePath());
			
			String name = file.getName().toString();
			name = name.substring(0, name.lastIndexOf('.'));
			Extension extension;
			try {
				extension = (Extension) classloader.loadClass(name).getConstructor().newInstance();
				classes.add(extension);
			} catch (Exception e) { Log.send("Failed to load a class: " + file.toString()); e.printStackTrace(); }
        }
        classloader.close();
		return classes;
	}
	
}
