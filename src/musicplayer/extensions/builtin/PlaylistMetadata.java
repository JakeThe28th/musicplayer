package musicplayer.extensions.builtin;

import musicplayer.MainProgram;
import musicplayer.components.search.Search;
import musicplayer.components.settings.G_ElementPair;
import musicplayer.extensions.Extension;
import musicplayer.graphics.IconType;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Icon;
import musicplayer.gui.G_List;
import musicplayer.gui.G_Text;
import musicplayer.gui.G_TypingBox;
import musicplayer.gui.enums.Alignment;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.gui.screens.G_PlaylistScreen;
import musicplayer.gui.screens.Screen;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
import musicplayer.utility.Log;

public class PlaylistMetadata extends Extension {
	
	@Override public String   identifier() 		{ return "builtin;playlistmetadata"; }
	
	@Override
	public void onLoad() {
		MainProgram.registerScreen(G_PlaylistMetadataScreen.instance);
		G_PlaylistScreen.register_header_icon(edit_playlist_icon, false);
	}
	
	G_Icon 		edit_playlist_icon 			= new G_Icon(IconType.GENERIC_EDIT)
	{ @Override public void onClick() { 
		MainProgram.change_screen(G_PlaylistMetadataScreen.instance.identifier());
		G_PlaylistMetadataScreen.instance.setPlaylist(MusicPlayer.current_view_playlist());
	} };
	
	static class G_PlaylistMetadataScreen extends G_Element implements Screen {
		
		public void setPlaylist(Playlist playlist) {
			properties.clear();
			for (String field : playlist.fields()) {
				G_Text field_name = new G_Text();
				field_name.text(field);
				
				G_TypingBox field_value = new G_TypingBox();
				field_value.rawtext(playlist.metadata(field));
				
				G_ElementPair pair = new G_ElementPair(field_name, field_value, 0.25f);
				
				properties.add(pair);
				// Log.send(field);
			}
			G_TypingBox.current_typing_box = null;
		}
		 
		public static G_PlaylistMetadataScreen instance = new G_PlaylistMetadataScreen();
		
		@Override public G_Element instance() { return instance; }
		@Override public String identifier() { return "builtin;playlistmetadata"; }
		
		G_Icon home = new G_Icon(IconType.GENERIC_HOME)
		{ @Override public void onClick() { 
			MainProgram.change_screen(G_HomeScreen.IDENTIFIER);
		}};
		G_Icon back = new G_Icon(IconType.CONTROL_SKIP_PREVIOUS)
		{ @Override public void onClick() { 
			// TODO: add an onChange or something event to Screen, and put this in G_PlayListScreen
			if (MusicPlayer.current_view_playlist() != null) MusicPlayer.reload_view_playlist();
			MainProgram.previous_screen();
		}};
		G_List icons = new G_List(back, home);
		G_List properties = new G_List();
		
		{
			this.addSubElement(icons);
			icons.halign(Alignment.MIDDLE);
			properties.verticalify();
			this.addSubElement(properties);
		}

		@Override
		public void recalculate_size() {
//			icons.recalculate_size();
//			properties.recalculate_size();
		}

		@Override
		public void layout(int left, int top, int right, int bottom) {
			icons.layout(left, top, right, top+home.height());
			properties.layout(left, top+icons.height(), right, bottom);
		}

		@Override
		public void draw(int depth) {
			icons.draw(depth+2);
			properties.draw(depth+1);
		}
		@Override
		public void foo() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void tickAnimation() {
			// TODO Auto-generated method stub
			
		}
		 
	 }

}