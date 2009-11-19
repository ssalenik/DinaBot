package dinaBOT.sensor;

import lejos.nxt.SensorPort;
import lejos.nxt.LightSensor;

/**
 *
 * @author Gabriel Olteanu, Severin Smith, Vinh Phong Buu
 * @see LineDetectorListener
 * @see dinaBOT.navigation.Odometer
 * @see dinaBOT.navigation.ArcOdometer
 * @version 2
*/
public class LineDetector implements Runnable {

	/* -- Static Variables --*/

	//The actual left and right line detectors
	public static final LineDetector left = new LineDetector(new LightSensor(SensorPort.S4, true));
	public static final LineDetector right = new LineDetector(new LightSensor(SensorPort.S2, true));

	//The ghetto threshold I'm using
	static final int THRESHOLD = 450;

	/* -- Instance Variables --*/
	LightSensor sensor; //The light sensor associated with this LineDetector

	int previous_reading; //The latest LightSensor value

	LineDetectorListener listener; //The current listner

	boolean running; //The run condition for line_detector_thread
	Thread line_detector_thread;

	/**
	 * Creates a new LineDetector
	 *
	 * @param sensor the sensor this LineDetector is going to use to detect line crosses
	*/
	private LineDetector(LightSensor sensor) {
		//Set up
		this.sensor = sensor;
		previous_reading = 0;

		//Start our thread polling sensor values
		start();
	}

	/**
	 * Get the latest LightSensor reading
	 *
	 * @return the latest LightSensor reading
	*/
	public int getLatestReading() {
		return previous_reading;
	}

	/**
	 * Package protected method which notifies the current listener of a line cross event.
	 *
	*/
	void notifyListener() {
		if(listener != null) listener.lineDetected(this);
	}

	/**
	 * Registers a listener to this LineDetector to be notified of line cross events. Only one listener can currently be registered at a time. Registering a new listener will replace any previously registered listeners.
	 *
	 * @param listener the listener to register
	*/
	public void registerListener(LineDetectorListener listener) {
		this.listener = listener;
	}

	/**
	 * Start the line detector thread. If it is already running it will be stopped and restarted
	 *
	 * @see #stop()
	*/
	public void start() {
		stop();
		running = true;
		line_detector_thread = new Thread(this);
		line_detector_thread.setDaemon(true);
		line_detector_thread.start();
	}

	/**
	 * Stops the line detector thread.
	 *
	 * @see #start()
	*/
	public void stop() {
		if(running) {
			running = false;
			while(line_detector_thread != null && line_detector_thread.isAlive()) Thread.yield();
		}
	}

	/**
	 * The run method from runnable. Constantly polls the light sensor and looks for line cross events
	 *
	*/
	public void run() {
		while(running) {
			int new_reading = sensor.getNormalizedLightValue();
			if(new_reading < THRESHOLD && previous_reading > THRESHOLD) notifyListener();
			previous_reading = new_reading;
		}
	}

}