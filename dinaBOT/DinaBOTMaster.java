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
		odometer.setDebug(true);
		odometer.setPosition(new double[] {UNIT_TILE,7*UNIT_TILE, 0}, new boolean[] {true, true, false});
		odometer.enableSnapping(true);
		//Pause so the user can remove his hand from the robot
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
		
		}
	
		while(true) {
			
			//int[] ex = {1,2,3,4,5,6,7,8,9,10,11,11,11,11,11,11,11,10,9,8,7,6,5,5,5,5,5,4,3,2,1,1,1}; //23
			
			//int[] why = {7,7,7,7,7,7,7,7,7,7,7,6,5,4,3,2,1,1,1,1,1,1,1,2,3,4,5,5,5,5,5,6,7};//22
			
			int[] ex = {11,11,5,5,1,1};
			int[] why = {7,1,1,5,5,7};
			
			for(int i = 0;i < ex.length;i++) {
				goXY(ex[i],why[i]);
				
			}

		}
	}
	
	public void goXY(int x, int y) {
		double[] position = odometer.getPosition();
		double distance = Math.sqrt((x*UNIT_TILE-position[0])*(x*UNIT_TILE-position[0])+(y*UNIT_TILE-position[1])*(y*UNIT_TILE-position[1]));
		double angle = Math.atan2((y*UNIT_TILE-position[1]),(x*UNIT_TILE-position[0]));
		while(distance > 1) {
			odometer.enableSnapping(false);
			movement.turnTo(angle,100);
			odometer.enableSnapping(true);
			
			
			if(distance < 5) movement.goForward(distance, 200);
			else movement.goForward(5, 200);
			
			position = odometer.getPosition();
			distance = Math.sqrt((x*UNIT_TILE-position[0])*(x*UNIT_TILE-position[0])+(y*UNIT_TILE-position[1])*(y*UNIT_TILE-position[1]));
			angle = Math.atan2((y*UNIT_TILE-position[1]),(x*UNIT_TILE-position[0]));
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