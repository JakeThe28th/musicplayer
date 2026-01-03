
package nowplaying.parts.data;

public class UUID {
	
	public UUID(String album, String identifier) {
		this.album = album;
		this.identifier = identifier;
	}
	
	public String album = "unknown";
	public String identifier = "unknown";
	
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

	public static UUID from(String uuid) {
		return new UUID(uuid.split(":")[0], uuid.split(":")[1]);
	}
	
}
