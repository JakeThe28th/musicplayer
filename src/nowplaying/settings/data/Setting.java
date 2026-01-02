package nowplaying.settings.data;

public interface Setting {

	public String serialize();
	public void deserialize(String serialized);
	
}
