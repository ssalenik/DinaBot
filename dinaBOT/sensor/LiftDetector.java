package dinaBOT.sensor;

import lejos.nxt.SensorPort;
import lejos.nxt.LightSensor;

public class LiftDetector implements Runnable {

	LightSensor l_sensor;

	static final int LIFT_THRESHOLD = 450;

	int latest_reading;
	boolean running = true;


	public LiftDetector(LightSensor l_sensor) {
		latest_reading = 0;
		this.l_sensor = l_sensor;
		Thread liftdetect_thread = new Thread(this);
		liftdetect_thread.setDaemon(true);
		liftdetect_thread.start();
	}

	public void run() {
		while (running) {
			latest_reading = l_sensor.readNormalizedValue();
		}
	}

	public int getLatestReading() {
		return latest_reading;
	}

	public boolean armsUp() {
		return (latest_reading > LIFT_THRESHOLD);
	}

}