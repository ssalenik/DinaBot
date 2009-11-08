package dinaBOT.navigation;

/**
 *
*/
public interface Navigation {
	
	/**
	 *
	 * @param x
	 * @param y
	 * @return interrupted
	*/
	public boolean goTo(double x, double y);
		
	public void interrupt();
	
}