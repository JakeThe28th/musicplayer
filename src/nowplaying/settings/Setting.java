package nowplaying.settings;

public interface Setting {

	public String serialize();
	public void deserialize(String serialized);
	
}
