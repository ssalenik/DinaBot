package dinaBOT.navigation;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import dinaBOT.mech.MechConstants;
import dinaBOT.sensor.*;

/**
 * This class localizes the robot initially to have it facing approximately at Pi/2.0 radians.
 * @author Vinh Phong Buu, Severin Smith
 *
*/
public class Localization implements MechConstants, USSensorListener {

	/**
	 * The maximum distance at which the localizer will recognize that a wall is seen.
	*/
	protected int WALL_DISTANCE = 25;

	Odometer odometer;
	Movement mover;

	int phase = 0;

	double angleA, angleB, finalAngle;

	/**
	 * Creates a new Localization using a supplied {@link dinaBOT.navigation#ArcOdometer odometer} and with
	 * {@link dinaBOT.sensor#USSensor Ultrasonic Sensors}.
	*/
	public Localization(Odometer odometer, Movement mover) {
		this.odometer = odometer;
		this.mover = mover;

		USSensor.low_sensor.registerListener(this);
	}

	/**
	 * Using falling edge technique, detects the two walls forming the initial
	 * corner where the robot starts and orients the robot at an orientation of about 90
	 * degrees.
	*/
	public void localizeUS() {
		//Reset all fields;
		odometer.enableSnapping(false);
		//Rotate the robot until it sees no wall
		phase = 1;
		mover.rotate(false, SPEED_ROTATE);
		while (mover.isMoving());

		//Keep rotating until the robot sees a wall
		//Then latch the angleA
		phase = 2;
		mover.rotate(false, SPEED_ROTATE);
		while(mover.isMoving());

		// switch direction and wait until it sees no wall
		phase = 1;
		mover.rotate(true, SPEED_ROTATE);
		while (mover.isMoving());

		//Keep rotating until the robot sees a wall
		//Then latch the angleB (this wall is further)
		phase = 3;
		mover.rotate(true, SPEED_ROTATE);
		while(mover.isMoving());

		//angleA is clockwise from angleB, so assume the average of the
		//Angles to the right of angleB is 45.0 degrees past 'north'
		//Find 90 degree orientation approximation
		phase = 0;

		//The robot is always in the South-West corner of the Arena (Bottom-Left).
		//The first wall seen is "south" wall.
		//The second wall seen is "west" wall.
		finalAngle = ((angleA+angleB)/2.0) + (Math.PI/4.0);

		mover.turnTo(finalAngle, SPEED_ROTATE);

		// update the odometer position (this will be a vague estimation)
		odometer.setPosition(new double[] {3.0*UNIT_TILE/4.0, 3.0*UNIT_TILE/4.0, Math.PI/2.0}, new boolean[] {true,true,true});
		odometer.enableSnapping(true);
	}

	/**
	 * Gridsnaps to correct it's orientation once it has USLocalized
	 * Works strictly if approximately on an intersection.
	*/
	public void localizeLight() {
		odometer.setPosition(new double[] {3.0*UNIT_TILE/4.0,3.0*UNIT_TILE/4.0, Math.PI/2.0}, new boolean[] {true, true, true});

		odometer.enableSnapping(true);
		odometer.enableLateralSnapping(false);

		mover.goForward(3.0*UNIT_TILE/4.0, SPEED_SLOW);
		mover.turnTo(Math.PI/2.0, SPEED_ROTATE);
		mover.goForward(-7.0*UNIT_TILE/8.0, SPEED_SLOW);
		//mover.turnTo(Math.PI/2.0, SPEED_ROTATE);
		//mover.goForward(5.0*UNIT_TILE/8.0, SPEED_SLOW);
		//mover.turnTo(Math.PI/2.0, SPEED_ROTATE);
		//mover.goForward(-UNIT_TILE/4.0, SPEED_SLOW);

		mover.goTo(3.0*UNIT_TILE/4.0, UNIT_TILE, SPEED_SLOW);

		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(3.0*UNIT_TILE/4.0, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(-7.0*UNIT_TILE/8.0, SPEED_SLOW);
		//mover.turnTo(0, SPEED_ROTATE);
		//mover.goForward(5.0*UNIT_TILE/8.0, SPEED_SLOW);
		//mover.turnTo(0, SPEED_ROTATE);
		//mover.goForward(-UNIT_TILE/4.0, SPEED_SLOW);

//		mover.goTo(UNIT_TILE, UNIT_TILE, SPEED_SLOW);
//		mover.turnTo(Math.PI/2.0, SPEED_ROTATE);

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
		boolean prev_snap = odometer.isSnapping();

		odometer.enableSnapping(false);
		odometer.enableLateralSnapping(false);

		double[] positon = odometer.getPosition();
		mover.goTo(Math.round(positon[0]/UNIT_TILE)*UNIT_TILE,Math.round(positon[1]/UNIT_TILE)*UNIT_TILE, SPEED_SLOW);

		odometer.enableSnapping(true);

		mover.turnTo(Math.PI/2.0, SPEED_ROTATE);
		mover.goForward(UNIT_TILE/2.0, SPEED_SLOW);
		mover.turnTo(Math.PI/2.0, SPEED_ROTATE);
		mover.goForward(-3.0*UNIT_TILE/4.0, SPEED_SLOW);
		mover.turnTo(Math.PI/2.0, SPEED_ROTATE);
	//	mover.goForward(UNIT_TILE/2.0, SPEED_SLOW);
	//	mover.turnTo(Math.PI/2.0, SPEED_ROTATE);
	//	mover.goForward(-UNIT_TILE/4.0, SPEED_SLOW);
		mover.goForward(UNIT_TILE/4.0, SPEED_SLOW);

		mover.turnTo(0, SPEED_ROTATE);
		double x_offset = UNIT_TILE-odometer.getPosition()[0]%UNIT_TILE;
	
		mover.goForward(UNIT_TILE/2.0, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		mover.goForward(-3.0*UNIT_TILE/4.0, SPEED_SLOW);
		mover.turnTo(0, SPEED_ROTATE);
		//mover.goForward(UNIT_TILE/2.0, SPEED_SLOW);
		//mover.turnTo(0, SPEED_ROTATE);
		//mover.goForward(-UNIT_TILE/4.0, SPEED_SLOW);
		mover.goForward(UNIT_TILE/4.0, SPEED_SLOW);

	//	positon = odometer.getPosition();
	//	mover.goTo(Math.round(positon[0]/UNIT_TILE)*UNIT_TILE,Math.round(positon[1]/UNIT_TILE)*UNIT_TILE, SPEED_SLOW);
	//	mover.turnTo(0, SPEED_ROTATE);

		odometer.enableLateralSnapping(true);
		odometer.enableSnapping(prev_snap);
	}

	public void newValues(int[] new_values, USSensor sensor) {

		switch(phase) {
			case 0:
				//Do nothing
				break;

			case 1:
				//Sweeping along wall
				//Stop when no wall is seen
				if(new_values[0] > WALL_DISTANCE+20) {
					mover.stop();
					Sound.twoBeeps();
				}
				break;

			case 2:
				//Latch first Wall
				//Stop when the angle is latched
				if(new_values[0] < WALL_DISTANCE) {
					phase = 0;
					angleA = odometer.getPosition()[2];
					try {
						Sound.playTone(200, 100,80);
					} catch(Exception e) {

					}

					mover.stop();
				}
				break;

			case 3:
				//Latch second Wall
				//Stop when it is latched
				if(new_values[0] < WALL_DISTANCE) {
					phase = 0;
					angleB = odometer.getPosition()[2];
					try {
						Sound.playTone(200, 100,80);
					} catch(Exception e) {}

					mover.stop();
				}
				break;
		}
	}

}
