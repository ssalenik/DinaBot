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

	static final int CAGE_FULL = 6;

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
	boolean debug;

	/**
	 * This is the constructor for the DinaBOTMaster
	 *
	 * @param drop_x the x coordinate of the drop off (in tile units)
	 * @param drop_y the y coordinate of the drop off (in tile units)
	*/
	public DinaBOTMaster(int drop_x, int drop_y) {
		//Add a convenient quit button
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				System.exit(0);
			}

			public void buttonReleased(Button b) {
				System.exit(0);
			}
		});

		USSensor low = USSensor.low_sensor;
		USSensor high = USSensor.high_sensor;

		slave_connection = new BTMaster();

		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicMovement(odometer, left_motor, right_motor);

		map = new Map(odometer, 13, 9, 45, UNIT_TILE);
		pather = new ManhattanPather(map, movement);

		navigator = new Navigator(odometer, movement, map, pather);

		localization = new Localization(odometer, movement);
		blockFind = new BlockFinder(odometer, movement, map, slave_connection);
		dropper = new DropOff(odometer, movement, slave_connection,localization, drop_x, drop_y);

		debug = false;
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
		System.out.println("Go");
		odometer.enableSnapping(false); //Disable snapping for diagonal movement

		if(blockFind.sweep(odometer.getPosition()[2])) { //Perform sweep
			if(debug) System.out.println("Picking up");

			alignPallet(); //If successfull align pallet

			map.stop(); //Temporarily disable map (stuff will pass in front of the sensor)
			slave_connection.request(PICKUP); //Pickup
			map.start(); //Reenable map

			pallet_count++; //Increment pallet count

			double[] current_position = odometer.getPosition();
			movement.turnTo(Math.atan2((initial_position[1]-current_position[1]),(initial_position[0]-current_position[0]))+Math.PI, SPEED_ROTATE); //Return to start position
			movement.goForward(-Math.sqrt((initial_position[0]-current_position[0])*(initial_position[0]-current_position[0])+(initial_position[1]-current_position[1])*(initial_position[1]-current_position[1])), SPEED_MED);
			odometer.enableSnapping(true); //Renable snapping

			return true;
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
	public void goToDropArea() {

		//Getting drop off coordinates
		//Assuming that the coordinate of the drop-off point is the bottom left node of the tile
		int[] dropCoords = dropper.getDropCoords();

		double[] start_position = odometer.getPosition();

		int[] dropSetUpCoords = new int[2];
		dropSetUpCoords[0] = Functions.constrain(Functions.roundToInt(start_position[0]/UNIT_TILE), dropCoords[0]-1, dropCoords[0]+2);
		dropSetUpCoords[1] = Functions.constrain(Functions.roundToInt(start_position[1]/UNIT_TILE), dropCoords[1]-1, dropCoords[1]+2);

		int nav_status = navigator.goTo(dropSetUpCoords[0]*UNIT_TILE, dropSetUpCoords[1]*UNIT_TILE, true, true);
	
		while (nav_status < 0) {
			int[] nextDropCoord = dropper.getNextCoordinates(dropSetUpCoords);
			navigator.goTo(nextDropCoord[0] * UNIT_TILE, nextDropCoord[1] * UNIT_TILE, true, true);
		}
		
		localization.localizeAnywhere();
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

		localization.localize();

		map.start();
		
		int[][] pattern = { new int[] {12,12}, new int[] {1,1} //Zig-zag pattern

			/*new int[] {6,1},
			new int[] {6,2},
			new int[] {1,2},
			new int[] {1,3},
			new int[] {6,3},
			new int[] {6,4},
			new int[] {1,4},
			new int[] {1,5},
			new int[] {6,5},
			new int[] {6,6},
			new int[] {1,1}*/ // Go back to starting node
		};

		//Assuming that the coordinate of the drop-off point is the bottom left node of the tile
		int[] dropCoords = dropper.getDropCoords();

		//Consider the nodes around the drop off zone as obstacles.
		map.editMap(dropCoords[0],dropCoords[1], DROP_ZONE);
		map.editMap(dropCoords[0]+1,dropCoords[1], DROP_ZONE);
		map.editMap(dropCoords[0],dropCoords[1]+1, DROP_ZONE);
		map.editMap(dropCoords[0]+1,dropCoords[1]+1, DROP_ZONE);

		if(debug) System.out.println("Starting...");

		try {
			Thread.sleep(1000);
		} catch(Exception e) {

		}

		boolean done = false;

		//right now finishes once it has dropped off the stack!
		while(!done) {
		for(int i = 0;i < pattern.length;i++) { //For each node in search path

			if(debug) System.out.println("Leg number: "+i);

				int nav_status = navigator.goTo(pattern[i][0]*UNIT_TILE,pattern[i][1]*UNIT_TILE, false, true); //Start moving to node

				while(nav_status > 0) { //Keep going till you get there
					if(debug) System.out.println("Breaking for possible pellet");
					boolean pickup = pickUpPallet(); //Pick up pallet for interrupt
					if(pallet_count == CAGE_FULL) {
						if(debug) System.out.println("Run drop off...");
						goToDropArea(); //This should return us to the same point
						dropper.dropOff(1);
						done = true;
					} else nav_status = navigator.goTo(pattern[i][0]*UNIT_TILE,pattern[i][1]*UNIT_TILE, false, pickup); //And keep moving to node
				}

				if(done) continue;

				if(nav_status < 0) { //Make sure we exited sucess, not impossible path, this should trigger some sort of map reset
					if(debug) System.out.println("Impossible Path ..");
					map.reset();
					try{
					Thread.sleep(1500);
					} catch (Exception e) {}
				}
			}
		}

		if(debug) System.out.println("Done");
	}

	public void indeed() {
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

	/**
	 * This is where the static main method lies. This is where execution begins for the master brick
	 *
	 * @param args This is the command line args, this is irrelevant in the NXT
	*/
	public static void main(String[] args) {
		//DO some drop off input stuff here

		//int[] dropCoords = userInput();
		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster(9,2); //Instantiate the DinaBOT Master

		//Run some tests
		dinaBOTmaster.connect();
		dinaBOTmaster.indeed();

		while(true); //Never quit
	}
	
	/**
	 * User coordinates input method. This is a static method called at the beginning of runtime for all the setup to be done.
	 * This would include the drop coordinates and the search pattern to be used.
	 */
	
	public static int[] userInput() {
		
		boolean enterPressed = false;
		int[] input= {0,0,0};
		
		//User Input
		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(input[0] + "   " + input[1]+ " " + input[2], 0,0);
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
		
		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(input[0] + "   " + input[1]+ " " + input[2], 0,0);
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
		
		try {
			Thread.sleep(2000);
			LCD.drawString("Loading main program...", 0,0);
		} catch(Exception e) {
			
		}
		
		return input;
		
	}
	
	

}
