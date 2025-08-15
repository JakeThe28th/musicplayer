package musicplayer.utility;

public class Log {
	
	public static void send(String... strings) {
		String logged_message = "";
		String p = "";
		
		for (String s : strings) {
			logged_message += p + s;
			p = ", ";
		}
		
		System.out.println(logged_message);
	}

	public static void send(float... floats) {
		String logged_message = "";
		String p = "";
		
		for (float f : floats) {
			logged_message += p  + f;
			p = ", ";
		}
		
		System.out.println(logged_message);
	}
	
	public static void send(double... doubles) {
		String logged_message = "";
		String p = "";
		
		for (double d : doubles) {
			logged_message += p  + d;
			p = ", ";
		}
		
		System.out.println(logged_message);
	}

}
