package dinaBOT.navigation;

/**
 *
*/
public interface Navigation {
	
	/**
	 *
	 * @param x
	 * @param y
	 * @return
	*/
	public boolean goTo(double x, double y);
		
	public void interrupt();
	
}