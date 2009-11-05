package dinaBOT.sensor;

import lejos.nxt.*;

public class LineDetector implements Runnable {
		
	public static final LineDetector left = new LineDetector(new LightSensor(SensorPort.S1, true), LineDetectorListener.Position.LEFT);
	public static final LineDetector right = new LineDetector(new LightSensor(SensorPort.S2, true), LineDetectorListener.Position.RIGHT);

	static final int THRESHOLD = 450;
	
	LightSensor sensor;
	LineDetectorListener.Position position;

	int previous_reading;
	
	LineDetectorListener listener;
	
	boolean running;
	Thread line_detector_thread;
	
	private LineDetector(LightSensor sensor, LineDetectorListener.Position position) {
		this.sensor = sensor;
		this.position = position;
		previous_reading = 0;
		start();
	}
	
	public int getLatestReading() {
		return previous_reading;
	}
	
	void notifyListener() {
		if(listener != null) listener.lineDetected(position);
	}
	
	public void registerListener(LineDetectorListener listener) {
		this.listener = listener;
	}
	
	public void start() {
		stop();
		running = true;
		line_detector_thread = new Thread(this);
		line_detector_thread.setDaemon(true);
		line_detector_thread.start();
	}
	
	public void stop() {
		if(running) {
			running = false;
			while(line_detector_thread != null && line_detector_thread.isAlive()) Thread.yield();
		}
	}
	
	public void run() {
		while(running){
			int new_reading = sensor.getNormalizedLightValue();
			if(new_reading < THRESHOLD && previous_reading > THRESHOLD) notifyListener();
			previous_reading = new_reading;
		}		
	}
	
}