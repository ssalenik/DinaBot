package dinaBOT.detection;

import lejos.nxt.*;

import dinaBOT.mech.MechConstants;
import dinaBOT.navigation.*;
import dinaBOT.sensor.*;
import dinaBOT.comm.*;

/**
 * This class locates blocks, and navigate properly towards them.
 *
 * @author Vinh Phong Buu, Stepan Salenikovich, Severin Smith
 * @see USSensor
 * @see USSensorListener
 * @see MechConstants
 * @see Map
*/
public class BlockFinder implements USSensorListener, MechConstants, CommConstants {

	/* -- Class Variables -- */

	//Super experimental constant
	static final int SECOND_COLUMN_MAX = 75;
	//too close to obstacle constant
	static final int SAFE_DIST = 25;

	/* -- Instance Variable -- */

	Odometer odometer;
	Movement mover;
	Map mapper;
	BTMaster slave_connection;

	//Fields
	double angleA;
	double angleB;

	int[] latest_low_readings;
	int[] latest_high_readings;
	
	int lowest_high_reading;

	//True when data sets for low & high are acquired
	boolean data_acquired = false;

	//Current phase of operation
	int phase;

	int blockDistance_A;
	int blockDistance_B;

	int minLow;
	int minHigh;

	int[][] map;

	//too close boolean
	boolean too_close;

	boolean debug;

	/**
	 * Creates a BlockFinder.
	 *
	 * @param odometer to be used by this BlockFinder
	 * @param mover the movement to use
	 * @param mapper the map to be used
	*/
	public BlockFinder(Odometer odometer, Movement mover, Map mapper, BTMaster slave_connection) {
		this.odometer = odometer;

		this.mover = mover;
		this.mapper = mapper;
		
		this.slave_connection = slave_connection;
		
		latest_low_readings = new int[] {255, 255, 255, 255, 255, 255, 255, 255};
		latest_high_readings = new int[] {255, 255, 255, 255, 255, 255, 255, 255};

		phase = 0;

		USSensor.low_sensor.registerListener(this);
		USSensor.high_sensor.registerListener(this);
	}

	/**
	 * Pivots the robot to perform a {@value dinaBOT.mech.MechConstants#SWEEP_ARC} radians sweep using the ultrasonic sensor
	 * to detect the nearest block. The robot then moves towards it.
	 * In case of a false block detection, the robot simply returns to the orientation it was facing as before it
	 * initiated the sweep.
	 *
	 * @param blockAngle The orientation of the robot when the block was seen during search (in radians).
	 * @return true if the block was found and homed in on, false otherwise
	*/
	public boolean sweep(double blockAngle) {
		//Reset all the variables
		too_close = false;
		
		blockDistance_A = 255;
		blockDistance_B = 255;
		
		latest_low_readings = new int[] {255, 255, 255, 255, 255, 255, 255, 255};
		latest_high_readings = new int[] {255, 255, 255, 255, 255, 255, 255, 255};
		
		lowest_high_reading = 255;

		double initialOrientation = odometer.getPosition()[2];

		angleA = initialOrientation;
		angleB = initialOrientation;

		//Turn past the direction where the block was first seen
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
		
		if(debug) {
			System.out.println("----");
			System.out.println(blockDistance_A);
			System.out.println(blockDistance_B);
			System.out.println(lowest_high_reading);
		}
		
		if (blockDistance_A < lowest_high_reading-DETECTION_THRESHOLD 
				&& blockDistance_B < lowest_high_reading-DETECTION_THRESHOLD 
				&& Math.abs(blockDistance_A - blockDistance_B) < 10
				&& blockDistance_A != 255
				&& blockDistance_B !=255) {
			lowest_high_reading = 255;
		
			double angle = (angleA+angleB)/2;
			double blockDistance = (blockDistance_A+blockDistance_B)/2;

			//check if coord is outside of map
			if(!mapper.checkUSCoord(blockDistance, angle)) return false;

			// go to phase 3; go to pallet phase
			phase = 3;
			
			
			mover.turnTo((angleA+angleB)/2, SPEED_ROTATE);
			double offset = 0;
			if((blockDistance_A+blockDistance_B)/2 < 17) {
				offset = -(20-(blockDistance_A+blockDistance_B)/2);
				mover.goForward(-(20-(blockDistance_A+blockDistance_B)/2), SPEED_MED);
			}
			mapper.stop();
			slave_connection.request(RELEASE);
			mapper.start();
			mover.goForward((blockDistance_A+blockDistance_B)/2-offset, SPEED_MED);
 			phase = 0;

			//gets too close to obstacle while moving
			if(too_close) {
				mapper.stop();
				slave_connection.request(ARMS_UP); //Pickup
				mapper.start();

				return false;
			}
			return true;
		} else {
			//Fail-safe technique for now.
			System.out.println("fail");
			mover.turnTo(initialOrientation, SPEED_ROTATE);
			phase = 0;
			return false;
		}
		
	}

	void findEdgeA() {
		if(minLow < US_TRUST_THRESHOLD
				&& minHigh - minLow > DETECTION_THRESHOLD
				&& minLow < blockDistance_A
				&& latest_low_readings[1] < SECOND_COLUMN_MAX) {

			blockDistance_A = minLow;
			angleA = odometer.getPosition()[2];
			Sound.buzz();
			
		}
	}

	void findEdgeB() {
		if(minLow < US_TRUST_THRESHOLD
				&& minHigh - minLow > DETECTION_THRESHOLD
				&& minLow < blockDistance_B
				&& latest_low_readings[1] < SECOND_COLUMN_MAX) {

			blockDistance_B = minLow;
			angleB = odometer.getPosition()[2];
			Sound.buzz();		

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
					latest_low_readings = new_values;
					if (data_acquired) {
						data_acquired = false;
					}
				} else if (sensor == USSensor.high_sensor) {
					latest_high_readings = new_values;
					if(latest_high_readings[0] < lowest_high_reading) lowest_high_reading = latest_high_readings[0];
					if (!data_acquired) {
						minLow = latest_low_readings[0];
						minHigh = lowest_high_reading;
						findEdgeA();
						data_acquired = true;
					}
				}

				break;

			case 2:
				//Latching B
				if (sensor == USSensor.low_sensor) {
					latest_low_readings = new_values;
					if (data_acquired) {
						data_acquired = false;
					}
				} else if (sensor == USSensor.high_sensor) {
					latest_high_readings = new_values;
					if(latest_high_readings[0] < lowest_high_reading) lowest_high_reading = latest_high_readings[0];
					if (!data_acquired) {
						minLow = latest_low_readings[0];
						minHigh = lowest_high_reading;
						findEdgeB();
						data_acquired = true;
					}
				}

				break;

			case 3:
			if (sensor == USSensor.high_sensor) {
					latest_high_readings = new_values;
					if(latest_high_readings[0] < lowest_high_reading) {
						lowest_high_reading = latest_high_readings[0];
						if((latest_high_readings[1] < 150 && lowest_high_reading < SAFE_DIST) ) { //|| (latest_high_readings[1] >= 150 && lowest_high_reading < SAFE_DIST*2 )) {
							too_close = true;
							mover.stop();
						}
					}
				}

				break;
		}
	}

	/**
	 * Enables and disbles the printout of debug information from the block finder
	 *
	 * @param debug true enables debug information false disables it
	*/
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
