package dinaBOT.navigation;

import lejos.nxt.Motor;
import java.lang.Math;
import dinaBOT.util.*;

public class BasicMovement implements Movement {

	Odometer odometer;

	Motor left_motor, right_motor;

	double[] position;

	boolean moving, ongoing_movement;
	Thread ongoing_movement_thread;
	double destination_distance, destination_angle;

	public BasicMovement(Odometer odometer, Motor left_motor, Motor right_motor) {
		this.odometer = odometer;

		this.left_motor = left_motor;
		this.right_motor = right_motor;

		moving = false;
		ongoing_movement = false;

		position = new double[3];
	}


	public void goForward(double distance, int speed) {
		goForward(distance, speed, false);
	}

	public synchronized void goForward(double distance, int speed, boolean returnImmediately) {
		if(speed == 0 || distance == 0) { //Avoid silly input
			stop();
			return;
		}

		stop();

		//Update the position array
		position = odometer.getPosition();

		//Set the motor speeds
		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		if(returnImmediately) {
			ongoing_movement = true;
			destination_distance = distance;
			ongoing_movement_thread = new Thread(new Runnable() {
				public void run() {
					//Remeber the initial position
					double initial_x = position[0];
					double initial_y = position[1];

					//If distance is positive, go forward, else go backwards
					if(destination_distance > 0) {
						left_motor.forward();
						right_motor.forward();
					} else {
						left_motor.backward();
						right_motor.backward();
					}

					//While the euclidian distance from the start is less than the requested distance
					while(ongoing_movement && destination_distance*destination_distance > ((position[0]-initial_x)*(position[0]-initial_x)+(position[1]-initial_y)*(position[1]-initial_y))) {
						position = odometer.getPosition(); //Update the position
						Thread.yield(); //And yield
					}

					//The distance was reached, stop the motors
					left_motor.stop();
					right_motor.stop();
					ongoing_movement = false;
					moving = false;
				}
			});

			ongoing_movement_thread.setDaemon(true);
			ongoing_movement_thread.start();
		} else {
			//Remeber the initial position
			double initial_x = position[0];
			double initial_y = position[1];

			//If distance is positive, go forward, else go backwards
			if(distance > 0) {
				left_motor.forward();
				right_motor.forward();
			} else {
				left_motor.backward();
				right_motor.backward();
			}

			//While the euclidian distance from the start is less than the requested distance
			while(distance*distance > ((position[0]-initial_x)*(position[0]-initial_x)+(position[1]-initial_y)*(position[1]-initial_y))) {
				position = odometer.getPosition(); //Update the position
				Thread.yield(); //And yield
			}

			//The distance was reached, stop the motors
			stop();
		}
	}
	
	/**
	 * Rotate a given amount (relative change) at a given speed.
	 *
	 * @param angle the amount to rotate by (in radians)
	 * @param speed the speed to rotate at
	*/
	public void turn(double angle, int speed) {
		
	}

	/**
	 * Rotate a given amount (relative change) at a given speed, return immediately if requested.
	 *
	 * @param angle the amount to rotate by (in radians)
	 * @param speed the speed to rotate at
	 * @param immediateReturn returns immediately if true, blocks otherwise
	*/
	public void turn(double angle, int speed, boolean immediateReturn) {
		
	}

	public void turnTo(double angle, int speed) {
		turnTo(angle, speed, false);
	}

	public synchronized void turnTo(double angle, int speed, boolean returnImmediately) {
		if(speed == 0) { //Avoid silly input
			stop();
			return;
		}

		stop();

		//Set the motor speeds
		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		//Update the position array
		position = odometer.getPosition();

		moving = true;

		if(returnImmediately) {
			ongoing_movement = true;
			destination_angle = angle;
			ongoing_movement_thread = new Thread(new Runnable() {
				public void run() {
					//Convert modulo 2*pi (angle E [-2*pi, 2*pi])
					destination_angle = (destination_angle)%(2*Math.PI);

					//Adjust angle so it's in the range [-pi+current_pos, pi+current_pos]
					//eg within pi of the current positon
					while(destination_angle < (position[2] - Math.PI)) destination_angle += 2*Math.PI;
					while(destination_angle > (position[2] + Math.PI)) destination_angle -= 2*Math.PI;
					//NB this is the same method used in the lejos source code
					//although this code was not copied from there

					if((destination_angle - position[2]) > 0) { //If the realtive angle is positive go counter-clockwise
						left_motor.backward();
						right_motor.forward();
						while(ongoing_movement && (destination_angle - position[2]) >= 0) { //Until the sign of the relative angle changes
							position = odometer.getPosition(); //Update the position
							Thread.yield(); //And yield
						}
					} else { //If the realtive angle is negative go clockwise
						left_motor.forward();
						right_motor.backward();
						while(ongoing_movement && (destination_angle-position[2]) <= 0) { //Until the sign of the relative angle changes
							position = odometer.getPosition(); //Update the position
							Thread.yield(); //And yield
						}
					}

					//The angle was reached, stop the motors
					left_motor.stop();
					right_motor.stop();
					ongoing_movement = false;
					moving = false;
				}
			});
			ongoing_movement_thread.setDaemon(true);
			ongoing_movement_thread.start();
		} else {
			//Convert modulo 2*pi (angle E [-2*pi, 2*pi])
			angle = (angle)%(2*Math.PI);

			//Adjust angle so it's in the range [-pi+current_pos, pi+current_pos]
			//eg within pi of the current positon
			while(angle < (position[2] - Math.PI)) angle += 2*Math.PI;
			while(angle > (position[2] + Math.PI)) angle -= 2*Math.PI;
			//NB this is the same method used in the lejos source code
			//although this code was not copied from there

			if((angle - position[2]) > 0) { //If the realtive angle is positive go counter-clockwise
				left_motor.backward();
				right_motor.forward();
				while((angle - position[2]) >= 0) { //Until the sign of the relative angle changes
					position = odometer.getPosition(); //Update the position
					Thread.yield(); //And yield
				}
			} else { //If the realtive angle is negative go clockwise
				left_motor.forward();
				right_motor.backward();
				while((angle-position[2]) <= 0) { //Until the sign of the relative angle changes
					position = odometer.getPosition(); //Update the position
					Thread.yield(); //And yield
				}
			}

			//The angle was reached, stop the motors
			stop();
		}
	}

	public synchronized void rotate(boolean direction, int speed) {
		stopOngoingMovement();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		if(direction) {
			left_motor.forward();
			right_motor.backward();
		} else {
			left_motor.backward();
			right_motor.forward();
		}
	}

	public synchronized void forward(int speed) {
		stopOngoingMovement();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		left_motor.forward();
		right_motor.forward();
	}

	public synchronized void backward(int speed) {
		stopOngoingMovement();

		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);

		moving = true;

		left_motor.backward();
		right_motor.backward();
	}

	public synchronized void stop() {
		stopOngoingMovement();

		left_motor.stop();
		right_motor.stop();

		moving = false;
	}
	
	public void driveStraight(int direction, double distance, int speed) {
		
		turnTo(Math.PI*direction, speed);
		double latch_corrdinate = 0;
		double variable_coordinate = 0;
		position = odometer.getPosition();
		if(direction%2 == 0) {
			latch_corrdinate = position[1];
			variable_coordinate = position[0];
		} else {
			latch_corrdinate = position[0];
			variable_coordinate = position[1];
		}
		System.out.println(latch_corrdinate);
		System.out.print(variable_coordinate);
		System.out.println("---");
		
		Controller controller = new PIDController((float)latch_corrdinate, 20f, 0f, 0f);
		
		left_motor.setSpeed(speed);
		right_motor.setSpeed(speed);
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
		
		left_motor.forward();
		right_motor.forward();
		
		if(direction == 0) {
			while(position[0]-variable_coordinate < distance) {
				position = odometer.getPosition();
				int correction = (int)controller.output((float)position[1], 1f);
				System.out.println(correction);
				left_motor.setSpeed(speed-correction);
				right_motor.setSpeed(speed+correction);
			}
		} else if(direction == 1) {
			while(position[0]-variable_coordinate < distance) {
				position = odometer.getPosition();
				int correction = (int)controller.output((float)position[0], 1f);
				left_motor.setSpeed(speed+correction);
				right_motor.setSpeed(speed-correction);
			}
		} else if(direction == 2) {
			while(variable_coordinate-position[0] < distance) {
				position = odometer.getPosition();
				int correction = (int)controller.output((float)position[1], 1f);
				left_motor.setSpeed(speed-correction);
				right_motor.setSpeed(speed+correction);
			}
		} else if(direction == 3) {
			while(variable_coordinate-position[0] < distance) {
				position = odometer.getPosition();
				int correction = (int)controller.output((float)position[0], 1f);
				left_motor.setSpeed(speed-correction);
				right_motor.setSpeed(speed+correction);
			}
		}

		left_motor.stop();
		right_motor.stop();
	}

	synchronized void stopOngoingMovement() {
		ongoing_movement = false;
		while(ongoing_movement_thread != null && ongoing_movement_thread.isAlive()) Thread.yield();
	}

	public boolean isMoving() {
		return moving;
	}

	double cos(double angle) {
		return Math.cos(angle);
	}

	double sin(double angle) {
		return Math.sin(angle);
	}

}