package dinaBOT.sensor;

import dinaBOT.sensor.USSensorListener;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.SensorPort;

public class USSensor implements Runnable {

	public static USSensor high_sensor = new USSensor(SensorPort.S3);
	public static USSensor low_sensor = new USSensor(SensorPort.S4);

	UltrasonicSensor sensor;
	USSensorListener listener;

	Thread us_sensor_thread;
	boolean running;

	int[] latest_values;

	public USSensor(SensorPort port) {
		sensor = new UltrasonicSensor(port);

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
			notifyListener();
		}
	}

	void notifyListener() {
		listener.newValues(latest_values);
	}

	public void registerListener(USSensorListener listener) {
		this.listener = listener;
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