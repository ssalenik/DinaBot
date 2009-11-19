package dinaBOT;

import lejos.nxt.*;

import java.util.Random;
import java.lang.Math;

import dinaBOT.navigation.*;
import dinaBOT.mech.*;
import dinaBOT.comm.*;
import dinaBOT.detection.*;
import dinaBOT.util.*;

/**
 * The DinaBOTMaster is the main class the master brick. It <b>is</b> the robot. It contains the main() for the master.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTMaster implements MechConstants, CommConstants {

	/* -- Static Variables --*/

	Motor left_motor = Motor.A;
	Motor right_motor = Motor.B;

	/* -- Instance Variables --*/

	Odometer odometer;
	Movement movement;

	BTMaster slave_connection;
	
	Localization localization;
	DropOff dropOff;
	
	int dropOffX = 0, dropOffY = 0; //Variables that are input at runtime to indicate where the drop-off point is

	/**
	 * This is the contructor for the DinaBOT master
	 *
	*/
	
	//Odometer odometer, Movement mover, BTmaster slave_connection, int dropOffX, int dropOffY
	public DinaBOTMaster() {
		
		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicMovement(odometer, left_motor, right_motor);
		slave_connection = new BTMaster();
		
		localization = new Localization(odometer, movement);
		dropOff = new DropOff(odometer, movement, slave_connection, dropOffX, dropOffY);
		
	}

	/**
	 * This is demo method for our professor meeting on November 18th. It will ask for the input offsets for how much it will displace the pellet when
	 * it picks it up. It then does a 360 sweep around it to find the styrofoam pellet. When it finds it picks it up and displaces it for a certain offset
	 * value.
	*/
	public void milestoneDemo() {
		//User Input
		double offsetX = 0, offsetY = 0;
		boolean enterPressed = false;

		//Sweeping
		double SWEEP_OFFSET = Math.PI/2;
		boolean foundBlock = false;

		BlockFinder blockFind = new BlockFinder(odometer, movement);

		/* User Input*/

		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(offsetX + " " + offsetY, 0,0);
			int buttonID = Button.waitForPress();
			switch(buttonID) {
				case Button.ID_LEFT:
					offsetX--;
					break;
				case Button.ID_RIGHT:
					offsetX++;
					break;
				case Button.ID_ENTER:
					enterPressed = true;
					break;
			}
		}

		enterPressed = false;

		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(offsetX + " " + offsetY,0,0);
			int buttonID = Button.waitForPress();
			switch (buttonID) {
				case Button.ID_LEFT:
					offsetY--;
					break;
				case Button.ID_RIGHT:
					offsetY++;
					break;
				case Button.ID_ENTER:
					enterPressed = true;
					break;
			}
		}

		try {
			Thread.sleep(2000);
			LCD.drawString("Loading main program...", 0,0);
		} catch(Exception e) {

		}

		//Continuously sweep for block
		while(!foundBlock) {
			foundBlock = blockFind.sweep(odometer.getPosition()[2]);
			if(!foundBlock) movement.turn(SWEEP_OFFSET, SPEED_ROTATE);
		}

		//Once pellet is found
		LCD.clear();
		LCD.drawString("Pellet found!",0,0);

		//Align + Pickup
		alignBrick();

		if(slave_connection.request(PICKUP)) {
			LCD.clear();
			LCD.drawString("Success ...", 0, 0);
		}


		odometer.setPosition(new double[] {0,0,0}, new boolean[] {true, true, true});

		Button.waitForPress();

		movement.goTo(offsetX + BLOCK_DISTANCE, offsetY, SPEED_MED);

		slave_connection.request(OPEN_CAGE);

		movement.goForward(30, SPEED_MED);

		slave_connection.request(CLOSE_CAGE);
	}

	/**
	 * This is a testing method for block alignment using brick to brick communication (currently over bluetooth).
	 *
	*/
	public void alignBrick() {

		double forward_distance = 5;

		movement.goForward(forward_distance, SPEED_MED);

		movement.turn(Math.PI/3, SPEED_ROTATE);

		movement.goForward(forward_distance, SPEED_MED);

		if(slave_connection.request(HOLD)) {
			LCD.clear();
			LCD.drawString("Success ...", 0, 0);
		}

		movement.goForward(-forward_distance, SPEED_MED);

		movement.turn(-Math.PI/3, SPEED_ROTATE);

		for(int i = 0;i < 2;i++) {
			if(slave_connection.request(HOLD)) {
				LCD.clear();
				LCD.drawString("Success ...", 0, 0);
			}

			movement.goForward(-forward_distance, SPEED_MED);

			if(slave_connection.request(RELEASE)) {
				LCD.clear();
				LCD.drawString("Success ...", 0, 0);
			}

			movement.goForward(forward_distance, SPEED_MED);
		}
	}

// stepan's pathing and mapping test WORKS!!
	public void pathTest() {
		Map mapper = new Map(odometer, 12, 45, UNIT_TILE);
		Pathing pather = new ManhattanPather(mapper, movement);
		Navigator navigator = new Navigator(pather, movement, mapper, odometer);

		odometer.enableSnapping(true);
		odometer.setDebug(false);

		//Pause so the user can remove his hand from the robot
		try {
			Thread.sleep(1000);
		} catch(Exception e) {}

		// go to 2,3
		navigator.goTo(3*UNIT_TILE, 3*UNIT_TILE, false);
		


		// go back to 0,0
		navigator.goTo(0.0,0.0, false);
	}
	
	// stepan's pathing and mapping AND block detection and pickup test
	public void pathPickupTest() {
		Map mapper = new Map(odometer, 12, 45, UNIT_TILE);
		Pathing pather = new ManhattanPather(mapper, movement);
		Navigator navigator = new Navigator(pather, movement, mapper, odometer);
		BlockFinder blockFind = new BlockFinder(odometer, movement);
		
		boolean foundBlock = false;
		int done;
		

		odometer.enableSnapping(true);
		odometer.setDebug(false);

		//Pause so the user can remove his hand from the robot
		try {
			Thread.sleep(1000);
		} catch(Exception e) {}

		// go to 2,2
		done = navigator.goTo(2*UNIT_TILE, 2*UNIT_TILE, false);
		if ( done == 1) {
			foundBlock = blockFind.sweep(odometer.getPosition()[2]);
			
			if(foundBlock) {
				//Align + Pickup
				alignBrick();

				slave_connection.request(PICKUP);
			}
		} else if (done == 0) {

			// go back to 0,0
			navigator.goTo(0.0,0.0, false);
		}
	}
	
	public void connect() {
		while(!slave_connection.connect());
	}

	public void moveTest() {
		odometer.setDebug(true);
		odometer.enableSnapping(true);
		odometer.setPosition(new double[] {UNIT_TILE, UNIT_TILE, 0}, new boolean[] {true, true, true});
		while(true) {
			movement.goTo(UNIT_TILE*3, UNIT_TILE, 150);
			movement.goTo(UNIT_TILE*3, UNIT_TILE*3, 150);
			movement.goTo(UNIT_TILE, UNIT_TILE*3, 150);
			movement.goTo(UNIT_TILE, UNIT_TILE, 150);
		}
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
		dinaBOTmaster.connect();
		//dinaBOTmaster.alignBrick();
		//dinaBOTmaster.milestoneDemo();


		dinaBOTmaster.pathPickupTest();
		//dinaBOTmaster.moveTest();
		//DinaList<Integer> list = new DinaList<Integer>();

		//dinaBOTmaster.moveTest();

		while(true); //Never quit

	}

}
