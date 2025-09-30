package musicplayer.graphics;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

public class KeybindAPI {
	
	public interface KeybindEvent { public void run(); }
	
	static HashMap<String, Keybind> keybinds = new HashMap<>();
	public static boolean lock_keybinds = false;
	
	public record Key(int scancode, int action, int mods) { }
	static record Keybind(boolean bypass_lock, KeybindEvent event, Key...keys) {
		public Keybind(KeybindEvent event, Key...keys) {
			this(false, event, keys);
		}
	}
	
	public static void bindkey(String bind_name, int key, int action, KeybindEvent event) {
		bind(bind_name, GLFW.glfwGetKeyScancode(key), action, event);
	}
	
	public static void bind(String bind_name, int scancode, int action, KeybindEvent event) {
		keybinds.put(bind_name, new Keybind(event, new Key(scancode, action, 0)));
	}
	
	public static void bind(boolean bypass_lock, String bind_name, int scancode, int action, KeybindEvent event) {
		keybinds.put(bind_name, new Keybind(bypass_lock, event, new Key(scancode, action, 0)));
	}
	
	public static void tick() {
		should_stop_typing = false;
		for (String name : keybinds.keySet()) {
			Keybind bind = keybinds.get(name);
			if (lock_keybinds && !bind.bypass_lock) { continue; }
			boolean pressed = true;
			for (Key key : bind.keys) { if (key.action != Input.action(key.scancode)) pressed = false; }
			if (pressed) bind.event.run();
		}
	}

	public static boolean should_stop_typing = false;
	public static boolean shouldStopTyping() {
		return should_stop_typing || Input.action(GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_ESCAPE)) == GLFW.GLFW_PRESS;
	}
	
}
