package dinaBOT.sensor;

import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.LightSensor;

public class LiftDetector {
	
	LightSensor l_sensor;
	
	static final int LIFT_THRESHOLD = 500;
	boolean debug = true;
	
	public LiftDetector(LightSensor l_sensor) {
		this.l_sensor = l_sensor;
	}

	public boolean armsUp() {
		
		int value = l_sensor.getNormalizedLightValue();
		LCD.clear();
		LCD.drawInt(value,0,0);
		return (value > LIFT_THRESHOLD);
		
		//return (l_sensor.getNormalizedLightValue() > LIFT_THRESHOLD);
	}
	
}