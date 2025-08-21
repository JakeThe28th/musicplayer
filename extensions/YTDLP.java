import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import musicplayer.audio.AudioSource;
import musicplayer.extensions.AudioReaderExtension;
import musicplayer.extensions.Extension;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.utility.Log;

public class YTDLP extends Extension implements AudioReaderExtension {

	public static final String[] TYPES = new String[] { "webloader" };
	
	@Override public String   identifier() 		{ return "utility;ytdlp"; }
	@Override public String[] supportedTypes() 	{ return TYPES; }
	
	public static String download_file_type = "wav";
	
	String cached_song_directory;
	String cached_song_list_filename;
	ArrayList<String> cached_urls = new ArrayList<String>();
	ArrayList<String> cached_url_types = new ArrayList<String>();

	@Override
	public void onLoad() throws IOException {
		// TODO Auto-generated method stub
		ExtensionAPI.registerAudioReader(this);
		
		new File(working_directory).mkdirs();
		
		cached_song_directory = working_directory + "cache/";
		cached_song_list_filename = working_directory + "cachedsongs.txt";

		// Read list of already downloaded links
		File cache = new File(cached_song_list_filename);
		if (cache.exists()) {
			String[] lines = Files.readString(cache.toPath()).split("\n");
			for (int i = 0; i < lines.length / 2; i++) {
				cached_urls.add(lines[(i*2) + 0].strip());
				cached_url_types.add(lines[(i*2) + 1].strip());
			}
		}
	}
	
	@Override
	public AudioSource read(String filename) {
		try { 
			String url = Files.readString(Paths.get(filename)).strip();
			
			if (!cached_urls.contains(url)) cache(url);
			
			int index = cached_urls.indexOf(url);
			String type = cached_url_types.get(index);
			return ExtensionAPI.readAudio(cached_song_directory + index + "." + type, type);
			
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	
	private void cache(String url) throws IOException {
		String location = cached_song_directory + cached_urls.size() + "." + download_file_type;
		download(url, location, download_file_type); 
		cached_urls.add(url);
		cached_url_types.add(download_file_type);
		
		// save cache to disk
		String cachestring = "";
		for (int i = 0; i < cached_urls.size(); i++) {
			cachestring += cached_urls.get(i) + "\n";
			cachestring += cached_url_types.get(i) + "\n";
		}
		
		if (Files.exists(Paths.get(cached_song_list_filename))) {
			Files.delete(Paths.get(cached_song_list_filename));
		}
		Files.writeString(Paths.get(cached_song_list_filename), cachestring, StandardOpenOption.CREATE);
	}
	
	/** Download w/ YT-DLP on the command line*/
	private void download(String url, String output_file, String format) throws IOException {
		Log.send(identifier() + ": Downloading " + url);
		
		ProcessBuilder builder = new ProcessBuilder(
				working_directory + "yt-dlp.exe", 
				url,
				"--extract-audio",
				"--audio-format",
				format,
				"-o",
				output_file
				);
		
		
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
        }
        
	}
}
