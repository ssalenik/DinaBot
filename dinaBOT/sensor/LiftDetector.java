package dinaBOT.sensor;

import lejos.nxt.SensorPort;
import lejos.nxt.LightSensor;

public class LiftDetector implements Runnable {
	
	LightSensor l_sensor;
	
	static final int LIFT_THRESHOLD = 500;
	
	int latest_reading;
	boolean running = true;
	
	public LiftDetector(LightSensor l_sensor) {
		latest_reading = 0;
		this.l_sensor = l_sensor;
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