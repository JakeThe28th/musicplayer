package musicplayer.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.audio.AudioSource;
import musicplayer.audio.io.pcm.WAVFile;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

public class Song {

	public Song(File directory, UUID uuid) throws IOException {
		this.directory = directory;
		
		// ... read metadata ... //
		this.fields = Utility.readKeyValue(Paths.get(directory.getPath() + "\\info.txt"));

		this.uuid = uuid;
	}
	
	/** For creating songs from scratch ONLY */
	public Song(UUID uuid, HashMap<String, String> fields) throws IOException {
		this.fields = fields;
		this.uuid = uuid;
		directory = new File(Library.album_directory + "\\" + uuid.album + "\\" + uuid.identifier + "\\");
	}
	
	File directory;
	HashMap<String, String> fields = new HashMap<String, String>();
	AudioSource audio;
	
	public String name() { return fields.get("name"); }
	
	public AudioSource audio() throws IOException, UnsupportedAudioFileException { 
		if (audio == null) {
			String value = fields.get("file");
			String path 		= directory.getPath() + "\\" + value;
			String extension 	= value.substring(value.lastIndexOf('.') + 1, value.length());
			audio 				= ExtensionAPI.readAudio(path, extension, uuid());
		}
		return audio;
	}
	
	public void free_audio() { 
		if (audio != null) {
			if (audio != MusicPlayer.EMPTY) audio.end();
			audio = null;
		}
	}

	private UUID uuid;
	public UUID uuid() { return uuid; }

	public byte[] temporary_file;
	
	/** Saves this song to disk. 
	 * @throws IOException */
	public void save() throws IOException {
		
		// Create a folder for this song if it doesn't already exist
		String directory = Library.album_directory + uuid.album + "\\";
		File album_folder = new File(directory + uuid.identifier + "\\");
		album_folder.mkdirs();
		
		// Save fields
		Utility.writeKeyValue(Paths.get(album_folder.toString() + "\\info.txt"), fields);
		
		// Save file
		if (temporary_file != null) {
			Files.write(Paths.get(album_folder.toString() + "\\" + fields.get("file")), temporary_file);
		} else {
			Log.send("Tried to save a song with no data. Did you call Album::save() instead of Playlist::save()...?");
		}
		
	}

}