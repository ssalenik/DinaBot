package dinaBOT.sensor;

import lejos.nxt.SensorPort;
import lejos.nxt.LightSensor;

public class LiftDetector {
	
	LightSensor l_sensor;
	
	static final int LIFT_THRESHOLD = 500;
	
	public LiftDetector(LightSensor l_sensor) {
		this.l_sensor = l_sensor;
	}

	public boolean armsUp() {
		return (l_sensor.getNormalizedLightValue() > LIFT_THRESHOLD);
	}
	
}