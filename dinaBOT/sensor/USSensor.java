package dinaBOT.sensor;

import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPort;

import dinaBOT.util.DinaList;

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

	public USSensor(SensorPort port) {
		sensor = new UltrasonicSensor(port);

		listeners = new DinaList<USSensorListener>();

		latest_values = new int[8];
		sensor.off();

		start();
	}

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
						latest_values[i] = (int)((double)(latest_values[i]*latest_values[i])*0.1091-3.3446*(double)latest_values[i]+35.629);
					}
				} else {
					for(int i = 0;i < 8;i++) {
						latest_values[i] = (int)(0.9846*(double)latest_values[i]-0.1456);
					}
				}
			} else if (this == USSensor.high_sensor) {
				for(int i = 0;i < 8;i++) {
					latest_values[i] = (int)(1.3013*(double)latest_values[i]-0.7027);
				}
			}

			notifyListeners();
		}
	}

	void notifyListeners() {
		for(int i = 0;i < listeners.size();i++) {
			listeners.get(i).newValues(latest_values, this);
		}
	}

	public void registerListener(USSensorListener listener) {
		listeners.add(listener);
	}

	public void start() {
		stop();
		running = true;
		us_sensor_thread = new Thread(this);
		us_sensor_thread.setDaemon(true);
		us_sensor_thread.start();
	}

	public void stop() {
		if(running) {
			running = false;
			while(us_sensor_thread != null && us_sensor_thread.isAlive()) Thread.yield();
		}
	}

}