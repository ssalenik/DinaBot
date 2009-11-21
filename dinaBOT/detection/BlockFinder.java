package dinaBOT.detection;

import lejos.nxt.*;
import dinaBOT.mech.MechConstants;
import dinaBOT.navigation.*;
import dinaBOT.sensor.*;

/**
 * This class locates blocks, and navigate properly towards them.
 *
 * @author Vinh Phong Buu
*/
public class BlockFinder implements USSensorListener, MechConstants {

	Odometer odometer;
	Movement mover;

	//constants have been moved to MechConstants - stepan

	//Fields
	double angleA;
	double angleB;

	int latest_low_reading_1;
	int latest_low_reading_2;
	int latest_high_reading;
	int lowest_high_reading;

	//True when data sets for low & high are acquired
	boolean data_acquired = false;

	//Current phase of operation
	int phase;

	int blockDistance_A;
	int blockDistance_B;
	
	int minLow;
	int minHigh;
	
	boolean debug;	
	
	//Super experimental constant
	static final int SECOND_COLUMN_MAX = 75;

	/**
	 * Creates a BlockFinder using a supplied {@link dinaBOT.navigation.ArcOdometer odometer}.
	 *
	*/
	public BlockFinder(Odometer odometer, Movement mover) {
		this.odometer = odometer;
		this.mover = mover;
		
		phase = 0;		
		
		USSensor.low_sensor.registerListener(this);
		USSensor.high_sensor.registerListener(this);
	}

	/**
	 * Pivots the robot to perform a {@value #SWEEP_ARC} radians sweep using the ultrasonic sensor
	 * to detect the nearest block. The robot then moves towards it.
	 * In case of a false block detection, the robot simply returns to the orientation it was facing as before it
	 * initiated the sweep.
	 *
	 *@param blockAngle The orientation of the robot when the block was seen during search (in radians).
	 *
	*/
	public boolean sweep(double blockAngle) {
		blockDistance_A = 255;
		blockDistance_B = 255;
		latest_low_reading_1 = 255;
		latest_low_reading_2 = 255;
		latest_high_reading = 255;
		lowest_high_reading = 255;		
		
		double initialOrientation = odometer.getPosition()[2];
		
		angleA = initialOrientation;
		angleB = initialOrientation;

		//Turn to the direction where the block was first seen
		mover.turnTo(blockAngle+SWEEP_ARC/2, SPEED_ROTATE);

		//Clockwise sweep
		phase = 1;
		mover.turn(-SWEEP_ARC, SPEED_ROTATE, false);

		//Counter-clockwise sweep
		phase = 2;
		mover.turn(SWEEP_ARC, SPEED_ROTATE, false);

		//To the bisecting angle !
		// or back to start in case of FAIL
		phase = 0;
		System.out.println("______");
		System.out.println(blockDistance_A);
		System.out.println(blockDistance_B);
		System.out.println(lowest_high_reading);
		if (blockDistance_A < lowest_high_reading-DETECTION_THRESHOLD && blockDistance_B < lowest_high_reading-DETECTION_THRESHOLD && Math.abs(blockDistance_A - blockDistance_B) < 5 && blockDistance_A != 255 && blockDistance_B !=255) {
			mover.turnTo((angleA+angleB)/2, SPEED_ROTATE);
			mover.goForward((blockDistance_A+blockDistance_B)/2, SPEED_MED);
			return true;
		} else {
			//Fail-safe technique for now.
			mover.turnTo(initialOrientation, SPEED_ROTATE);
			return false;
		}
	}

	public void findEdgeA() {
			if(minLow < US_TRUST_THRESHOLD
					&& minHigh - minLow > DETECTION_THRESHOLD
					&& minLow < blockDistance_A
					&& latest_low_reading_2 < SECOND_COLUMN_MAX) {

				blockDistance_A = minLow;
				angleA = odometer.getPosition()[2];
				Sound.buzz();
				if(debug) {
					LCD.drawInt(minLow, 0, 0);
					LCD.drawInt(minHigh, 0, 2);
					LCD.drawInt(latest_low_reading_2, 0, 1);
				}
			}
	}

	public void	findEdgeB() {

			if(minLow < US_TRUST_THRESHOLD
					&& minHigh - minLow > DETECTION_THRESHOLD
					&& minLow < blockDistance_B
					&& latest_low_reading_2 < SECOND_COLUMN_MAX) {

				blockDistance_B = minLow;
				angleB = odometer.getPosition()[2];
				Sound.buzz();
				if(debug) {
					LCD.drawInt(minLow, 0, 3);
					LCD.drawInt(minHigh, 0, 5);
					LCD.drawInt(latest_low_reading_2, 0, 4);
				}
			}
		}

	public void newValues(int[] new_values, USSensor sensor) {
		switch (phase) {
			case 0:
				//Do nothing
				break;

			case 1:
				//Latching A
				if (sensor == USSensor.low_sensor) {
					latest_low_reading_1 = new_values[0];
					latest_low_reading_2 = new_values[1];
					if (data_acquired) {
						data_acquired = false;
					}
				} else if (sensor == USSensor.high_sensor) {
					latest_high_reading = new_values[0];
					if(latest_high_reading < lowest_high_reading) {lowest_high_reading = latest_high_reading;System.out.println(lowest_high_reading);}
					if (!data_acquired) {
						minLow = latest_low_reading_1;
						minHigh = lowest_high_reading;
						findEdgeA();
						data_acquired = true;
					}
				}

				break;

			case 2:
				//Latching B
				if (sensor == USSensor.low_sensor) {
					latest_low_reading_1 = new_values[0];
					latest_low_reading_2 = new_values[1];
					if (data_acquired) {
						data_acquired = false;
					}
				} else if (sensor == USSensor.high_sensor) {
					latest_high_reading = new_values[0];
					if(latest_high_reading < lowest_high_reading) {lowest_high_reading = latest_high_reading;System.out.println(lowest_high_reading);}
					if (!data_acquired) {
						minLow = latest_low_reading_1;
						minHigh = lowest_high_reading;
						findEdgeB();
						data_acquired = true;
					}
				}

				break;
		}
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
