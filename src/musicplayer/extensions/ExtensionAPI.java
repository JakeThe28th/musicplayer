package musicplayer.extensions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import musicplayer.audio.AudioSource;
import musicplayer.extensions.builtin.BuiltinAudioReader;
import musicplayer.utility.Utility;

public class ExtensionAPI {
	
	static HashMap<String, Extension> 				extensions 	 = new HashMap<>();
	static ArrayList<AudioReaderExtension> 			audioreaders = new ArrayList<>();

	public static void loadExtension(Extension extension) throws IOException {
		extensions.put(extension.identifier(), extension);
		extension.onLoad();
	}
	
	public static void init() throws IOException {
		
		loadExtension(new BuiltinAudioReader());
		
		// Load external extensions
		for (Object extension : Utility.loadClassesFromFolder("extensions/")) {	
			((Extension) extension).setWorkingDirectory("extensions/" + ((Extension) extension).identifier() + "/");
			loadExtension((Extension) extension);
		}
	
	}
	
	public static void registerAudioReader(AudioReaderExtension e) {
		audioreaders.add(e);
	}
	
	public static AudioSource readAudio(String filename, String extension) {
		for (AudioReaderExtension e : audioreaders) {
			for (String type : e.supportedTypes()) if (type.equals(extension)) {
				return e.read(filename);
			}
		}
		throw new Error("Failed to read audio file " + filename + ", " + extension);
		//return null;
	}

	
}
