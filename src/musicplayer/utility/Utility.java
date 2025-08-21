package musicplayer.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import musicplayer.extensions.Extension;
import musicplayer.graphics.GraphicsAPI;

public class Utility {

	public static double lerp(double x, double y, double t) {
		return (1 - t) * x + t * y;
	}
	
	public static ArrayList<Object> loadClassesFromFolder(String path) throws IOException {
		ArrayList<Object> classes = new ArrayList<Object>();
        URLClassLoader classloader = new URLClassLoader(new URL[] { Paths.get(path).toUri().toURL() });
        
        for (File file : new File(path).listFiles()) {
        	if (!file.getName().endsWith(".java")) continue;
        	Log.send("Loading extension: " + file.toString());
        	
    		GraphicsAPI.center_text(0, 0, "Initializing Extensions...");
    		GraphicsAPI.center_text(0, 30, "Reading class"+ file.toString() +"...");
    		GraphicsAPI.render();
        	
        	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		    compiler.run(null, null, null, file.getAbsolutePath());
			
			String name = file.getName().toString();
			name = name.substring(0, name.lastIndexOf('.'));
			Extension extension;
			try {
				extension = (Extension) classloader.loadClass(name).getConstructor().newInstance();
				classes.add(extension);
			} catch (Exception e) { Log.send("Failed to load an extension: " + file.toString()); e.printStackTrace(); }
        }
        classloader.close();
		return classes;
	}
	
	public static void runCommand(GenericSingleStringInterface tick, GenericInterface finish, String...command) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);
		
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
            if (tick != null) tick.run(line);
        }
        if (finish != null) finish.run();
	}
	
	public static void delete(File file) {
		if (file.isDirectory()) {
			for (File c : file.listFiles()) delete(c);
		} else {
			file.delete();
		}
	}
	
}
