package musicplayer.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.sound.sampled.UnsupportedAudioFileException;

import disaethia.engine.audio.AudioSource;
import disaethia.io.audio.pcm.WAVFile;

public class Song {

	public Song(File directory) throws IOException {
		this.directory = directory;
		
		// ... read metadata ... //
		String file = Files.readString(Paths.get(directory.getPath() + "\\info.txt"));
		String[] info = file.split("\n");
		for (String text : info) {
			text = text.strip();
			String field = "";
			int i = 0;
			while (i < text.length() && text.charAt(i) != '=') {
				field += text.charAt(i);
				i++;
			}
			String value = text.substring(i+1);
			fields.put(field, value);
			
		}
	}
	
	File directory;
	HashMap<String, String> fields = new HashMap<String, String>();
	AudioSource audio;
	
	public String name() { return fields.get("name"); }
	
	public AudioSource audio() throws IOException, UnsupportedAudioFileException { 
		if (audio == null) {
			String value = fields.get("file");
			if (value.endsWith(".wav")) {
				audio = new AudioSource();
				audio.addAudio(new WAVFile(directory.getPath() + "\\" + value));
			}
		}
		return audio;
	}
	
	public void free_audio() { 
		if (audio != null) {
			audio.end();
			audio = null;
		}
	}

	private UUID uuid;
	public UUID uuid() { return uuid; }
	public void uuid(String album, String identifier) {
		uuid = new UUID(album, identifier);
	}

}