package musicplayer.extensions.types;

import musicplayer.audio.AudioSource;
import musicplayer.parts.UUID;

public interface AudioReaderExtension {
	
	public AudioSource read(String filename, UUID song);
	public String[] supportedTypes();

}
