import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import musicplayer.audio.AudioSource;
import musicplayer.extensions.AudioReaderExtension;
import musicplayer.extensions.Extension;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.parts.Album;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
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
		
		
		
	}
	
	@Override
	public AudioSource read(String filename, UUID song) {
		try { 
			String url = Files.readString(Paths.get(filename)).strip();
			
			if (!cached_urls.contains(url)) {
				if (MusicPlayer.hasLoadProgress(song)) return MusicPlayer.EMPTY;
				MusicPlayer.setLoadProgress(song, 0.02f);
				Thread download = new Thread() {
				    public void run() { try { cache(url, song); } catch(IOException v) { v.printStackTrace(); } }  
				};
				download.start();
				return MusicPlayer.EMPTY;
			} else {
				int index = cached_urls.indexOf(url);
				String type = cached_url_types.get(index);
				return ExtensionAPI.readAudio(cached_song_directory + index + "." + type, type, song);
			}
			
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	
	private void cache(String url, UUID song) throws IOException {
		String location = cached_song_directory + cached_urls.size() + "." + download_file_type;
		download(url, location, download_file_type, song); 
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
		
		MusicPlayer.finishLoading(song);

	}
	
	long sleeping_start_time = 0;
	long sleeping_length = 6 * 1000;
	boolean downloaded = false;
	String last_line = "";
	
	/** Download w/ YT-DLP on the command line*/
	private void download(String url, String output_file, String format, UUID song) throws IOException {
		Log.send(identifier() + ": Downloading " + url);
		
		Utility.runCommand(
				(line) -> {
					downloaded = false;
					float progress = 0.5f;
					
					// Forced progress at the "sleeping 6.0 seconds" step so there's
					// at least SOME indication the program is working.
					float base_progress = 0.15f;
					float pre_sleep_progress = 0.05f;
					if (line.contains("Sleeping")) {
						sleeping_start_time = System.currentTimeMillis();
						long sleeping_progress_ms = 0;
						while (sleeping_progress_ms < sleeping_length) {
							sleeping_progress_ms = System.currentTimeMillis()-sleeping_start_time;
							float sleeping_progress = sleeping_progress_ms / ((float) sleeping_length);
								  sleeping_progress *= (base_progress-pre_sleep_progress);
							MusicPlayer.setLoadProgress(song, pre_sleep_progress + sleeping_progress);
						}
					} else if (line.contains("%") && line.startsWith("[download]")) {
						downloaded = true;
						line = line.substring(line.indexOf("]") + 1);
						line = line.substring(0, line.indexOf("%"));
						line = line.strip();
						progress = Float.parseFloat(line) / 100f;
						progress = progress * (1-base_progress);
						progress += base_progress;
						MusicPlayer.setLoadProgress(song, progress);
					} else {
						if (!downloaded) MusicPlayer.setLoadProgress(song, pre_sleep_progress);
					}
				},
				() -> {
				},
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
		
		Utility.delete(new File(working_directory + "temp"));
		
		Utility.runCommand(null, null,
				working_directory + "yt-dlp.exe", 
				link,
				"--write-info-json",
				"--skip-download",
				"-o",
				working_directory + "temp\\%(playlist_index)s"
				);
		
		int digit_count = new File(working_directory + "temp\\").list()[0].indexOf('.');
		String num = String.format("%0"+digit_count+"d", 0);
		
		JSONObject info = new JSONObject(Files.readString(Paths.get(working_directory + "temp\\" + num + ".info.json")));
		
		String album_title = info.getString("title");
		int count = info.getInt("playlist_count");

		
		Album album = new Album(album_title);
		
		// Album Cover
		JSONArray thumbnails = info.getJSONArray("thumbnails");
		String best_link = "";
		int res_square = 0;
		for (int i = 0; i < thumbnails.length(); i++) {
			JSONObject thumb = thumbnails.getJSONObject(i);
			// Check to see if this is bigger
			if (res_square < (thumb.getInt("width")*thumb.getInt("height"))) {
				String url = thumb.getString("url");
				if (url.contains("sqp")) best_link = url;
			}
		}
		
		// If there is a cover...
		if (best_link != "") {
			try {
				URL url = new URL(best_link);
			    BufferedImage image = ImageIO.read(url);
			    if (image != null) album.linked_playlist.cover(image);
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		// Read songs
		
		for (int i = 1; i <= count; i++) {

			num = String.format("%0"+digit_count+"d", i);

			JSONObject song_info = new JSONObject(Files.readString(Paths.get(working_directory + "temp\\"+num+".info.json")));
			
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

		Library.registerAlbum(album);
	
	}
	
}
