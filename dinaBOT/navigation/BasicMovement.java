package dinaBOT.navigation;

import lejos.nxt.Motor;
import java.lang.Math;

public class BasicMovement implements Movement {

	enum Mode { INACTIVE, ROTATE_CW, ROTATE_CCW, ADVANCE }

	Odometer odometer;

	Motor left_motor, right_motor;

	MovementDaemon movement_daemon;
	Thread movement_daemon_thread;
	boolean movement_daemon_running;
	
	boolean moving;

	public BasicMovement(Odometer odometer, Motor left_motor, Motor right_motor) {
		this.odometer = odometer;

		this.left_motor = left_motor;
		this.right_motor = right_motor;
		
		moving = false;
		
		movement_daemon_running = true;
		movement_daemon = new MovementDaemon();
		movement_daemon_thread = new Thread(movement_daemon);
		movement_daemon_thread.setDaemon(true);
		movement_daemon_thread.start();
	}

	public synchronized void driveStraight(int direction, double distance, int speed) {
		System.out.println("NOT IMPLEMENTED");
	}

	public void goForward(double distance, int speed) {
		goForward(distance, speed, false);
	}

	public synchronized void goForward(double distance, int speed, boolean returnImmediately) {
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

	public synchronized void turnTo(double angle, int speed, boolean returnImmediately) {
		stop();
		if(speed == 0) return; //Avoid silly input
		movement_daemon.turnTo(angle, speed);
		if(!returnImmediately) while(movement_daemon.isActive()) Thread.yield();
	}

	public synchronized void rotate(boolean direction, int speed) {
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

	public synchronized void forward(int speed) {
		stop();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		left_motor.forward();
		right_motor.forward();
	}

	public synchronized void backward(int speed) {
		stop();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		left_motor.backward();
		right_motor.backward();
	}

	public synchronized void stop() {
		if(movement_daemon.isActive()) movement_daemon.stop();
		
		left_motor.stop();
		right_motor.stop();

		moving = false;
	}
	

	public boolean isMoving() {
		return moving || movement_daemon.isActive();
	}

	class MovementDaemon implements Runnable {
		
		Mode mode;
		double target_distance, target_angle;
		double[] initial_position;
		double[] current_position;
		
		MovementDaemon() {
			mode = Mode.INACTIVE;
			initial_position = new double[3];
			current_position = new double[3];
		}
		
		public void run() {
			while(movement_daemon_running) {
				if(mode == Mode.INACTIVE) {
					Thread.yield();
				} else {
					if(mode == Mode.ADVANCE) {
						if(target_distance*target_distance < ((current_position[0]-initial_position[0])*(current_position[0]-initial_position[0])+(current_position[1]-initial_position[1])*(current_position[1]-initial_position[1]))) {
							stop();
						}
					} else if(mode == Mode.ROTATE_CCW) {
						if((target_angle - current_position[2]) <= 0) { //Until the sign of the relative angle changes
							stop();
						}
						
					} else if(mode == Mode.ROTATE_CW) {
						if((target_angle - current_position[2]) >= 0) { //Until the sign of the relative angle changes
							stop();
						}
					}
					current_position = odometer.getPosition();
					Thread.yield();
				}
			}
		}
		
		void goForward(double distance, int speed) {
			left_motor.setSpeed(speed);
			right_motor.setSpeed(speed);
			
			initial_position = odometer.getPosition();

			target_distance = distance;
			
			if(distance > 0) {
				left_motor.forward();
				right_motor.forward();
			} else {
				left_motor.backward();
				right_motor.backward();
			}
			
			mode = Mode.ADVANCE;
		}
		
		void turnTo(double angle, int speed) {
			left_motor.setSpeed(speed);
			right_motor.setSpeed(speed);

			initial_position = odometer.getPosition();

			//Convert modulo 2*pi (angle E [-2*pi, 2*pi])
			target_angle = (angle)%(2*Math.PI);

			//Adjust angle so it's in the range [-pi+current_pos, pi+current_pos]
			//eg within pi of the current positon
			while(target_angle < (initial_position[2] - Math.PI)) target_angle += 2*Math.PI;
			while(target_angle > (initial_position[2] + Math.PI)) target_angle -= 2*Math.PI;
			//NB this is the same method used in the lejos source code
			//although this code was not copied from there

			if((target_angle - initial_position[2]) > 0) { //If the realtive angle is positive go counter-clockwise
				left_motor.backward();
				right_motor.forward();
				mode = Mode.ROTATE_CCW;
			} else { //If the realtive angle is negative go clockwise
				left_motor.forward();
				right_motor.backward();
				mode = Mode.ROTATE_CW;
			}
		}
	
		void stop() {
			mode = Mode.INACTIVE;
			left_motor.stop();
			right_motor.stop();
		}
		
		boolean isActive() {
			if(mode == Mode.INACTIVE) return false;
			else return true;
		}
	}

}