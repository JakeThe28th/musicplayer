package nowplaying.settings.data;

public interface Setting {

	public String serialize();
	/** Returns a copy of this Setting, with its value influenced by
	 *  the serialized data. */
	public Setting deserialize(String serialized);
	
}
