package musicplayer.extensions.builtin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import musicplayer.MainProgram;
import musicplayer.extensions.Extension;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.parts.Album;
import musicplayer.parts.Library;
import musicplayer.utility.Utility;

/** Built-in extension that handles importing individual songs, playlists, and albums. */
public class BasicImporter extends Extension {

//	TODO
//	register dragNDrop()
//	   -- If file.endswith ".zip", and view == library, add album/playlist
//	   -- if file.endswith ".wav" and view == playlist, popup add  song  dialog
//	   
//	maybe like
//	MainProgram . register (G_Element custom screen ) ?? and switch to it for extra settings?
	
	String unzip_directory;

	@Override public String   identifier() 		{ return "builtin;basicimporter"; }
	@Override public void onLoad() throws IOException {

		new File(working_directory).mkdirs();
		unzip_directory = working_directory + "extract/";
		
		addAlbumHooks();
		
	}

	private void addAlbumHooks() {
		G_HomeScreen.menu_options_album.add(new Option("Import album from folder of audio files", () -> {
			
			String folder = TinyFileDialogs.tinyfd_selectFolderDialog("Folder with audio to import", "");
			if (folder == null) return;
						
			String album_name = TinyFileDialogs.tinyfd_inputBox(
					MainProgram.PROGRAM_TITLE + " ", 
					"Name of imported album", 
					new File(folder).getName());
			if (album_name == null) return;
			
			String album_identifier = Utility.asValidIdentifier(album_name + ";importedfolder" + ((int) (Math.random() * 10000)));
	
			String root = Library.album_directory + album_identifier + "\\";
			
			try { Files.list(Paths.get(folder)).forEach((f) -> {
				String song_name = f.getFileName().toString();
				try {
					Path path = Paths.get(root + song_name + "\\" + song_name);
					path.getParent().toFile().mkdirs();
					Files.writeString(Paths.get(path.getParent().toString() + "\\info.txt"),
							"name=" + song_name + "\n" +
							"file=" + song_name + "\n");
					Files.copy(f, path);
				} catch (IOException e) { e.printStackTrace(); }
			});	} catch (IOException e) { e.printStackTrace(); }
				
			try { 
				Files.writeString(Paths.get(root + "info.txt"), "name=" + album_name);
				// All we've done is copy files, so we should also...
				// ...actually register the album...
				Library.registerAlbum(new Album(new File(root))); 
				} catch (IOException e) { e.printStackTrace(); }

		}));
		
	}
	

}
