package dinaBOT;

import lejos.nxt.*;

import dinaBOT.navigation.*;
import dinaBOT.mech.*;
import dinaBOT.comm.*;


/**
 * The DinaBOTMaster is the main class the master brick. It <b>is</b> the robot. It contains the main() for the master.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTMaster implements MechConstants {

	/* -- Static Variables -- */

	Motor left_motor = Motor.A;
	Motor right_motor = Motor.B;

	/* -- Instance Variables -- */

	Odometer odometer;
	Movement movement;

	BTMaster slave_connection;

	/**
	 * This is the contructor for the DinaBOT master
	 *
	*/
	public DinaBOTMaster() {
		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicMovement(odometer, left_motor, right_motor);

		slave_connection = new BTMaster();
	}

	/**
	 * This is a testing method for odometry, navigation and movement
	 *
	*/
	public void moveTest() {
		//Configure your odometry
		odometer.setDebug(true);
		odometer.setPosition(new double[] {30.48,30.48, 0}, new boolean[] {true, true, false});
		odometer.enableSnapping(false);

		//Pause so the user can remove his hand from the robot
		try {
			Thread.sleep(1000);
		} catch(Exception e) {

		}

		//Perform various tests
		movement.rotate(false, 150);

		while(odometer.getPosition()[2] < 2*Math.PI) {
			Thread.yield();
		}
		movement.stop();

	//	movement.driveStraight(0, UNIT_TILE*10, 150);
	}

	/**
	 * This is a testing method for brick to brick communication (currently over bluetooth).
	 *
	*/
	public void pickupTest() {
		//Set up connection
		System.out.println("Trying to connect ...");
		slave_connection.connect();

		try {
			Thread.sleep(1000);
		} catch(Exception e) {

		}

		System.out.println("Trying to pickup ...");
		if(slave_connection.requestPickup()) System.out.println("Success ...");

	}


	/**
	 * This is where the static main method lies. This is where execution begins for the master brick
	 *
	 * @param args This is the command line args, this is irrelevent in the NXT
	*/
	public static void main(String[] args) {
		//Add a convenient quit button
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				System.exit(0);
			} 

			public void buttonReleased(Button b) {
				System.exit(0);
			}
		});

		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster(); //Instantiate the DinaBOT Master
		//Run some tests
		//dinaBOTmaster.moveTest();
		dinaBOTmaster.pickupTest();

		while(true); //Never quit
	}

}