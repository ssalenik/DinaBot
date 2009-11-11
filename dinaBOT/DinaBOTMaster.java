package dinaBOT;

import lejos.nxt.*;
import java.util.Random;
import java.lang.Math;

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
		odometer.setDebug(false);
		odometer.setPosition(new double[] {UNIT_TILE*4, UNIT_TILE*4, 0}, new boolean[] {true, true, false});
		odometer.enableSnapping(true);
		
		//Pause so the user can remove his hand from the robot
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}

		Random rand = new Random();
		
		for(int i = 0;i < 16;i++) {
			double x = odometer.getPosition()[0]/UNIT_TILE;
			double y = odometer.getPosition()[0]/UNIT_TILE;
			int direction = rand.nextInt(4);
			if(x > 5.5) movement.turnTo(2*Math.PI/2, 70);
			else if(x < 2.5) movement.turnTo(0*Math.PI/2, 70);
			else if(y > 5.5) movement.turnTo(1*Math.PI/2, 70);
			else if(y < 2.5) movement.turnTo(3*Math.PI/2, 70);
			else movement.turnTo(direction*Math.PI/2, 70);
			movement.goForward(UNIT_TILE*2, 200);
		}
	}
	
	/**
	 * This is a testing method for brick to brick communication (currently over bluetooth).
	 *
	 */
	public void pickupTest() {
		
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
		LCD.clear();
		LCD.drawString("Trying to pickup ...", 0, 0);
		if(slave_connection.requestPickup()) {
			LCD.clear();
			LCD.drawString("Success ...", 0, 0);
		}
		
	}
	
	public void openAndCloseConnectTest() {
		
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
		}
		
		slave_connection.connect();

		movement.goForward(40,150);
		pickupTest();
		//Button.waitForPress();
		
		slave_connection.disconnect();
				
		//Button.waitForPress();
		movement.goForward(-40,150);
		slave_connection.connect();
		
		slave_connection.openCage();
		
		movement.goForward(40,150);
		
		slave_connection.closeCage();
	}
	
	public void goFetch(int distance) {
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
		}
		
		slave_connection.connect();
		
		movement.goForward(distance,150);
		
		pickupTest();		
		
		movement.goForward(-distance,150);
		
		slave_connection.openCage();
		
		movement.goForward(distance,150);
		
		slave_connection.closeCage();
	
	
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
	//	dinaBOTmaster.goFetch(40);
		dinaBOTmaster.moveTest();
		
		while(true); //Never quit
	}
	
}