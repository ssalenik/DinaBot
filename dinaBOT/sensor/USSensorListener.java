package dinaBOT.sensor;

/**
 * Classes must implement this interface to register for notification of new ultrasonic sensor data.
 *
 * @author Gabriel Olteanu, Severin Smith, Vinh Phong Buu
 * @see LineDetector
 * @see dinaBOT.navigation.Odometer
 * @see dinaBOT.navigation.ArcOdometer
 * @version 2
*/
public interface USSensorListener {

	/**
	 * This method is called by the {@link USSensor} to notify the listener of new incoming data
	 *
	 * @param new_values the new data from the ultrasonic sensor
	 * @param sensor the sensor calling the listener
	 * @see USSensor#registerListener(USSensorListener listener)
	*/
	public void newValues(int[] new_values, USSensor sensor);

}