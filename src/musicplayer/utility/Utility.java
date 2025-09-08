package musicplayer.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

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
        
        // Compile .java files
        for (File file : new File(path).listFiles()) {
        	if (!file.getName().endsWith(".java")) continue;
        	
        	Log.send("Compiling extension: " + file.toString());
    		GraphicsAPI.center_text(0, 0, "Initializing Extensions...");
    		GraphicsAPI.center_text(0, 30, "Reading source "+ file.toString() +"...");
    		GraphicsAPI.render();
        	
        	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		    compiler.run(null, null, null, file.getAbsolutePath());
        }
        
        // Load classes (Since source files can generate more than one class, this is a separate step)
        for (File file : new File(path).listFiles()) {
        	if (!file.getName().endsWith(".class")) continue;
        	
        	Log.send("Loading extension class: " + file.toString());
    		GraphicsAPI.center_text(0, 0, "Initializing Extensions...");
    		GraphicsAPI.center_text(0, 30, "Loading class "+ file.toString() +"...");
    		GraphicsAPI.render();
    		
			String name = file.getName().toString();
			name = name.substring(0, name.lastIndexOf('.'));
			try {
				Class<?> class_ = classloader.loadClass(name);
				Object object = class_.getConstructor().newInstance();
				classes.add(object);
			} catch (Exception e) { 
				if (e instanceof NoSuchMethodException) {
					Log.send("Did not load extension class '" + file.toString() + "'; It has no constructor.");
				} else {
					Log.send("Failed to load an extension class: " + file.toString()); e.printStackTrace();
				}
			}
        }
        
        classloader.close();
		return classes;
	}
	
	public static void runCommand(GenericSingleStringInterface tick, GenericInterface finish, String...command) throws IOException {
		runCommand(null, tick, finish, command);
	}
	
	public static void runCommand(File directory, GenericSingleStringInterface tick, GenericInterface finish, String...command) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);
		if (directory != null) builder.directory(directory);
		
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

	public static HashMap<String, String> readKeyValue(Path path) throws IOException {
		HashMap<String, String> fields = new HashMap<String, String>();
		if (!path.toFile().exists()) return fields;

		String string = Files.readString(path);
		
		String file = string; //Files.readString(Paths.get(directory.getPath() + "\\info.txt"));
		if (file.isBlank()) return fields;
		
		String[] info = file.split("\n");
		for (String text : info) {
			text = text.strip();
			String field = "";
			int i = 0;
			while (i < text.length() && text.charAt(i) != '=') {
				field += text.charAt(i);
				i++;
			}
			String value = text.substring(i+1);
			fields.put(field, value);
			
		}
		
		return fields;
	}

	public static void writeKeyValue(Path path, HashMap<String, String> fields) throws IOException {
		String fields_string = "";
		for (String key : fields.keySet()) {
			fields_string += key + "=" + fields.get(key) + "\n";
		}
		Files.writeString(path, fields_string);
	}

	public static File getIfExists(String path, String... extensions) {
		for (String ext : extensions) {
			File get = new File(path + ext);
			if (get.exists()) return get;
		}
		return null;
	}

	public static String asValidIdentifier(String string) {
		string = string.replace(' ', '_');
		String normailize = Normalizer.normalize(string, Normalizer.Form.NFD);
		return normailize.replaceAll("[^a-zA-Z\\-;0-9_]", "");
	}
	
}
