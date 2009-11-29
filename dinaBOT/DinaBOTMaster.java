package dinaBOT;

import lejos.nxt.*;

import java.lang.Math;

import dinaBOT.comm.*;
import dinaBOT.detection.*;
import dinaBOT.mech.*;
import dinaBOT.navigation.*;
import dinaBOT.sensor.*;
import dinaBOT.util.*;


/**
 * The DinaBOTMaster is the main class the master brick. It <b>is</b> the robot. It contains the main() for the master.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTMaster implements MechConstants, CommConstants, SearchPatterns {

	/* -- Class Variables -- */

	static final int CAGE_FULL = 2;

	Motor left_motor = Motor.A;
	Motor right_motor = Motor.B;

	/* -- Instance Variables -- */

	//Connection objects
	BTMaster slave_connection;

	//Basics movement objects
	Odometer odometer;
	Movement movement;

	//High level navigation object
	Map map;
	Pathing pather;

	Navigator navigator;

	//Utilities
	Localization localization;
	BlockFinder blockFind;
	DropOff dropper;

	//Variables
	int pallet_count;
	int drop_count;
	int[] dropSetUpCoords;
	boolean debug;

	/**
	 * This is the constructor for the DinaBOTMaster
	 *
	 * @param input the x, y coordinates of the drop and the search pattern to use
	*/
	public DinaBOTMaster(int[] input) {
		//Add a convenient quit button
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				map.printMap();
				System.exit(0);
			}

			public void buttonReleased(Button b) {
			}
		});

		USSensor low = USSensor.low_sensor;
		USSensor high = USSensor.high_sensor;

		slave_connection = new BTMaster();

		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicMovement(odometer, left_motor, right_motor);

		map = new Map(odometer, 13, 9);
		pather = new ManhattanPather(map, movement);

		navigator = new Navigator(odometer, movement, map, pather);

		localization = new Localization(odometer, movement);
		blockFind = new BlockFinder(odometer, movement, map, slave_connection);
		dropper = new DropOff(odometer, movement, slave_connection,localization, input[0], input[1]);

		debug = true;
	}

	public void connect() {
		slave_connection.setDebug(true);
		while(!slave_connection.connect());
		slave_connection.setDebug(false);
	}

	/**
	 * This is a heuristic method for block alignment using brick to brick communication (currently over bluetooth). It assumes the block is directly in front if the robot in an unknown orientation.
	 *
	*/
	public void alignPallet() {
		//Align contants (heuristically tuned)
		double forward_distance = 5;
		double rotate_angle = Math.PI/4;

		//Alignement method
		movement.goForward(forward_distance, SPEED_MED);

		movement.turn(rotate_angle, SPEED_ROTATE);

		movement.goForward(forward_distance, SPEED_MED);

		slave_connection.request(HOLD);

		movement.goForward(-forward_distance, SPEED_MED);

		movement.turn(-rotate_angle, SPEED_ROTATE);

		for(int i = 0;i < 2;i++) {
			slave_connection.request(HOLD);
			movement.goForward(-forward_distance, SPEED_MED);

			slave_connection.request(RELEASE);

			movement.goForward(forward_distance, SPEED_MED);
		}
	}

	/**
	 * Searches for a pellet in the immediate vicinity and picks it up if found (also increments pallet_count)
	 *
	*/
	public boolean pickUpPallet() {
		double[] initial_position = odometer.getPosition(); //Remember start position

		odometer.enableSnapping(false); //Disable snapping for diagonal movement

		if(blockFind.sweep(odometer.getPosition()[2])) { //Perform sweep
			if(debug) System.out.println("Picking up");

			map.stop();
			slave_connection.request(RELEASE);
			
			alignPallet(); //If successfull align pallet

			if(pallet_count >= 5) movement.goForward(-3, SPEED_MED);

			boolean pickup = slave_connection.request(PICKUP); //Pickup

			if(pickup) {
				pallet_count++; //Increment pallet count

				double[] current_position = odometer.getPosition();
				movement.turnTo(Math.atan2((initial_position[1]-current_position[1]),(initial_position[0]-current_position[0]))+Math.PI, SPEED_ROTATE); //Return to start position
				movement.goForward(-Math.sqrt((initial_position[0]-current_position[0])*(initial_position[0]-current_position[0])+(initial_position[1]-current_position[1])*(initial_position[1]-current_position[1])), SPEED_MED);
				odometer.enableSnapping(true); //Renable snapping
				map.start(); //Reenable map

				return true;
			} else {
				double[] current_position = odometer.getPosition();
				movement.turnTo(Math.atan2((initial_position[1]-current_position[1]),(initial_position[0]-current_position[0]))+Math.PI, SPEED_ROTATE); //Return to start position
				movement.goForward(-Math.sqrt((initial_position[0]-current_position[0])*(initial_position[0]-current_position[0])+(initial_position[1]-current_position[1])*(initial_position[1]-current_position[1])), SPEED_MED);
				odometer.enableSnapping(true); //Renable snapping

				slave_connection.request(RELEASE);
				slave_connection.request(ARMS_UP);
			
				map.start(); //Reenable map
				return false;
			}
		} else {
			double[] current_position = odometer.getPosition();
			movement.turnTo(Math.atan2((initial_position[1]-current_position[1]),(initial_position[0]-current_position[0]))+Math.PI, SPEED_ROTATE); //Return to start position
			movement.goForward(-Math.sqrt((initial_position[0]-current_position[0])*(initial_position[0]-current_position[0])+(initial_position[1]-current_position[1])*(initial_position[1]-current_position[1])), SPEED_MED);
			odometer.enableSnapping(true); //Renable snapping

			return false;
		}

	}

	/**
	 * This method makes the robot move to the correct drop off point, depending on where it is positioned with respect to it.
	 *
	*/
	public void performDropOff() {

		//Getting drop off coordinates
		//Assuming that the coordinate of the drop-off point is the bottom left node of the tile

		double[] start_position = odometer.getPosition();

		if(dropSetUpCoords == null) {
			int[] dropCoords = dropper.getDropCoords();

			dropSetUpCoords = new int[2];
			dropSetUpCoords[0] = Functions.constrain(Functions.roundToInt(start_position[0]/UNIT_TILE), dropCoords[0]-1, dropCoords[0]+2);
			dropSetUpCoords[1] = Functions.constrain(Functions.roundToInt(start_position[1]/UNIT_TILE), dropCoords[1]-1, dropCoords[1]+2);
		}

		int nav_status = navigator.goTo(dropSetUpCoords[0]*UNIT_TILE, dropSetUpCoords[1]*UNIT_TILE, true, true);

		for(int i = 0;nav_status < 0;i++) {
			int[] nextDropCoord = dropper.getNextCoordinates(dropSetUpCoords);
			navigator.goTo(nextDropCoord[0] * UNIT_TILE, nextDropCoord[1] * UNIT_TILE, true, true);
			if(i == 8) {
				if(debug) System.out.println("Impossible Drop Off ..");
				map.reset();
				try {
					Thread.sleep(1500);
				} catch(Exception e) {

				}
				Button.waitForPress();
			}
		}

		localization.localizeAnywhere();
		dropper.dropOff(drop_count%2);
		drop_count++;

		if(drop_count == 1) {
			//Assuming that the coordinate of the drop-off point is the bottom left node of the tile
			int[] dropCoords = dropper.getDropCoords();

			//Consider the nodes around the drop off zone as obstacles.
			map.editMap(dropCoords[0],dropCoords[1], DROP_ZONE);
			map.editMap(dropCoords[0]+1,dropCoords[1], DROP_ZONE);
			map.editMap(dropCoords[0],dropCoords[1]+1, DROP_ZONE);
			map.editMap(dropCoords[0]+1,dropCoords[1]+1, DROP_ZONE);
		}

		pallet_count = 0;
		//Go back to start_position?
	}

	/**
	 * This is the "main" execution method of the robot. Here is the central thread of our program which should control everything.
	 *
	*/
	public void run() {
		//Setup

		odometer.enableSnapping(true);
		odometer.setDebug(false);
		odometer.setPosition(new double[] {UNIT_TILE, UNIT_TILE, 0}, new boolean[] {true, true, true});
		navigator.setBacktrack(false);
		//localization.localize();

		map.start();

		int[][] pattern = {//Zig-zag pattern
			new int[] {6,7},
			new int[] {11,1},
			new int[] {11,7},
			new int[] {6,1},
			new int[] {1,7},
			new int[] {1,1} // Go back to starting node
		};

		if(debug) System.out.println("Starting...");

		try {
			Thread.sleep(1000);
		} catch(Exception e) {

		}

		//right now finishes once it has dropped off the stack!
		for(int i = 0;i < pattern.length;) { //For each node in search path

			if(debug) System.out.println("Leg number: "+i);

			int nav_status = navigator.goTo(pattern[i][0]*UNIT_TILE,pattern[i][1]*UNIT_TILE, false, true); //Start moving to node

			while(nav_status > 0) { //Keep going till you get there
				if(debug) System.out.println("Breaking for possible pellet");
				boolean pickup = pickUpPallet(); //Pick up pallet for interrupt
				if(pallet_count == CAGE_FULL) {
					if(debug) System.out.println("Run drop off...");
					performDropOff(); //This should return us to the same point
				} else nav_status = navigator.goTo(pattern[i][0]*UNIT_TILE,pattern[i][1]*UNIT_TILE, false, pickup); //And keep moving to node
			}

			if(nav_status < 0) { //Make sure we exited sucess, not impossible path, this should trigger some sort of map reset
				double[] current_position = odometer.getPosition();
				if(debug) System.out.println("Negative 1");
				if(map.coordValue(current_position) >= DANGER) {
					if(debug) System.out.println("Inside Obstacle Ooops ...");
					Button.waitForPress();
					navigator.backtrack();
				} else if(map.coordValue(new double[] {pattern[i][0]*UNIT_TILE, pattern[i][1]*UNIT_TILE}) >= OBSTACLE) {
					i++;
				} else {
					if(debug) System.out.println("Impossible Path ..");
					map.reset();
					try {
						Thread.sleep(1500);
					} catch(Exception e) {

					}
					Button.waitForPress();
				}
			} else {
				i++;				
			}
		}

		if(debug) System.out.println("Done");
	}

	public void grabTest() {
		while(true) {
			slave_connection.request(RELEASE);
			Button.waitForPress();
			slave_connection.request(PICKUP);
			Button.waitForPress();
			slave_connection.request(RELEASE);
			slave_connection.request(ARMS_UP);
			Button.waitForPress();
			slave_connection.request(OPEN_CAGE);
			slave_connection.request(CLOSE_CAGE);
		}
	}

	public void grabConfirm() {

		while (true) {

			Button.waitForPress();
			slave_connection.request(RELEASE);
			Button.waitForPress();

			if(slave_connection.request(PICKUP)) {
				LCD.clear();
				LCD.drawString("Success",0,0);
			}
			else {
				LCD.clear();
				LCD.drawString("Failure",0,0);
			}


		}

	}

	/**
	 * This is where the static main method lies. This is where execution begins for the master brick
	 *
	 * @param args This is the command line args, this is irrelevant in the NXT
	*/
	public static void main(String[] args) {
		//DO some drop off input stuff here

		//int[] dropCoords = getUserInput();
		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster(new int[] {6,7,4}); //Instantiate the DinaBOT Master

		//Run some tests
		dinaBOTmaster.connect();
		//dinaBOTmaster.grabConfirm();
		dinaBOTmaster.run();

		while(true); //Never quit
	}

	/**
	 * User coordinates input method. This is a static method called at the beginning of runtime for all the setup to be done.
	 * This would include the drop coordinates and the search pattern to be used.
	 * @return input Array containing the x-coordinate and y-coordinate of the drop-off tile, and which search pattern to execute.
	 */
	public static int[] getUserInput() {

		boolean enterPressed = false;
		int nameIndex= 0;
		int[] input= {0,0/*,0*/};

		// Enter the x coordinate of the dropoff tile
		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(input[0] + " " + input[1]/*+ "\nPattern: " + SearchPatterns.PatternNames[nameIndex]*/, 0,0);
			int buttonID = Button.waitForPress();
			switch(buttonID) {
				case Button.ID_LEFT:
					input[0]--;
					break;
				case Button.ID_RIGHT:
					input[0]++;
					break;
				case Button.ID_ENTER:
					enterPressed = true;
					break;
			}
		}

		enterPressed = false;

		//Enter the y coordinate of the dropoff tile
		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(input[0] + " " + input[1]/*+ "\nPattern: " + SearchPatterns.PatternNames[nameIndex]*/, 0,0);
			int buttonID = Button.waitForPress();
			switch(buttonID) {
				case Button.ID_LEFT:
					input[1]--;
					break;
				case Button.ID_RIGHT:
					input[1]++;
					break;
				case Button.ID_ENTER:
					enterPressed = true;
					break;
			}
		}

		enterPressed = false;
		/*
		//Enter the search pattern
		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(input[0] + " " + input[1]+ "\nPattern: " + SearchPatterns.PatternNames[nameIndex], 0,0);
			int buttonID = Button.waitForPress();
			switch(buttonID) {
				case Button.ID_LEFT:
					if(nameIndex == 0)
						nameIndex = PatternNames.length - 1;
					else
						nameIndex--;
					break;
				case Button.ID_RIGHT:
					nameIndex = (++nameIndex) % SearchPatterns.PatternNames.length;
					break;
				case Button.ID_ENTER:
					enterPressed = true;
					input[3] = nameIndex;
					break;
			}
		}
		*/

		try {
			Thread.sleep(2000);
			LCD.drawString("Loading main program...", 0,0);
		} catch(Exception e) {

		}

		return input;

	}



}
