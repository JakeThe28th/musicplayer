package musicplayer.extensions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import musicplayer.MainProgram;
import musicplayer.audio.AudioSource;
import musicplayer.extensions.builtin.BasicImporter;
import musicplayer.extensions.builtin.BuiltinAudioReader;
import musicplayer.extensions.builtin.ProgramSettings;
import musicplayer.extensions.builtin.search.Search;
import musicplayer.extensions.types.AudioReaderExtension;
import musicplayer.extensions.types.GUIModifierExtension;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

public class ExtensionAPI {
	
	static HashMap<String, Extension> 				extensions 	  = new HashMap<>();
	static ArrayList<AudioReaderExtension> 			audioreaders  = new ArrayList<>();
	static ArrayList<GUIModifierExtension> 			guimodifiers = new ArrayList<>();

	public static void loadExtension(Extension extension) throws IOException {
		
		((Extension) extension).setWorkingDirectory("extensions/" + ((Extension) extension).identifier() + "/");
		
		GraphicsAPI.center_text(0, 0, "Initializing Extensions...");
		GraphicsAPI.center_text(0, 30, "Loaded "+ extension.identifier() +"...");
		GraphicsAPI.render();
		
		extensions.put(extension.identifier(), extension);
		extension.onLoad();
	}
	
	public static void init() throws IOException {
		
		loadExtension(new BuiltinAudioReader());
		loadExtension(new ProgramSettings());
		loadExtension(new BasicImporter());
		
		// I know this extension is kinda cheating by
		// having multiple .java source files,
		// but to be fair that was only because organizing
		// it in one file was getting unwieldy so I
		// think it's still fine to treat it as an extension
		loadExtension(new Search());

		// Load external extensions
		for (Object extension : Utility.loadClassesFromFolder("extensions/")) {
			if (Extension.class.isAssignableFrom(extension.getClass())) loadExtension((Extension) extension);
		}
	
	}
	
	public static void tick() {
		for (String extension : extensions.keySet()) {
			extensions.get(extension).onTick();
		}
	}
	
	public static void end() {
		for (String extension : extensions.keySet()) {
			Log.send("Closing extension " + extension);
			extensions.get(extension).onClose();
		}
	}
	
	public static void registerAudioReader(AudioReaderExtension e) {
		audioreaders.add(e);
	}
	
	public static AudioSource readAudio(String filename, String extension, UUID song) {
		for (AudioReaderExtension e : audioreaders) {
			for (String type : e.supportedTypes()) if (type.equals(extension)) {
				return e.read(filename, song);
			}
		}
		MainProgram.showError("Unknown format '" + extension + "', Failed to read audio file '" + filename + "'");
		return null;
	}

	public static void registerGUIModifier(GUIModifierExtension e) {
		guimodifiers.add(e);
	}
	
	public static void modifyGUI(G_Element element) {
		for (GUIModifierExtension e : guimodifiers) {
			e.modify(element);
		}
	}
	
}
