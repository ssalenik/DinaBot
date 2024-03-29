package dinaBOT.sensor;

/**
 * Classes must implement this interface to register for notification of line cross events from the left and right LineDetectors.
 *
 * @author Gabriel Olteanu, Severin Smith, Vinh Phong Buu
 * @see LineDetector
 * @see dinaBOT.navigation.Odometer
 * @see dinaBOT.navigation.ArcOdometer
 * @version 2
*/
public interface LineDetectorListener {

	/**
	 * This method is called by the <code>detector</code> to notify the LineDetectorListener of a line cross event. You can register your LineDetectorListener with a LineDetector using {@link LineDetector#registerListener(LineDetectorListener listener)}.
	 *
	 * @param detector the LineDetector calling this listener
	 * @see LineDetector#registerListener(LineDetectorListener listener)
	*/
	public void lineDetected(LineDetector detector);

}