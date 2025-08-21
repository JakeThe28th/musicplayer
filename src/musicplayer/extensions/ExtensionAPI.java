package musicplayer.extensions;

import java.util.ArrayList;
import java.util.HashMap;

import musicplayer.audio.AudioSource;
import musicplayer.extensions.builtin.BuiltinAudioReader;

public class ExtensionAPI {
	
	static HashMap<String, Extension> 				extensions 	 = new HashMap<>();
	static ArrayList<AudioReaderExtension> 			audioreaders = new ArrayList<>();

	public static void loadExtension(Extension extension) {
		extensions.put(extension.identifier(), extension);
		extension.onLoad();
	}
	
	public static void init() {
		loadExtension(new BuiltinAudioReader());
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
