package dinaBOT.navigation;

/**
 * Classes must implement this interface to register for notification of new obstacles being added to the map.
 *
 * @author Severin Smith, Stepan Salenikovich
 * @see Map
 * @version 1
*/
public interface MapListener {

	/**
	 * This method is called by the Map to notify the MapListener of a new obstacle. You can register your MapListener with a Map using {@link Map#registerListener(MapListener listener)}.
	 *
	 * @param x the x position of the obstacle
	 * @param y the y position of the obstacle
	 * @see Map#registerListener(MapListener listener)
	*/
	public void newObstacle(int x, int y);
		
}
