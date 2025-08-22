package musicplayer.extensions;

import java.io.IOException;

public abstract class Extension {
	
	public abstract String identifier();
	public abstract void onLoad() throws IOException;
	
	public void onClose() {};

	protected String working_directory;
	public void setWorkingDirectory(String string) { working_directory = string; }

}
