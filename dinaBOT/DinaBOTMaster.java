package dinaBOT;

import lejos.nxt.*;

import java.util.Random;
import java.lang.Math;

import dinaBOT.navigation.*;
import dinaBOT.mech.*;
import dinaBOT.comm.*;
import dinaBOT.detection.*;
import dinaBOT.sensor.*;
import dinaBOT.util.*;


/**
 * The DinaBOTMaster is the main class the master brick. It <b>is</b> the robot. It contains the main() for the master.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTMaster implements MechConstants, CommConstants, SearchPatterns {

	/* -- Static Variables --*/

	Motor left_motor = Motor.A;
	Motor right_motor = Motor.B;

	/* -- Instance Variables --*/
	
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
	DropOff dropper;
	BlockFinder blockFind;	
	
	//Variables
	int block_count;

	/**
	 * This is the constructor for the DinaBOT master
	 *
	 * @param drop_x the x coordinate of the drop off (in tile units)
	 * @param drop_y the y coordinate of the drop off (in tile units)
	*/
	public DinaBOTMaster(int drop_x, int drop_y) {
		
		USSensor low = USSensor.low_sensor;
		USSensor high = USSensor.high_sensor;
		
		slave_connection = new BTMaster();

		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicMovement(odometer, left_motor, right_motor);
		
		localization = new Localization(odometer, movement);
		
		map = new Map(odometer, 9, 45, UNIT_TILE);
		pather = new ManhattanPather(map, movement);
		
		navigator = new Navigator(odometer, movement, map, pather);
		
		blockFind = new BlockFinder(odometer, movement, map);
		
		dropper = new DropOff(odometer, movement, slave_connection, drop_x, drop_y);
	}
	
	public void connect() {
		slave_connection.setDebug(true);
		while(!slave_connection.connect());
		slave_connection.setDebug(false);
	}

	/**
	 * This is a method for block alignment using brick to brick communication (currently over bluetooth). It assumes the block is directly in front if the robot in an unknown orientation.
	 *
	*/
	public void alignBrick() {
		//Align contant (heuristically tuned)
		double forward_distance = 5;
		double rotate_angle = Math.PI/4;

		//Main program
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

	// stepan's pathing and mapping test WORKS!!
	public void pathTest() {
		Map mapper = new Map(odometer, 12, 45, UNIT_TILE);
		Pathing pather = new ManhattanPather(mapper, movement);
		Navigator navigator = new Navigator(odometer, movement, mapper, pather);

		odometer.enableSnapping(true);
		odometer.setDebug(false);

		//Pause so the user can remove his hand from the robot
		try {
			Thread.sleep(1000);
		} catch(Exception e) {}

		// go to _,_ then 0,0
		if ( navigator.goTo(2*UNIT_TILE, 2*UNIT_TILE, true) == 0) navigator.goTo(0.0,0.0, true);		
	}
	
	/**
	 * This is the "main" execution method of the robot. Here is the central thread of our program which should control everything.
	 *
	*/
	public void run() {
		//Setup
		odometer.enableSnapping(true);
		odometer.setDebug(true);
		odometer.setPosition(new double[] {UNIT_TILE, UNIT_TILE, 0}, new boolean[] {true, true, true});
		
		//Pattern to follow
		int[][] pattern = ZIGZAG_X;
		
		//Getting drop off coordinates
		//Assuming that the coordinate of the drop-off point is the bottom left node of the tile
		int [] dropCoords = dropper.getDropCoords();
		
		//Consider the nodes around the drop off zone as obstacles.
		map.editMap(dropCoords[0],dropCoords[1], 1);
		map.editMap(dropCoords[0]+1,dropCoords[1], 1);
		map.editMap(dropCoords[0],dropCoords[1]+1, 1);
		map.editMap(dropCoords[0]+1,dropCoords[1]+1, 1);
		
		System.out.println("Starting...");
		
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
	
		for(int i = 0;i < pattern.length;i++) {
			System.out.println("Leg number: "+i);
			int nav_status = navigator.goTo(pattern[i][0]*UNIT_TILE,pattern[i][1]*UNIT_TILE, false);
			while(nav_status > 0) {
				double[] prev_pos = odometer.getPosition();
				odometer.enableSnapping(false);
				slave_connection.request(RELEASE);
				System.out.println("Breaking for possible pellet");
				if(blockFind.sweep(odometer.getPosition()[2])) {
					System.out.println("Pickup");
					alignBrick();
					map.stop();
					slave_connection.request(PICKUP);
					map.start();
					block_count++;
					
					movement.goTo(prev_pos[0], prev_pos[1], SPEED_MED);
					movement.turnTo(prev_pos[2], SPEED_ROTATE);
				}
				odometer.enableSnapping(true);
				if(block_count == 6) {
					System.out.println("Robot Full... returning");
					navigator.goTo(dropper.getDropCoords()[0]*UNIT_TILE, dropper.getDropCoords()[1]*UNIT_TILE, true);
					return;
				}
				nav_status = navigator.goTo(pattern[i][0]*UNIT_TILE,pattern[i][1]*UNIT_TILE, false);
				
			}
			if(nav_status < 0) {
				System.out.println("Impossible Path .. ending");
				return;
			}
		}
		
		System.out.println("Done");
	}
	
	/**
	 * This is a testing method of coordinate correction, randomly drives around in an 8 by 8 area starting at 4-4
	 *
	*/
	public void moveTest() {
		odometer.setDebug(false);
		odometer.setPosition(new double[] {UNIT_TILE*4,UNIT_TILE*4,0}, new boolean[] {true, true, true});
		odometer.enableSnapping(true);
		
		int[][] pattern = {
			new int[] {1,4},
			new int[] {1,7},
			new int[] {4,7},
			new int[] {4,4},
			new int[] {7,4},
			new int[] {7,7},
			new int[] {4,7},
			new int[] {4,4},
			new int[] {1,4},
			new int[] {1,1},
			new int[] {4,1},
			new int[] {4,4},
			new int[] {7,4},
			new int[] {7,1},
			new int[] {4,1},
			new int[] {4,4}
		};
		
		for(int i = 0;i < pattern.length;i++) {
			movement.goTo(pattern[i][0]*UNIT_TILE, pattern[i][1]*UNIT_TILE, SPEED_MED);
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
		
		//DO some drop off stuff here

		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster(3, 3); //Instantiate the DinaBOT Master

		//Run some tests
		dinaBOTmaster.connect();
		dinaBOTmaster.run();

		//dinaBOTmaster.moveTest();
		
		while(true); //Never quit
	}

}
