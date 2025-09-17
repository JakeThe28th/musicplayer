package musicplayer.components.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import musicplayer.MainProgram;
import musicplayer.components.settings.types.ColorSetting;
import musicplayer.components.settings.types.Setting;
import musicplayer.components.settings.types.StringSetting;
import musicplayer.extensions.Extension;
import musicplayer.gui.G_Icon;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.utility.Log;

public class ProgramSettings extends Extension  {
	
	// -- //
	
	G_SettingsScreen settings_screen = G_SettingsScreen.INSTANCE;

	@Override public String   identifier() 		{ return "builtin;settings"; }


	G_Icon 		settings_home_icon 				= new G_Icon("gear")
	{ @Override public void onClick() { 
		MainProgram.change_screen(settings_screen.identifier());
	} };

	
	@Override
	public void onLoad() throws IOException {
		MainProgram.registerScreen(settings_screen);
		G_HomeScreen.register_right_icon(settings_home_icon);
	}
	
	@Override
	public void onTick() {
		
	}

}
