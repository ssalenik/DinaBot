package dinaBOT.util;

/**
 * This is a collection of usefull simple functions which come up often when programming, they are all statically accessable here
 *
 * @author Severin Smith
 * @version 1

*/
public class Functions {

	/**
	 * This class only has static method and can never be instantiated
	 *
	*/
	private Functions() {
		
	}
	
	/**
	 * Constrains the value to the specified range.
	 *
	 * @param value the value to constrain
	 * @param min the minimum of the range
	 * @param max the maxiumum of the range
	 * @see #constrain(int value, int min, int max)
	*/
	public static double constrain(double value, double min, double max) {
		if(value > max) return max;
		if(value < min) return min;
		return value;
	}
	
	/**
	 * Constrains the value to the specified range.
	 *
	 * @param value the value to constrain
	 * @param min the minimum of the range
	 * @param max the maxiumum of the range
	 * @see #constrain(double value, double min, double max)
	*/
	public static int constrain(int value, int min, int max) {
		if(value > max) return max;
		if(value < min) return min;
		return value;
	}
	
	/**
	 * Maps and constrains the value to the specified range from another range.
	 *
	 * @param value the value to constrain and map
	 * @param low1 the minimum of the initial range
	 * @param high1 the maxiumum of the inital range
	 * @param low2 the minimum of the final range
	 * @param high2 the maxiumum of the final range
	 * @see #map(int value, int low1, int high1, int low2, int high2)
	*/
	public static double map(double value, double low1, double high1, double low2, double high2) {
		double result = (value-low1)/(high1-low1)*(high2-low2)+low2;
		return constrain(result, low2, high2);
	}
	
	/**
	 * Maps and constrains the value to the specified range from another range.
	 *
	 * @param value the value to constrain and map
	 * @param low1 the minimum of the initial range
	 * @param high1 the maxiumum of the inital range
	 * @param low2 the minimum of the final range
	 * @param high2 the maxiumum of the final range
	 * @see #map(double value, double low1, double high1, double low2, double high2)
	*/
	public static int map(int value, int low1, int high1, int low2, int high2) {
		int result = (int)((double)(value-low1)/(double)(high1-low1)*(double)(high2-low2))+low2;
		return constrain(result, low2, high2);
	}
	
}