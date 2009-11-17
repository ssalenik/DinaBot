package dinaBOT;

import lejos.nxt.*;

import java.util.Random;
import java.lang.Math;

import dinaBOT.navigation.*;
import dinaBOT.mech.*;
import dinaBOT.comm.*;
import dinaBOT.detection.*;

/**
 * The DinaBOTMaster is the main class the master brick. It <b>is</b> the robot. It contains the main() for the master.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
 */
public class DinaBOTMaster implements MechConstants, CommConstants {
	
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
		
		/* User Input */
		
		while(!enterPressed) {
			LCD.clear();
			LCD.drawString(offsetX + "   " + offsetY, 0,0);
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
			LCD.drawString(offsetX + "   " + offsetY,0,0 );
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
	
	// stepan's pathing and mapping test  WORKS!!
	public void pathTest() {
		Map mapper = new Map(odometer, 12, 45, UNIT_TILE);
		Pathing pather = new ManhattanPather(mapper, movement);
		double[][] path;
		double[] position;
				
		odometer.enableSnapping(true);
		odometer.setDebug(true);
		
		//Pause so the user can remove his hand from the robot
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
		
		}
		
		//position = odometer.getPosition();
		
		//obstacles
		/*
		mapper.editMap(2, 0, 2);
		mapper.editMap(2, 1, 2);
		mapper.editMap(3, 0, 2);
		mapper.editMap(3, 1, 2);
		
		path = pather.generatePath(0.0, 0.0, 0, 3*UNIT_TILE, 3*UNIT_TILE);
		
		if( path != null) {
			for(int i = 0; i < path.length; i++) {
				movement.goTo(path[i][0], path[i][1], 150);
			}
		}
		* */
		
		// go to 6,0
		double[] end = new double[] {5*UNIT_TILE, 5*UNIT_TILE};
		
		position = odometer.getPosition();
		
		path = pather.generatePath( position[0], position[1], position[2], end[0], end[1]);
		
		if( path != null) {
			for(int i = 0; i < path.length; i++) {
				if( mapper.obstacleCheck() ) {
					position = odometer.getPosition();
					path = pather.generatePath( position[0], position[1], position[2], end[0], end[1]);
					i = 0;
				}
				movement.goTo(path[i][0], path[i][1], 150);
			}
		}
		
		// go back to 0,0
		end =  new double[] {0*UNIT_TILE, 0*UNIT_TILE};
		
		position = odometer.getPosition();
		
		path = pather.generatePath( position[0], position[1], position[2], end[0], end[1]);
		
		if( path != null) {
			for(int i = 0; i < path.length; i++) {
				if( mapper.obstacleCheck() ) {
					position = odometer.getPosition();
					path = pather.generatePath( position[0], position[1], position[2], end[0], end[1]);
					i = 0;
				}
				movement.goTo(path[i][0], path[i][1], 150);
			}
		}	
	}
	
	public void connect() {
		while(!slave_connection.connect());
	}
	
	public void moveTest() {
		odometer.setDebug(true);
		odometer.enableSnapping(true);
		odometer.setPosition(new double[] {UNIT_TILE, UNIT_TILE, 0}, new boolean[] {true, true, true});
		movement.goTo(UNIT_TILE*4, UNIT_TILE, SPEED_FAST);
		movement.turnTo(Math.PI/2, SPEED_ROTATE);
		movement.goTo(UNIT_TILE*4, UNIT_TILE*3, SPEED_FAST);
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
		//dinaBOTmaster.connect();
		//dinaBOTmaster.alignBrick();
		//dinaBOTmaster.milestoneDemo();
		dinaBOTmaster.moveTest();
		while(true); //Never quit
	}
	
}
