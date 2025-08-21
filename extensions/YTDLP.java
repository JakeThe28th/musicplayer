import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import musicplayer.audio.AudioSource;
import musicplayer.extensions.AudioReaderExtension;
import musicplayer.extensions.Extension;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.parts.Album;
import musicplayer.parts.Playlist;
import musicplayer.parts.Song;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

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
		
		albumFromPlaylist("https://www.youtube.com/playlist?list=PLQ-AumaVerPcpI809WoHDXohS0kFIvwaQ");
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
		
		Utility.runCommand(
				working_directory + "yt-dlp.exe", 
				url,
				"--extract-audio",
				"--audio-format",
				format,
				"-o",
				output_file
				);
		
	}
	
	
	
	private void albumFromPlaylist(String link) throws IOException {
		Utility.runCommand(
				working_directory + "yt-dlp.exe", 
				link,
				"--write-info-json",
				"--skip-download",
				"-o",
				working_directory + "temp\\%(playlist_index)s"
				);
		
		JSONObject info = new JSONObject(Files.readString(Paths.get(working_directory + "temp\\0.info.json")));
		
		String album_title = info.getString("title");
		int count = info.getInt("playlist_count");
		
		Album album = new Album(album_title);
		
		for (int i = 1; i <= count; i++) {
			JSONObject song_info = new JSONObject(Files.readString(Paths.get(working_directory + "temp\\"+i+".info.json")));
			
			String song_title = song_info.getString("title");
			String song_id = song_info.getString("id");
			
			byte[] data = ("https://www.youtube.com/watch?v=" + song_id).getBytes();
			
			HashMap<String, String> fields = new HashMap<String, String>();
			fields.put("file", "song.webloader");
			fields.put("name", song_title);
			
			Song song = new Song(new UUID(album_title, song_id), fields);
			song.temporary_file = data;
			album.set(song);
		}
		
		album.save();

	
	}
	
}
