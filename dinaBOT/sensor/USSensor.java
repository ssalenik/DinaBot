package dinaBOT.sensor;

import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPort;

import dinaBOT.util.DinaList;

/**
 * The USSensor is a wrapper for the ultrasonic sensors. It provides a centralized polling system for the sensors as well as a point where filtering and linearization can be implemented. All listeners will be notified of ultrasonic data as it arrives in it's filtered and linearized form.
 *
 * @author Severin Smith, Vinh Phong Buu
 * @see USSensorListener
 * @version 2
*/
public class USSensor implements Runnable {

	/* Class Variables */

	public static USSensor high_sensor = new USSensor(SensorPort.S3);
	public static USSensor low_sensor = new USSensor(SensorPort.S1);

	/* Instance Variables */

	UltrasonicSensor sensor;

	DinaList<USSensorListener> listeners;

	Thread us_sensor_thread;
	boolean running;

	int[] latest_values;

	/**
	 * Creates a new USSensor instance
	 *
	 * @param port the sensor port to be wrapped
	*/
	public USSensor(SensorPort port) {
		sensor = new UltrasonicSensor(port);

		listeners = new DinaList<USSensorListener>();

		latest_values = new int[8];
		sensor.off();

		start();
	}

	/**
	 * This run method from the Runnable interface. It continuously polls the us sensor, there is a 50ms delay between each new data poll. Polled data is first filter/linearized, then stored for reference until the next polling and finally the listeners are notified of the new inbound data.
	 *
	*/
	public void run() {
		while(running) {
			sensor.ping();

			try {
				Thread.sleep(50);
			} catch(Exception e) {

			}

			sensor.getDistances(latest_values);

			if (this == USSensor.low_sensor) {
				if(latest_values[0] <= 26) {
					for(int i = 0;i < 8;i++) {
						if(latest_values[i] != 255) latest_values[i] = (int)((double)(latest_values[i]*latest_values[i])*0.1091-3.3446*(double)latest_values[i]+35.629);
					}
				} else {
					for(int i = 0;i < 8;i++) {
						if(latest_values[i] != 255) latest_values[i] = (int)(0.9846*(double)latest_values[i]-0.1456);
					}
				}
			} else if (this == USSensor.high_sensor) {
				for(int i = 0;i < 8;i++) {
					if(latest_values[i] != 255) latest_values[i] = (int)(1.3013*(double)latest_values[i]-0.7027);
				}
				/*
				if(latest_values[1] == 255) {
					for(int i = 0;i < 8;i++) {
						if(latest_values[i] != 255) latest_values[i] = (int)(1.010638*(double)latest_values[i]-11.042534);
					}
				} else {
					for(int i = 0;i < 8;i++) {
						if(latest_values[i] != 255) latest_values[i] = (int)(1.0619082*(double)latest_values[i]-2.3686924);
					}
				}
				*/
			}

			notifyListeners();
		}
	}

	/**
	 * Notifies the listeners of new data
	 *
	*/
	void notifyListeners() {
		for(int i = 0;i < listeners.size();i++) {
			listeners.get(i).newValues(latest_values, this);
		}
	}

	/**
	 * Register a new {@link USSensorListener listener} with the USSensor. This listener will be notified of new ultrasonic sensor data.
	 *
	 * @param listener the listener to register with this USSensor instance.
	*/
	public void registerListener(USSensorListener listener) {
		listeners.add(listener);
	}

	/**
	 * Start the underlying thread which polls the ultrasonic sensor.
	 *
	*/
	public void start() {
		stop();
		running = true;
		us_sensor_thread = new Thread(this);
		us_sensor_thread.setDaemon(true);
		us_sensor_thread.start();
	}

	/**
	 * Stops the underlying thread which polls the ultrasonic sensor.
	 *
	*/
	public void stop() {
		if(running) {
			running = false;
			while(us_sensor_thread != null && us_sensor_thread.isAlive()) Thread.yield();
		}
	}

}