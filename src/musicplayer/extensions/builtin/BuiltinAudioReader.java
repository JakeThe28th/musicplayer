package musicplayer.extensions.builtin;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import musicplayer.audio.AudioSource;
import musicplayer.audio.io.pcm.WAVFile;
import musicplayer.extensions.AudioReaderExtension;
import musicplayer.extensions.Extension;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.parts.UUID;

public class BuiltinAudioReader extends Extension implements AudioReaderExtension {

	public static final String[] TYPES = new String[] { "wav" };
	
	@Override public String   identifier() 		{ return "builtin;audioreader"; }
	@Override public String[] supportedTypes() 	{ return TYPES; }
	
	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		ExtensionAPI.registerAudioReader(this);
	}

	@Override
	public AudioSource read(String filename, UUID song) {
		try {
			AudioSource audio = new AudioSource();
			audio.addAudio(new WAVFile(filename));
			return audio;
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
