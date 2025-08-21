package musicplayer.extensions;

import musicplayer.audio.AudioSource;

public interface AudioReaderExtension {
	
	public AudioSource read(String filename);
	public String[] supportedTypes();

}
