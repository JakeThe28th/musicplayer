package musicplayer;

import java.io.File;
import java.net.URI;
import java.util.HashSet;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Log;

// TODO

public class MusicPlayerCompiler {
	
	public static final String SOURCE_INPUT_DIRECTORY = "src//";
	public static final String LIBRARY_INPUT_DIRECTORY = "external_libs//";
	public static final String LWJGL_INPUT_DIRECTORY = "lwjgl//";

	public static final String SOURCE_OUTPUT_DIRECTORY = "mp_compiled//source//";
	
	public static void main(String[] args) {
		
		// Compile source files
		Log.send("Compiling Source Files");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			HashSet<String> options = new HashSet<String>();
			options.add("-d");
			options.add(SOURCE_OUTPUT_DIRECTORY);
	    	HashSet<JavaFileObject> sources = findJavaSources(SOURCE_INPUT_DIRECTORY);
	    	for (JavaFileObject s : sources) {
	    		Log.send(s.getName());
	    	}
	    	HashSet<JavaFileObject> libraries = findLibraries(LIBRARY_INPUT_DIRECTORY);
	    	sources.addAll(libraries);
	    	for (JavaFileObject s : libraries) {
	    		Log.send(s.getName());
	    	}
	    	HashSet<JavaFileObject> lwjgl = findLibraries(LWJGL_INPUT_DIRECTORY);
	    	sources.addAll(lwjgl);
	    	for (JavaFileObject s : lwjgl) {
	    		Log.send(s.getName());
	    	}
		CompilationTask task = compiler.getTask(null, null, null, options , null, sources);
		task.call();
		
		// A
		
	}

	private static HashSet<JavaFileObject> findJavaSources(String input_dir) {
		
		HashSet<JavaFileObject> set = new HashSet<JavaFileObject>();

        for (File file : new File(input_dir).listFiles()) {
        	if (file.isDirectory()) 
        		set.addAll(findJavaSources(file.getPath()));
        	else if (file.getName().endsWith(".java")) 
        		set.add(new UnSimpleJavaFileObject(file.toURI(), JavaFileObject.Kind.SOURCE));	
        }
        
        return set;
	}
	
	private static HashSet<JavaFileObject> findLibraries(String input_dir) {
		
		HashSet<JavaFileObject> set = new HashSet<JavaFileObject>();

        for (File file : new File(input_dir).listFiles()) {
        	if (file.isDirectory()) 
        		set.addAll(findLibraries(file.getPath()));
        	else if (file.getName().endsWith(".jar")) 
        		set.add(new UnSimpleJavaFileObject(file.toURI(), JavaFileObject.Kind.OTHER));	
        }
        
        return set;
	}
	
	static class UnSimpleJavaFileObject extends SimpleJavaFileObject {
		public UnSimpleJavaFileObject(URI uri, Kind kind) {
			super(uri, kind);
		}
	}

}
