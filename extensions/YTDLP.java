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
import java.util.Stack;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import musicplayer.MainProgram;
import musicplayer.audio.AudioSource;
import musicplayer.extensions.Extension;
import musicplayer.extensions.ExtensionAPI;
import musicplayer.extensions.types.AudioReaderExtension;
import musicplayer.extensions.types.GUIModifierExtension;
import musicplayer.graphics.GraphicsAPI;
import musicplayer.gui.G_Element;
import musicplayer.gui.G_Song;
import musicplayer.gui.extra.Popup.Option;
import musicplayer.gui.screens.G_HomeScreen;
import musicplayer.parts.Album;
import musicplayer.parts.Library;
import musicplayer.parts.MusicPlayer;
import musicplayer.parts.Playlist;
import musicplayer.parts.Song;
import musicplayer.parts.UUID;
import musicplayer.utility.Log;
import musicplayer.utility.Utility;

public class YTDLP extends Extension implements AudioReaderExtension, GUIModifierExtension {

	public static final String[] TYPES = new String[] { "webloader" };
	
	@Override public String   identifier() 		{ return "utility;ytdlp"; }
	@Override public String[] supportedTypes() 	{ return TYPES; }
	
	public static String download_file_type = "m4a";
	
	String cached_song_directory;
	String cached_song_list_filename;
	ArrayList<String> cached_urls = new ArrayList<String>();
	ArrayList<String> cached_url_types = new ArrayList<String>();

	// Download Thread //
	static record QueuedDownload(String url, UUID song) {}
	private static ArrayList<QueuedDownload> queue = new ArrayList<>();
	
	public static synchronized void 		DLqueue	(QueuedDownload download) 	{ queue.add(download); }
	public static synchronized boolean 		DLhasnext() 						{ return queue.size() > 0; }
	public static synchronized QueuedDownload DLpop() { 
		QueuedDownload q = queue.getFirst(); 
		queue.removeFirst(); 
		return q; 
	}
	
	Thread download_thread = new Thread() {
	    public void run() { 
			Log.send("(YT-DLP) Starting download thread");
			while (!interrupted()) try {
	    		if (DLhasnext()) {
	    			QueuedDownload q = DLpop();
	    			cache(q.url, q.song);
	    		} 
	    		try { 
	    			// don't destroy the CPU
	    			Thread.sleep(1000); 
	    		} catch (InterruptedException e) { 
	    			e.printStackTrace(); 
	    			return;
	    		}
	    	} catch(IOException v) { v.printStackTrace(); } 
	    }  
	};
	// ... //

	@Override
	public void onLoad() throws IOException {
		// TODO Auto-generated method stub
		ExtensionAPI.registerAudioReader(this);
		ExtensionAPI.registerGUIModifier(this);
		
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
		
		addAlbumHooks();
		
		download_thread.start();
		
	}
	
	@Override
	public void onClose() {
		try {
			download_thread.interrupt();
		} catch (Exception e) {
			Log.send(identifier() + " Exception while closing thread");
		}
	}
	
	@Override
	public AudioSource read(String filename, UUID song) {
		try { 
			String url = Files.readString(Paths.get(filename)).strip();
			
			if (!cached_urls.contains(url)) {
				if (MusicPlayer.hasLoadProgress(song)) return MusicPlayer.EMPTY;
				MusicPlayer.setLoadProgress(song, 0.02f);
				DLqueue(new QueuedDownload(url, song));
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
		
		if (!new File(location).exists()) {
			MainProgram.showError("Failed to download " + song);
			MainProgram.showError("URL: " + url);
			return;
		}
		
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

	
	/** Download w/ YT-DLP on the command line*/
	private void download(String url, String output_file, String format, UUID song) throws IOException {
		Log.send(identifier() + ": Downloading " + url);
		
		String ffmpeg_location = ExtensionAPI.env("ffmpeg-location");
		Log.send(identifier() + ": Local FFMPEG: " + ffmpeg_location);
					
		String[] args = new String[] {
				working_directory + "yt-dlp.exe", 
				url,
				"--extract-audio",
				"--audio-format",
				format,
				"-o",
				output_file
				};
		
		if (ffmpeg_location != null) {
			args = Utility.append(args, "--ffmpeg-location", ffmpeg_location);
		}
		
		Utility.runCommand(
				(line) -> {
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
						line = line.substring(line.indexOf("]") + 1);
						line = line.substring(0, line.indexOf("%"));
						line = line.strip();
						progress = Float.parseFloat(line) / 100f;
						progress = progress * (1-base_progress);
						progress += base_progress;
						MusicPlayer.setLoadProgress(song, progress);
					} else {
						if (line.contains("Extracting URL")) MusicPlayer.setLoadProgress(song, pre_sleep_progress);
					}
				},
				() -> { },
				args
				);
		
	}
	
	
	
	private void albumFromPlaylist(String link, boolean add) throws IOException {
		
		Utility.delete(new File(working_directory + "temp"));
		
		Utility.runCommand(null, null,
				working_directory + "yt-dlp.exe", 
				link,
				"--write-info-json",	// Save playlist metadata
				"--skip-download",		// Don't download the video
				"--yes-playlist",		// Prefer playlists to videos if the link is ambiguous
				"--flat-playlist",		// Don't write metadata json for each song (..slow..)
				"-o",					// vvv Playlist metadata output file
				working_directory + "temp\\%(playlist_index)s",
				"--print-to-file",		// vvv Print video ID to file
				"id",
				working_directory + "temp\\%(playlist_index)s_id.txt",
				"--print-to-file",		// vvv Print video title to file
				"title",
				working_directory + "temp\\%(playlist_index)s_title.txt"
				);
				
		int digit_count = new File(working_directory + "temp\\").list()[0].indexOf('.');
		String num = String.format("%0"+digit_count+"d", 0);
		
		JSONObject info = new JSONObject(Files.readString(Paths.get(working_directory + "temp\\" + num + ".info.json")));
		
		String album_title = info.getString("title");
		int count = info.getInt("playlist_count");

		String album_identifier = Utility.asValidIdentifier(album_title + ";" + info.getString("id"));
		Album album = new Album(album_identifier);
		album.linked_playlist.name(album_title);
		album.linked_playlist.metadata("yt-playlist-source", info.getString("id"));

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
			
			String song_title = Files.readString(Paths.get(working_directory + "temp\\"+num+"_title.txt")).strip();
			String song_id = Files.readString(Paths.get(working_directory + "temp\\"+num+"_id.txt")).strip();
			
			byte[] data = ("https://www.youtube.com/watch?v=" + song_id).getBytes();
			
			HashMap<String, String> fields = new HashMap<String, String>();
			fields.put("file", "song.webloader");
			fields.put("name", song_title);
			
			Song song = new Song(new UUID(album.getIdentifier(), song_id), fields);
			song.temporary_file = data;
			album.set(song);
		}
		
		album.save();
		
		if (add) Library.registerAlbum(album);
		
	}
	
	

	private void addAlbumHooks() {
		G_HomeScreen.menu_options_album.add(new Option("Import album (YT-DLP)", () -> {
			String name = TinyFileDialogs.tinyfd_inputBox(
					MainProgram.PROGRAM_TITLE + " ", 
					"Import album from playlist: ", 
					"https://www.youtube.com/playlist?list=");
			TinyFileDialogs.tinyfd_messageBox(MainProgram.PROGRAM_TITLE, "The program will freeze during this action. You can view progress in the console log.", "ok", "alert", true);
			if (name != null) {
				try {
					albumFromPlaylist(name, true);
				} catch (IOException e) { e.printStackTrace(); }
			}
		}));
		
	}
	
	@Override
	public void modify(G_Element element) {
		if (element instanceof G_Song) {
			G_Song song = ((G_Song) element);
			Song real_song = Library.getSongFromAlbum(song.song);
			if (real_song.file_extension() != null)
			if (real_song.file_extension().equals(TYPES[0])) {
				try {
					String url = Files.readString(Paths.get(real_song.directory + "\\" + real_song.field("file"))).strip();
					if (!cached_urls.contains(url)) {
						song.name.base_color = GraphicsAPI.TRANSLUCENT_WHITE;
					}
				} catch (IOException e) { e.printStackTrace(); }				
			}
			if (real_song.file_extension() == null) {
				song.name.base_color = GraphicsAPI.TRANSPARENT_RED;
				Log.send("(YT-DLP) null file extensions????");
			}
		}
	}
	
}
