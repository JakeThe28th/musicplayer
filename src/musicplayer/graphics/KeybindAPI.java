package musicplayer.graphics;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

public class KeybindAPI {
	
	public interface KeybindEvent { public void run(); }
	
	static HashMap<String, Keybind> keybinds = new HashMap<>();
	
	public record Key(int scancode, int action, int mods) { }
	static record Keybind(KeybindEvent event, Key...keys) { }
	
	public static void bindkey(String bind_name, int key, int action, KeybindEvent event) {
		bind(bind_name, GLFW.glfwGetKeyScancode(key), action, event);
	}
	
	public static void bind(String bind_name, int scancode, int action, KeybindEvent event) {
		keybinds.put(bind_name, new Keybind(event, new Key(scancode, action, 0)));
	}
	
	public static void tick() {
		for (String name : keybinds.keySet()) {
			Keybind bind = keybinds.get(name);
			boolean pressed = true;
			for (Key key : bind.keys) { if (key.action != Input.action(key.scancode)) pressed = false; }
			if (pressed) bind.event.run();
		}
	}
	
}
