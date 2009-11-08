package dinaBOT.sensor;

/**
 * This interface specifies the interfaces that classes to implement to resiter for notification of line cross events from the left and right LineDetectors.
 *
 * @author Gabriel Olteanu, Severin Smith, Vinh Phong Buu
 * @see LineDetector
 * @see dinaBOT.navigation.Odometer
 * @see dinaBOT.navigation.ArcOdometer
 * @version 2
*/
public interface LineDetectorListener {
	
	/**
	 * This method is called by the <code>detector</code> to notify the LineDetectorListener of a line cross event. You can register you LineDetectorListener with a LineDetector using {@link LineDetector#registerListener(LineDetectorListener listener)}.
	 *
	 * @param detector the LineDetector calling this listener
	 * @see LineDetector#registerListener(LineDetectorListener listener)
	*/
	public void lineDetected(LineDetector detector);
	
}