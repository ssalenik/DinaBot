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
		slave_connection = new BTMaster();

		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicMovement(odometer, left_motor, right_motor);
	
		localization = new Localization(odometer, movement);
		
		map = new Map(odometer, 12, 45, UNIT_TILE);
		pather = new ManhattanPather(map, movement);
		
		navigator = new Navigator(odometer, movement, map, pather);
		
		blockFind = new BlockFinder(odometer, movement);
		
		dropper = new DropOff(odometer, movement, slave_connection, drop_x, drop_y);
	}
	
	public void connect() {
		while(!slave_connection.connect());
	}

	/**
	 * This is a method for block alignment using brick to brick communication (currently over bluetooth). It assumes the block is directly in front if the robot in an unknown orientation.
	 *
	*/
	public void alignBrick() {
		//Align contant (heuristically tuned)
		double forward_distance = 5;
		double rotate_angle = Math.PI/5;

		//Main program
		movement.goForward(forward_distance, SPEED_MED);

		movement.turn(rotate_angle, SPEED_ROTATE);

		if(slave_connection.request(HOLD)) {
			LCD.clear();
			LCD.drawString("Success ...", 0, 0);
		}

		movement.goForward(-forward_distance, SPEED_MED);

		movement.turn(-rotate_angle, SPEED_ROTATE);

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
	
	/**
	 * This is the "main" execution method of the robot. Here is the central thread of our program which should control everything.
	 *
	*/
	public void run() {
		//Setup
		odometer.enableSnapping(true);
		odometer.setDebug(true);
		
		//Pattern to follow
		int[][] pattern = {
			new int[] {5,0}, //x-coordinates
			new int[] {5,0} //y-coordinates
		};
		
		System.out.println("Starting...");
		
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
	
		for(int i = 0;i < pattern.length;i++) {
			System.out.println("Leg number: "+i);
			int nav_status = navigator.goTo(pattern[i][0],pattern[i][1], false);
			while(nav_status > 0) {
				System.out.println("Breaking for possible pellet");
				if(blockFind.sweep(odometer.getPosition()[2])) {
					System.out.println("Pickup");
					alignBrick();
					map.stop();
					slave_connection.request(PICKUP);
					map.start();
					block_count++;
					if(block_count == 6) {
						System.out.println("Robot Full... ending");
						return;
					}
				}
				nav_status = navigator.goTo(pattern[i][0],pattern[i][1], false);
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
		odometer.setDebug(true);
		odometer.setPosition(new double[] {UNIT_TILE*4,UNIT_TILE*4,0}, new boolean[] {true, true, true});
		odometer.enableSnapping(true);
		Random rand = new Random(9812);
		int x = 4;
		int y = 4;
		while(true) {
			int direction = rand.nextInt(4);
			
			if(x == 1 && direction == 2) direction = 3;
			if(x == 7 && direction == 0) direction = 1;
			if(y == 1 && direction == 3) direction = 0;
			if(y == 7 && direction == 1) direction = 2;
			
			if(direction == 0) x++;
			if(direction == 1) y++;
			if(direction == 2) x--;
			if(direction == 3) y--;
			
			movement.goTo(x*UNIT_TILE, y*UNIT_TILE, SPEED_MED);
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

		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster(2, 0); //Instantiate the DinaBOT Master

		//Run some tests
		dinaBOTmaster.connect();
		dinaBOTmaster.run();

		//dinaBOTmaster.moveTest();
		
		while(true); //Never quit
	}

}
