package dinaBOT.navigation;

/**
 * The Navigation interface specifies a high level movement system which is obstacle and pathing aware.
 *
 * @author Severin Smith
 * @see Odometer
 * @see Movement
 * @version 1
*/
public interface Navigation extends MapListener {

	/**
	 * Request that the robot move to a specified set of coordinates on the grid. This is a high level method and should integrate pathing and obstacle avoidance considerations
	 *
	 * @param x the x coordiate to move to
	 * @param y the y coordiate to move to
	 * @return return less than 0 if the coordinates cannot be reached, 0 if they were reached and more than 0 if the goTo was suspend_interrupt
	 * @see #interrupt()
	*/
	public int goTo(double x, double y, boolean full, boolean pickup_sucess);

	/**
	 * Interrupt an ongoing {@link #goTo(double x, double y, boolean full, boolean pickup_sucess)} call
	 *
	 * @see #goTo(double x, double y, boolean full, boolean pickup_sucess)
	*/
	public void interrupt();

	public void backtrack();

}
