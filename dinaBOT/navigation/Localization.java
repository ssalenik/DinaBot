package dinaBOT.navigation;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import dinaBOT.mech.MechConstants;
import dinaBOT.sensor.*;

/**
 * This class localizes the robot initially to have it facing approximately at Pi/2 radians.
 * @author Vinh Phong Buu, Severin Smith
 *
 */
public class Localization implements MechConstants, USSensorListener {

	public Odometer odometer;
	public Movement mover;
	public int phase = 0;
	double angleA, angleB, finalAngle;
	double[] position;
	boolean angleALatched = false, angleBLatched = false;
	
	int last_values[] = new int[6];
	int median = 3;
	int idx = 0;

	/**
	 * The maximum distance at which the localizer will recongize that a wall is seen.
	 */
	protected int WALL_DISTANCE = 25;

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
	public void localizeUS() {
		//Reset all fields;
		last_values = new int[6];
		angleALatched = false;
		angleBLatched = false;
		
		// rotate the robot until it sees no wall
		mover.rotate(false, SPEED_ROTATE);
		phase =1;
		while (mover.isMoving());

		// keep rotating until the robot sees a wall
		//then latch the angleA
		phase =2;
		mover.rotate(false, SPEED_ROTATE);
		while(!angleALatched && mover.isMoving());

		// switch direction and wait until it sees no wall
		phase = 0;
		mover.turn(Math.PI/2, SPEED_ROTATE);
		phase =1;
		mover.rotate(true, SPEED_ROTATE);
		while (mover.isMoving());

		// keep rotating until the robot sees a wall
		//then latch the angleB (this wall is further)
		phase =3;
		mover.rotate(true, SPEED_ROTATE);
		while(!angleBLatched && mover.isMoving());

		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'
		//Find 90 degree orientation approximation
		phase = 0;
		if (angleA < angleB) {
			//The first wall seen is "south" wall.
			//The second wall seen is "west" wall.
			finalAngle = ((angleA+angleB)/2) + (Math.PI/4);
		} 
		/*else {
			//The first wall seen is "east" wall.
			//The second wall seen is "south" wall.
			finalAngle = ((angleA+angleB)/2) - (Math.PI/4);
		}*/

		mover.turnTo (finalAngle, SPEED_ROTATE);

		LCD.drawInt((int) Math.toDegrees(odometer.getPosition()[2]), 0, 3);
		// update the odometer position (this will be a vague estimation)
		odometer.setPosition(new double[] {3*UNIT_TILE/4,3*UNIT_TILE/4,Math.PI/2}, new boolean[] {true,true,true});
	}

	/**
	 * Gridsnaps to correct it's orientation once it has USLocalized
	 * Works strictly if approximately on an intersection.
	 */
	public void localizeLight() {
		odometer.setPosition(new double[] {3*UNIT_TILE/4,3*UNIT_TILE/4, Math.PI/2}, new boolean[] {true, true, true});
		odometer.enableSnapping(true);
		odometer.enableLateralSnapping(false);

		mover.goForward(UNIT_TILE, SPEED_SLOW);
		mover.goForward(-UNIT_TILE, SPEED_SLOW);
		mover.goForward(3*UNIT_TILE/4, SPEED_SLOW);
		mover.turnTo(Math.PI/2, SPEED_ROTATE);
		mover.goForward(-3*UNIT_TILE/4, SPEED_SLOW);
		mover.turnTo(Math.PI/2, SPEED_ROTATE);
		mover.goForward(UNIT_TILE/2, SPEED_SLOW);
		mover.turnTo(Math.PI/2, SPEED_ROTATE);
		mover.goForward(-3*UNIT_TILE/8, SPEED_SLOW);
		mover.turnTo(Math.PI/2, SPEED_ROTATE);
		mover.goForward(UNIT_TILE/4, SPEED_SLOW);
		mover.turnTo(Math.PI/2, SPEED_ROTATE);
		mover.goForward(-UNIT_TILE/4, SPEED_SLOW);

		mover.goTo(3*UNIT_TILE/4, UNIT_TILE, SPEED_SLOW);

		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(UNIT_TILE, SPEED_SLOW);
		mover.goForward(-UNIT_TILE, SPEED_SLOW);
		mover.goForward(3*UNIT_TILE/4, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(-3*UNIT_TILE/4, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(UNIT_TILE/2, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(-3*UNIT_TILE/8, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(UNIT_TILE/4, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(-UNIT_TILE/4, SPEED_SLOW);

		mover.goTo(UNIT_TILE, UNIT_TILE, SPEED_SLOW);
		odometer.enableLateralSnapping(true);
	}

	/**
	 * Call to USLocalize and LightLocalize simply.
	 */
	public void localize() {
		this.localizeUS();
		this.localizeLight();
	}

	/**
	 * Performs a quick localization routine using only the light sensors to fix the orientation
	 * at any node on the grid.
	 */
	public void localizeAnywhere() {
		odometer.enableSnapping(true);
		odometer.enableLateralSnapping(false);

		mover.turnTo(Math.PI, SPEED_ROTATE);
		mover.goForward(3/4*UNIT_TILE, SPEED_SLOW);
		mover.goForward(-3/4*UNIT_TILE, SPEED_SLOW);
		mover.turnTo(Math.PI/2, SPEED_ROTATE);
		mover.goForward(1/4*UNIT_TILE, SPEED_SLOW);
		mover.turnTo(Math.PI/2, SPEED_ROTATE);
		mover.goForward(-1/4*UNIT_TILE, SPEED_SLOW);

		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(3/4*UNIT_TILE, SPEED_SLOW);
		mover.goForward(-3/4*UNIT_TILE, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(1/4*UNIT_TILE, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(-1/4*UNIT_TILE, SPEED_SLOW);

		mover.turnTo(Math.PI/2, SPEED_ROTATE);
	}

	public void newValues(int[] new_values, USSensor sensor) {

		switch (phase) {

		case 0:
			//Do nothing
			break;

		case 1:
			//Sweeping along wall
			//Stop when no wall is seen
			
			if (sensor == USSensor.low_sensor) {
				last_values[idx%6] = new_values[0];
				idx++;
				if (last_values[median] > WALL_DISTANCE+20) {
					mover.stop();
					Sound.twoBeeps();
				}
			}
			break;

		case 2:
			//Latch first Wall
			//Stop when the angle is latched
			if (sensor == USSensor.low_sensor && new_values[0] < WALL_DISTANCE && !angleALatched) {
				angleA = odometer.getPosition()[2];
				angleALatched = true;
				try {
					Sound.playTone(1200, 200);
				} catch (Exception e) {}
				mover.stop();
				LCD.drawInt((int)Math.toDegrees(angleA), 0, 0);
			}
			break;

		case 3:
			//Latch second Wall
			//Stop when it is latched
			if (sensor == USSensor.low_sensor && new_values[0] < WALL_DISTANCE && !angleBLatched) {
				angleB = odometer.getPosition()[2];
				angleBLatched = true;
				try {
					Sound.playTone(1200, 200);
				} catch (Exception e) {}
				mover.stop();
				LCD.drawInt((int)Math.toDegrees(angleB), 0, 1);
			}
			break;

		}

	}


}
