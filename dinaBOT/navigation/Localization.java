package dinaBOT.navigation;

import dinaBOT.mech.MechConstants;
import dinaBOT.sensor.*;

public class Localization implements MechConstants, USSensorListener{

	public Odometer odometer;
	public Movement mover;
	public int phase = 0;
	double angleA, angleB, finalAngle;
	double[] position;
	
	/**
	 * The maximum distance at which the localizer will recongize that a wall is seen.
	 */
	protected int WALL_DISTANCE = 30;
	
	/**
	 * Creates a new Localization using a supplied {@link dinaBOT.navigation#ArcOdometer odometer} and with
	 * {@link dinaBOT.sensor#USSensor Ultrasonic Sensors}.
	 */
	public Localization(Odometer odometer, Movement mover) {
		this.odometer = odometer;
		this.mover = mover;
		USSensor.high_sensor.registerListener(this);
		USSensor.low_sensor.registerListener(this);
	}
	
	/**
	 * Using falling edge technique, detects the two walls forming the initial
	 * corner where the robot starts and orients the robot at an orientation of about 90
	 * degrees.
	 */
	public void Localize() {

		// rotate the robot until it sees no wall
		phase =1;
		mover.rotate(false, SPEED_ROTATE);
		while (mover.isMoving());
		
		// keep rotating until the robot sees a wall, then latch the angle
		phase =2;
		mover.rotate(false, SPEED_ROTATE);
		while(mover.isMoving());
		
		// switch direction and wait until it sees no wall
		phase =1;
		mover.rotate(true, SPEED_ROTATE);
		while (mover.isMoving());

		// keep rotating until the robot sees a wall, then latch the angle (this wall is further)
		phase =3;
		mover.rotate(true, SPEED_ROTATE);
		while(mover.isMoving());
		
		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'
		//Find 90 degree orientation approximation
		phase =0;
		if (angleA > angleB){
			//The first wall seen is "south" wall.
			//The second wall seen is "west" wall.
			finalAngle = ((angleA+angleB)/2) + 225; 
		} else {
			//The first wall seen is "east" wall.
			//The second wall seen is "south" wall.
			finalAngle = ((angleA+angleB)/2) - 45;
		}

		mover.turnTo (finalAngle, SPEED_ROTATE);

		// update the odometer position (this will be a vague estimation)
		odometer.setPosition(new double[] {0,0,Math.PI/2}, new boolean[] {true,true,true});
	}

	@Override
	public void newValues(int[] new_values, USSensor sensor) {
		
		switch (phase) {
		
		case 0:
			//Do nothing 
			break;
			
		case 1:
			//See no Wall
			if (sensor == USSensor.low_sensor && new_values[0] > WALL_DISTANCE+5) {
				mover.stop();
			}
			
		case 2:
			//Latch first Wall
			if (sensor == USSensor.low_sensor && new_values[0] < WALL_DISTANCE) {
				angleA = odometer.getPosition()[2];
				mover.stop();
			}
			break;
		
		case 3:
			//Latch second Wall
			if (sensor == USSensor.low_sensor && new_values[0] < WALL_DISTANCE) {
				angleB = odometer.getPosition()[2];
				mover.stop();
			}
			break;
			
		}
		
	}
	
	
}
