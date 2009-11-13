package BlockAquisition;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import dinaBOT.mech.MechConstants;
import dinaBOT.navigation.*;
import dinaBOT.sensor.*;

/**
 * This class contains all methods required to navigate an area, locate blocks, and navigate properly towards them.
 *
 */
public class BlockFinder implements USSensorListener{

	//Robot Constants
	private ArcOdometer odometer;
	private BasicMovement mover;
	protected static Motor LeftWheel = Motor.A;
	protected static Motor RightWheel = Motor.B;
	protected USSensor lowUS = USSensor.low_sensor;
	protected USSensor highUS = USSensor.high_sensor;

	/**
	 * Radius of a robot wheel = {@value}
	 * @see MechConstants#WHEEL_RADIUS
	 */
	public final double WHEEL_RADIUS = MechConstants.WHEEL_RADIUS;
	/**
	 * Wheel base of the robot = {@value}
	 * @see MechConstants#WHEEL_BASE
	 */
	public final double BOT_BASE = MechConstants.WHEEL_BASE;
	/**
	 * Speed used when turning = {@value}
	 */
	public final int TURN_SPEED = 50;
	/**
	 * Speed used when moving linearly = {@value}
	 */
	public final int MOVE_SPEED = 120;
	/**
	 * Maximum distance at which a block may be located in order to be detected = {@value}
	 */
	public final int MAX_BLOCK_DISTANCE = 50;

	/**
	 * Size in degrees of the arc the robot should sweep = {@value}
	 */
	public final double SWEEP_ARC = Math.PI;
	/**
	 * Maximum allowed difference between high and low sensor values to assume both are reading the same object
	 */
	public final int MAX_BANDWIDTH = 5;
	
	//Fields
	double angleA;
	double angleB;
	int[] low_Readings;
	int[] high_Readings;

	/**
	 * Creates a BlockFinder using a supplied {@link dinaBOT.navigation.ArcOdometer odometer}.
	 * 
	 */
	public BlockFinder(ArcOdometer odometer) {
		this.odometer = odometer;
		this.mover = new BasicMovement (odometer, LeftWheel, RightWheel);
		lowUS.registerListener(this);
		highUS.registerListener(this);
	}

	/**
	 *Pivots the robot to perform a {@value #SWEEP_ARC} radians sweep using the ultrasonic sensor
	 *to detect the nearest block.  The robot then moves towards it.
	 *In case of a false block detection, the robot simply returns to the orientation it was facing as before it
	 *initiated the sweep.
	 *
	 *
	 *@param blockAngle The orientation of the robot when the block was seen 
	 *during search (in radians)
	 *
	 */
	public void sweep(double blockAngle) {

		double initialOrientation = odometer.getPosition()[2];
		LeftWheel.setSpeed(TURN_SPEED);
		RightWheel.setSpeed(TURN_SPEED);
		angleA = initialOrientation;
		angleB = initialOrientation;
		int blockDistance_A = 255;
		int blockDistance_B = 255;
		int minLow, minHigh;

		//Turn to the direction where the block was first seen
		mover.turnTo(blockAngle+SWEEP_ARC/2, TURN_SPEED);
		Button.waitForPress();
		
		//Look left to right
		//Or try later: find sum of convsecutive "short" distances in the left
		mover.turn(-SWEEP_ARC, TURN_SPEED, true);
		while (mover.isMoving()) {
			minLow = low_Readings[0];
			minHigh = high_Readings[0];

			if(minLow < MAX_BLOCK_DISTANCE 
					&& Math.abs(minLow - minHigh) > MAX_BANDWIDTH
					&& minLow < blockDistance_A) {

				blockDistance_A = minLow;
				angleA = odometer.getPosition()[2];
				//mover.stop();
				Sound.twoBeeps();
			}
			
		}
		
		Button.waitForPress();

		//Look right to left
		//or later, Try find sum of consecutive "short" distances in the right
		//+ region where short distances are seen
		//
		mover.turn(SWEEP_ARC, TURN_SPEED, true);
		while (mover.isMoving()) {
			minLow = low_Readings[0];
			minHigh = high_Readings[0];

			if( minLow < MAX_BLOCK_DISTANCE 
					&& Math.abs(minLow - minHigh) > MAX_BANDWIDTH
					&& minLow < blockDistance_B) {

				blockDistance_B = minLow;
				angleB = odometer.getPosition()[2];
				//mover.stop();
				Sound.twoBeeps();
			}
				
		}
		
		Button.waitForPress();
		
		//Duplicate angle if either is missed
		//TODO: Maybe try to have a "resweep" to retry the latching or simply "surrender" and retry
		// at a different location
		if (angleA == 0 && angleB != 0) {
			angleA = angleB;
			blockDistance_A = 0;
			blockDistance_B = 0;
		} else if (angleA != 0 && angleB == 0) {
			angleB = angleA;
			blockDistance_A = 0;
			blockDistance_B = 0;
		}

		//To the bisecting angle !
		//IF the same pallet was seen in both cases
		if (Math.abs(blockDistance_A - blockDistance_B) < 5 && blockDistance_A != 255 && blockDistance_B !=255) {
			mover.turnTo((angleA+angleB)/2, TURN_SPEED);
			mover.goForward( (blockDistance_A+blockDistance_B)/2, MOVE_SPEED);
		} else {
			//Fail-safe technique for now.
			mover.turnTo(initialOrientation, TURN_SPEED);
		}

	}

	public void newValues(int[] new_values, Position position) {
		if (position == USSensorListener.Position.LOW) {
			this.low_Readings = new_values;
		} else if (position == USSensorListener.Position.HIGH) {
			this.high_Readings = new_values;
		}
	}


}
