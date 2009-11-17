package dinaBOT.navigation;

import lejos.nxt.Motor;
import java.lang.Math;

import dinaBOT.util.Functions;

/**
 * BasicMovement is a simple implementation of the specifications of the {@link Movement} interface. It provides simple movement paradigms for robot movement. This implementation assumes a robot with two wheels that can rotate about itself and who's position is accurately reflected by the associated {@link Odometer}.
 *
 * @author Severin Smith
 * @see Odometer
 * @see Movement
 * @see Navigation
 * @version 2
*/
public class BasicMovement implements Movement {

	/* -- Static Variables -- */

	//Possible states for the movement daemon to be in
	enum Mode { INACTIVE, SUSPENDED, ROTATE_CW, ROTATE_CCW, ADVANCE, GOTO }

	/* -- Instance Variables -- */

	Odometer odometer;

	Motor left_motor, right_motor;
	
	MovementDaemon movement_daemon;
	Thread movement_daemon_thread;
	boolean movement_daemon_running; //Run condition for the movement_daemon_thread

	boolean moving, inter;

	/**
	 * Create a new BasicMovement instance with the given odometer and left and right motors.
	 *
	 * @param odometer the odometer to be used
	 * @param left_motor the robot's left motor
	 * @param right_motor the robot's right motor
	*/
	public BasicMovement(Odometer odometer, Motor left_motor, Motor right_motor) {
		//Set up odometer and motors
		this.odometer = odometer;

		this.left_motor = left_motor;
		this.right_motor = right_motor;

		moving = false; //We aren't moving initally

		//Create movement daemon
		movement_daemon = new MovementDaemon();

		//Start it's thread
		movement_daemon_running = true;

		movement_daemon_thread = new Thread(movement_daemon);
		movement_daemon_thread.setDaemon(true);
		movement_daemon_thread.start();
	}
	
	public void goTo(double x, double y, int speed) {
		goTo(x, y, speed, false);
	}

	public void goTo(double x, double y, int speed, boolean returnImmediately) {
		stop();
		if(speed == 0) return;
		double[] current_position = odometer.getPosition();
		inter = false;
		turnTo(Math.atan2((y-current_position[1]),(x-current_position[0])), 60);
		if(inter) {
			inter = false;
			return;
		}
		movement_daemon.goTo(x, y, speed);
		if(!returnImmediately) while(movement_daemon.isActive()) Thread.yield();
	}

	public void goForward(double distance, int speed) {
		goForward(distance, speed, false);
	}

	public void goForward(double distance, int speed, boolean returnImmediately) {
		stop();
		if(speed == 0 || distance == 0) return; //Avoid silly input
		movement_daemon.goForward(distance, speed);
		if(!returnImmediately) while(movement_daemon.isActive()) Thread.yield();
	}

	public void turn(double angle, int speed) {
		turn(angle, speed, false);
	}

	public void turn(double angle, int speed, boolean immediateReturn) {
		turnTo(angle+odometer.getPosition()[2], speed, immediateReturn);
	}

	public void turnTo(double angle, int speed) {
		turnTo(angle, speed, false);
	}

	public void turnTo(double angle, int speed, boolean returnImmediately) {
		stop();
		inter = false;
		if(speed == 0) return; //Avoid silly input
		movement_daemon.turnTo(angle, speed);
		if(!returnImmediately) while(movement_daemon.isActive()) Thread.yield();
	}

	public void rotate(boolean direction, int speed) {
		stop();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		if(direction) {
			left_motor.backward();
			right_motor.forward();
		} else {
			left_motor.forward();
			right_motor.backward();
		}
	}

	public void forward(int speed) {
		stop();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		left_motor.forward();
		right_motor.forward();
	}

	public void backward(int speed) {
		stop();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		left_motor.backward();
		right_motor.backward();
	}

	public void stop() {
		if(movement_daemon.isActive()) movement_daemon.stop();

		left_motor.stop();
		right_motor.stop();

		moving = false;
		inter = true;
	}

	public void resume() {
		movement_daemon.resume();
	}
	
	public void suspend() {
		movement_daemon.suspend();
	}

	public boolean isMoving() {
		return moving || movement_daemon.isActive();
	}

	public void shutdown() {
		stop();
		movement_daemon_running = false;
		while(movement_daemon_thread.isAlive()) Thread.yield();
	}

	/**
	 * The MovementDaemon actually handles the details of all complex motions such as driveStraight, goForward and turnTo in a seperate thread. This allows theses method to be in either block or non-block.
	 * <p>
	 * The MovementDaemon can either be suspended (Mode.INACTIVE) when it is not in use. In this state the thread yields. Or it can be in one if the implmented other modes such as Mode.ROTATE_CCW, Mode.ROTATE_CW and Mode.ADVANCE in which case the thread is active. Within the BasicMovement class the status of the thread can be polled with isActive()
	*/
	class MovementDaemon implements Runnable {

		//The current mode of the MovementDaemon
		Mode mode, suspended_mode;
		
		int l_mode, r_mode;
		
		//Stored information, used depending on the mode
		double target_distance, target_angle, target_speed;
		double[] initial_position;
		double[] current_position;
		double[] target_position;

		/**
		 * Create MovementDaemon
		 *
		*/
		MovementDaemon() {
			mode = Mode.INACTIVE; //Initially inactive
			suspended_mode = Mode.INACTIVE;
			
			l_mode = 3;
			r_mode = 3;
			
			//Setup the arrays
			initial_position = new double[3];
			current_position = new double[3];
			target_position = new double[3];
		}

		/**
		 * Run method from Runnable. This method will suspend unless some complex movement is in progress
		 *
		*/
		public void run() {
			while(movement_daemon_running) { //While we're not shutting down
				if(mode == Mode.INACTIVE || mode == Mode.SUSPENDED) { //If inactive yield
					Thread.yield();
				} else { //Otherwise execute correct action accoring to current mode
					current_position = odometer.getPosition(); //Update position array
					if(mode == Mode.ADVANCE) { //Advance set distance
						if(target_distance*target_distance < ((current_position[0]-initial_position[0])*(current_position[0]-initial_position[0])+(current_position[1]-initial_position[1])*(current_position[1]-initial_position[1]))) {
							stop();
						}
					} else if(mode == Mode.ROTATE_CCW) { //Rotate set angle counter clockwise
						if((target_angle - current_position[2]) <= 0) { //Until the sign of the relative angle changes
							odometer.enableSnapping(true);
							stop();
						}

					} else if(mode == Mode.ROTATE_CW) { //Rotate set angle clockwise
						if((target_angle - current_position[2]) >= 0) { //Until the sign of the relative angle changes
							odometer.enableSnapping(true);
							stop();
						}
					} else if(mode == Mode.GOTO) { //Go to 
						target_angle = Math.atan2((target_position[1]-current_position[1]),(target_position[0]-current_position[0]));

						//Adjust angle so it's in the range [-pi+current_pos, pi+current_pos]
						//eg within pi of the current positon
						while(target_angle < (current_position[2] - Math.PI)) target_angle += 2*Math.PI;
						while(target_angle > (current_position[2] + Math.PI)) target_angle -= 2*Math.PI;

						double dmax = Math.sqrt((target_position[0]-current_position[0])*(target_position[0]-current_position[0])+(target_position[1]-current_position[1])*(target_position[1]-current_position[1]));
						target_distance = Math.cos(current_position[2]-target_angle)*dmax;

						int base_speed = (int)target_speed;

						left_motor.setSpeed((int)(base_speed+base_speed*2*(current_position[2]-target_angle)));
						right_motor.setSpeed((int)(base_speed-base_speed*2*(current_position[2]-target_angle)));

						if(target_distance < 1) stop();
					}
					Thread.yield(); //Yield for a little while
				}
			}
		}
		
		/**
		 * Initiate movement to a specified x and y location
		 *
		 * @param x the x coordinate to go to (in cm)
		 * @param y the y coordinate to go to (in cm)
		 * @param speed the speed to advance at
		*/
		void goTo(double x, double y, int speed) {
			/* Set permanents */
			target_position[0] = x;
			target_position[1] = y;
			target_speed = speed;
			
			/* Compute initial state */
			current_position = odometer.getPosition();
			
			target_angle = Math.atan2((target_position[1]-current_position[1]),(target_position[0]-current_position[0]));

			//Convert angle modulo 2*pi (angle E [-2*pi, 2*pi]) and store
			target_angle = (target_angle)%(2*Math.PI);

			//Adjust angle so it's in the range [-pi+current_pos, pi+current_pos]
			//eg within pi of the current positon
			while(target_angle < (current_position[2] - Math.PI)) target_angle += 2*Math.PI;
			while(target_angle > (current_position[2] + Math.PI)) target_angle -= 2*Math.PI;

			double dmax = Math.sqrt((target_position[0]-current_position[0])*(target_position[0]-current_position[0])+(target_position[1]-current_position[1])*(target_position[1]-current_position[1]));
			target_distance = Math.cos(current_position[2]-target_angle)*dmax;
		
			int base_speed = (int)target_speed;
	
			left_motor.setSpeed((int)(base_speed+base_speed*2*(current_position[2]-target_angle)));
			right_motor.setSpeed((int)(base_speed-base_speed*2*(current_position[2]-target_angle)));

			left_motor.forward();
			right_motor.forward();
			
			mode = Mode.GOTO;			
		}
		
		/**
		 * Initiate forward movement to a specified distance
		 *
		 * @param distance the distance to advance by (in cm)
		 * @param speed the speed to advance at
		*/
		void goForward(double distance, int speed) {
			//Set motor speed
			left_motor.setSpeed(speed);
			right_motor.setSpeed(speed);

			//Remember start position
			initial_position = odometer.getPosition();

			//Remeber requested distance
			target_distance = distance;

			//Start motors in correct direction 
			if(distance > 0) {
				left_motor.forward();
				right_motor.forward();
			} else {
				left_motor.backward();
				right_motor.backward();
			}

			//Set mode
			mode = Mode.ADVANCE; 
		}

		/**
		 * Initiate forward rotation to an absolute angle
		 *
		 * @param angle the angle to rotate to (in radians)
		 * @param speed the speed to advance at
		*/
		void turnTo(double angle, int speed) {
			odometer.enableSnapping(false);
			//Set motor speed
			left_motor.setSpeed(speed);
			right_motor.setSpeed(speed);

			//Remember start position
			initial_position = odometer.getPosition();

			//Convert angle modulo 2*pi (angle E [-2*pi, 2*pi]) and store
			target_angle = (angle)%(2*Math.PI);

			//Adjust angle so it's in the range [-pi+current_pos, pi+current_pos]
			//eg within pi of the current positon
			while(target_angle < (initial_position[2] - Math.PI)) target_angle += 2*Math.PI;
			while(target_angle > (initial_position[2] + Math.PI)) target_angle -= 2*Math.PI;

			if((target_angle - initial_position[2]) > 0) { //If the realtive angle is positive go counter-clockwise
				left_motor.backward();
				right_motor.forward();
				//And set mode
				mode = Mode.ROTATE_CCW;
			} else { //If the realtive angle is negative go clockwise
				left_motor.forward();
				right_motor.backward();
				//And set mode
				mode = Mode.ROTATE_CW;
			}
		}

		/**
		 * Stop any ongoing movements
		 *
		*/
		void stop() {
			mode = Mode.INACTIVE; //Disable current movement
			//Stop motors
			left_motor.stop();
			right_motor.stop();
		}

		/**
		 * Returns the currect status of the MovementDaemon
		 *
		 * @return true if the MovementDaemon is active, false otherwise
		*/
		boolean isActive() { //Return status
			if(mode == Mode.INACTIVE) return false;
			else return true;
		}
		
		void suspend() {
			l_mode = left_motor.getMode();
			r_mode = right_motor.getMode();
			
			left_motor.stop();
			right_motor.stop();
			
			suspended_mode = mode;
			mode = Mode.SUSPENDED;
		}
		
		void resume() {
			if(l_mode == 1) left_motor.forward();
			else if(l_mode == 2) left_motor.backward();
			if(r_mode == 1) right_motor.forward();
			else if(r_mode == 2) right_motor.backward();
			
			mode = suspended_mode;
		}
	}

}
