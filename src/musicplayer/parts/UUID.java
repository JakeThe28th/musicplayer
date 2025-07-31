
package musicplayer.parts;

public class UUID {
	
	public UUID(String album, String identifier) {
		this.album = album;
		this.identifier = identifier;
	}
	
	String album = "unknown";
	String identifier = "unknown";
	
	public String toString() {
		return album + ":" + identifier;
	}
	
}
