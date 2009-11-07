package dinaBOT;

import lejos.nxt.*;

import dinaBOT.navigation.*;
import dinaBOT.mech.*;
import dinaBOT.comm.*;


/**
 * The DinaBOT class is the central class of our project. It ties everything togethere. It <b>is</b> the robot.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTMaster implements MechConstants {
	
	/* Static Variables */
	
	Motor left_motor = Motor.A;
	Motor right_motor = Motor.B;
	
	/* Class Variables */
	
	Odometer odometer;
	Movement movement;
	BTmaster BTconnect;
	
	/**
	 * This is the contructor for the DinaBOT master
	 *
	*/
	public DinaBOTMaster() {
		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicNavigator(odometer, left_motor, right_motor);
		LCD.drawString("Going into constructor", 0, 0);
		Button.waitForPress();
		BTconnect = new BTmaster();
		BTconnect.connect();
	}
	
	/**
	 * This is our move test method. It will be gone soon
	 *
	*/
	public void moveTest() {	
	/*	odometer.setDebug(true);
		odometer.setPosition(new double[] {30.48,30.48, 0}, new boolean[] {true, true, false});
	//	odometer.enableSnapping(false);
	
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
		
	//	for(int i = 0;i < 4*4;i++) {
	//		movement.goForward(-UNIT_TILE, 150);
	//		movement.turnTo(Math.PI, 150);
	//		movement.goForward(-UNIT_TILE, 150);
	//	}
	
		movement.rotate(false, 150);
		
		while(odometer.getPosition()[2] < 2*Math.PI) {
			Thread.yield();
		}
		movement.stop();
		
	//	movement.driveStraight(0, UNIT_TILE*10, 150); */
	}
	
	public void pickupTest() {
		odometer.setPosition(new double[] {30.48,30.48,0}, new boolean[] {true,true, false});
		
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
		}
		if(BTconnect.requestPickup()) {
			LCD.clear();
			LCD.drawString("Success", 0, 0);
		}
			
	}
	
	
	/**
	 * This is where the static main method lies. This is where execution begins.
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
		
		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster(); //Initiate the DinaBOT Master
		dinaBOTmaster.pickupTest(); //Run Test 
		
		while(true); //Never quit
	}
	
}