package musicplayer.components;

import java.io.IOException;
import java.util.HashMap;

import musicplayer.components.search.Search;
import musicplayer.components.settings.ProgramSettings;
import musicplayer.extensions.Extension;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.utility.Log;

public class ComponentAPI {
	
	/*  Components are extensions that are *mandatory*
	 *  Deleting builtin extensions might make the pro-
	 *  gram useless, but it'll still run.
	 *  All regular extensions' functionality can be
	 *  loaded from external files.
	 *  Components are builtin extensions referenced by
	 *  the rest of the codebase, so can't be removed.
	 */
	
	static HashMap<String, Extension> 				components 	  = new HashMap<>();
	
	public static void loadComponent(Extension component) throws IOException {
		
		component.setWorkingDirectory("components/" + component.identifier() + "/");
		
		GraphicsAPI.center_text(0, 0, "Initializing Components...");
		GraphicsAPI.center_text(0, 30, "Loaded "+ component.identifier() + "...");
		GraphicsAPI.render();
		
		components.put(component.identifier(), component);
		component.onLoad();
	}

	public static void init() throws IOException {
		loadComponent(new Search());
		loadComponent(new ProgramSettings());
	}
	
	public static void tick() {
		for (String component : components.keySet()) {
			components.get(component).onTick();
		}
	}
	
	public static void end() {
		for (String component : components.keySet()) {
			Log.send("Closing component " + component);
			components.get(component).onClose();
		}
	}
}
