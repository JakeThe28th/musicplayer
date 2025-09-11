import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import musicplayer.MainProgram;
import musicplayer.audio.AudioSource;
import musicplayer.extensions.Extension;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.extensions.types.AudioReaderExtension;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Song;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

public class FFMPEG extends Extension implements AudioReaderExtension {

	public static final String[] TYPES = new String[] { "oga", "ogg", "m4a", "mp3" };
	
	@Override public String   identifier() 		{ return "utility;ffmpeg"; }
	@Override public String[] supportedTypes() 	{ return TYPES; }
	
	public static String conversion_file_type = "wav";
	
	// Conversion Thread //
	static record QueuedConversion(UUID song, File dest, File source) {}
	private static ArrayList<QueuedConversion> queue = new ArrayList<>();
	
	public static synchronized void 		CVqueue	  (QueuedConversion download) 	{ queue.add(download); }
	public static synchronized boolean 		CVhasnext() 							{ return queue.size() > 0; }
	public static synchronized QueuedConversion CVpop() { 
		QueuedConversion q = queue.getFirst(); 
		queue.removeFirst(); 
		return q; 
	}
	
	Thread conversion_thread = new Thread() {
	    public void run() { 
			Log.send("(FFMPEG) Starting conversion thread");
			while (!interrupted()) try {
	    		if (CVhasnext()) {
	    			QueuedConversion q = CVpop();
	    			convert(q.source, q.song, q.dest);
	    		} 
	    	} catch(IOException v) { v.printStackTrace(); } 
	    }  
	};
	// ... //

	String cached_song_directory;
	
	@Override
	public void onLoad() throws IOException {
		// TODO Auto-generated method stub
		ExtensionAPI.registerAudioReader(this);
		new File(working_directory).mkdirs();
		cached_song_directory = working_directory + "cache/";
		new File(cached_song_directory).mkdirs();
		
		if (!new File(working_directory + "ffmpeg\\bin\\").exists()) {
			MainProgram.showError("Can't find ffmpeg bin folder.");
			MainProgram.showError("Please download ffmpeg");
			MainProgram.showError("to be able to open more audio file types.");
			MainProgram.showError("checked: " + working_directory + "ffmpeg/bin/");
			// note: https://github.com/BtbN/FFmpeg-Builds/releases
		}
		
		conversion_thread.start();
	}
	
	@Override
	public void onClose() {
		conversion_thread.interrupt();
		Utility.delete(new File(cached_song_directory));
	}
	
	@Override
	public AudioSource read(String filename, UUID uuid) {
		Song song = Library.getSongFromAlbum(uuid);
		
		File target = new File(
				cached_song_directory 	+ 
				uuid.album 				+ "_" + 
				uuid.identifier 		+ "_" + 
				song.field("file") 		+ "." + 
				conversion_file_type);
		
		if (!target.exists()) {
			if (MusicPlayer.hasLoadProgress(uuid)) return MusicPlayer.EMPTY;
			MusicPlayer.setLoadProgress(uuid, 0.02f);
			CVqueue(new QueuedConversion(uuid, target, new File(filename)));
			return MusicPlayer.EMPTY;
		} else {
			try {
			return ExtensionAPI.readAudio(target.getPath(), "wav", uuid);
			} catch (Exception e) {
				Log.send("FFMPEG: Failed to read converted audio file of track '"+ uuid.toString() +"'");
				Log.trace(e);
				MainProgram.showError("FFMPEG: Failed to read converted audio file");
				MainProgram.showError("of track " + uuid.toString());

				return MusicPlayer.EMPTY;
			}
		}
		
	}
	
	/** Convert w/ffmpeg on the command line*/
	private void convert(File source, UUID song, File dest) throws IOException {
		Log.send(identifier() + ": Converting " + song);
		
		String destination_file = dest.getAbsolutePath();
		//String song_file = Library.getSongFromAlbum(song).field("file");
		//String source_file = new File(Library.album_directory + song.album + "\\" + song.identifier + "\\" + song_file).getAbsolutePath();
		String source_file = source.getAbsolutePath();

		Utility.runCommand(
				new File(working_directory + "ffmpeg\\bin\\"),
				(line) -> {
					float progress = 0.5f;
					MusicPlayer.setLoadProgress(song, progress);
				},
				() -> {
				},
				"ffmpeg.exe", 
				destination_file,
				"-i",
				source_file
				);
		
		MusicPlayer.finishLoading(song);
		
	}
	
}
