package musicplayer.utility;

public class Utility {

	public static double lerp(double x, double y, double t) {
		return (1 - t) * x + t * y;
	}
	
}
