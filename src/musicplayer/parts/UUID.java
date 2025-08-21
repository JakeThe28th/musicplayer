
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
	
	@Override
	public boolean equals(Object other) {
		if ( !(other instanceof UUID) ) return false;
		return 
			((UUID) other).album.equals(album) && 
			((UUID) other).identifier.equals(identifier);
	}
	
	@Override
	public int hashCode() {
		return (album + identifier).hashCode();
	}
	
}
