package dinaBOT.navigation;

/**
 * The Navigation interface specifies a high level movement system which is obstacle and pathing aware.
 *
 * @author Severin Smith
 * @see Odometer
 * @see Movement
 * @version 1
*/
public interface Navigation {
	
	/**
	 * Request that the robot move to a specified set of coordiates on the grid. This is a high level method and should integrate pathing and obstacle avoidance considerations
	 *
	 * @param x the x coordiate to move to
	 * @param y the y coordiate to move to
	 * @return returns true if the robot sucessfully reached the destination, false if it was interrupted or cannot reach the destination 
	 * @see #interrupt()
	*/
	public boolean goTo(double x, double y);
	
	/**
	 * Interrupt an ongoing {@link #goTo(double x, double y)} call
	 *
	 * @see #goTo(double x, double y)
	*/
	public void interrupt();
	
}