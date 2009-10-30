package dinaBOT.navigation;

/**
 *
*/
public interface Movement {
	
	/**
	 *
	 * @param distance
	 * @param speed
	*/
	public void goForward(double distance, int speed);
	
	/**
	 *
	 * @param distance
	 * @param speed
	 * @param immediateReturn
	*/
	public void goForward(double distance, int speed, boolean immediateReturn);
	
	/**
	 *
	 * @param angle
	 * @param speed
	*/
	public void turnTo(double angle, int speed);

	/**
	 *
	 * @param angle
	 * @param speed
	 * @param immediateReturn
	*/
	public void turnTo(double angle, int speed, boolean immediateReturn);
	
	/**
	 *
	 * @param speed
	*/
	public void forward(int speed);

	/**
	 *
	 * @param speed
	*/
	public void backward(int speed);

	/**
	 *
	 * @return
	*/
	public boolean isMoving();

	/**
	 *
	*/
	public void stop();
	
}