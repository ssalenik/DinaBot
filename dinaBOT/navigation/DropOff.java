package dinaBOT.navigation;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import dinaBOT.mech.MechConstants;
import dinaBOT.sensor.USSensor;
import dinaBOT.sensor.USSensorListener;
import dinaBOT.comm.*;

/*
 *Using the odometer, you will  
 *have to write a method called dropOff(), situated here, which will
 *contain the series of commands that directs the robot to the drop off of a full stack  
 *of bricks at the edge of a designated drop off tile on the grid. The  
 *teacher has told us that the tiles along the drop off point  
 *would be clear of obstacles, so you don't have to worry about that.  
 *You will have to determine what the best way of doing this without  
 *knocking off possible other stacks already positioned on the drop off  
 *point. The point at which the robot starts the drop off will be at one  
 *of the outer gridline nodes of the tiles surrounding the drop off  
 *point. Once the robot chooses the start-dropoff point for the first  
 *time, it will always use the same time each time it needs to drop off  
 *another stack. The inputed data for the drop off will be given at
 *startup and would be two ints passed from the main.
 */

/*
 * Drop off point sketch
 					 (top)
				 	 -------	
			 		|		|
		(Left)		| drop	|	(right)
			 		| point	|
			  		 -------	
 					 (bottom)

		This set of coordinates designates the middle of the each of the sides of the drop point.
		This is only used to perform the drop for the second stack.
 */

/**
 *This class contains the drop off routine
 *
 *@author Alexandre Courtemanche, Vinh Phong Buu
 */

public class DropOff implements MechConstants, CommConstants, USSensorListener{

	//Constants
	final int TOP = 0;
	final int RIGHT = 1;
	final int BOTTOM = 2;
	final int LEFT = 3;
	
	//Experimental values
	public final int BACK_UP_DISTANCE = -15;
	public final int DUMP_DISTANCE = 22;
	
	//Fields
	public Odometer odometer;
	public Movement mover;
	public BTMaster slave_connection;
	public Localization localizer;

	public int[] dropCoords = new int[2];
	public int phase =0;
	public double[] left_side,right_side,top_side,bottom_side;
	public double stackAngle = 0;
	public boolean latchedStack = false;

	/**
	 * Creates a drop off mechanism to drop piles off in a designated grid tile.
	 * Works using a supplied Odometer and Movement.
	 * 
	 * @param odometer
	 * @param mover
	 * @param slave_connection
	 * @param localizer
	 * @param drop_x X coordinate of the bottom left node of the drop off tile (in Unit Tiles).
	 * @param drop_y Y coordinate of the bottom left node of the drop off tile (in Unit Tiles).
	 */
	public DropOff(Odometer odometer, Movement mover, BTMaster slave_connection, Localization localizer, int drop_x, int drop_y) {
		this.odometer = odometer;
		this.mover = mover;
		this.slave_connection = slave_connection;
		this.localizer = localizer;
		dropCoords[0] = drop_x;
		dropCoords[1] = drop_y;
		USSensor.low_sensor.registerListener(this);
	}

	/**
	 * Obtains the coordinates of the drop off area.
	 * 
	 * @return Array containing XY coordinates of the bottom left corner of the drop off point.
	 */
	public int[] getDropCoords() {
		return dropCoords;
	}


	/**
	 * Executes the drop off routine once the robot is adjacent to the drop off point
	 * 
	 * @return Success status
	 */
	//TODO: Try USSensorListener to verify presence of first stack & maybe potential risks that could have dropoff interrupted and thus return false
	public boolean dropOff(int stack) {
		boolean success = false;
		double facing= 0;

		//First stack drop off
		//Drop in the middle of the tile
		if (stack == 1) {
			double[] dropPoint = {dropCoords[0]*UNIT_TILE+UNIT_TILE/2,dropCoords[1]*UNIT_TILE+UNIT_TILE/2};
			//Essentially raise claws if this isn't already taken care of.
			slave_connection.request(PICKUP);
			
			//mover.goTo(dropCoords[0], dropCoords[1], SPEED_MED);
			//localizer.localizeLight();
			mover.goTo(dropPoint[0], dropPoint[1], SPEED_SLOW);
			mover.turnTo(Math.PI/2, SPEED_ROTATE);
			mover.goForward(BLOCK_DISTANCE+3, SPEED_MED);
			//Arbitrary orientation for now
			mover.turnTo(Math.PI/2, SPEED_ROTATE);
			slave_connection.request(OPEN_CAGE);
			mover.goForward(DUMP_DISTANCE, SPEED_SLOW);
			slave_connection.request(CLOSE_CAGE);

			//Should go back to nearest node after this returns
			success = true;
		} else if (stack == 2) {
			//Second stack, now assume stack 1 is in the middle of the the drop zone already

			//Define 4 stacking area sides
			left_side = new double[] {dropCoords[0]*UNIT_TILE,dropCoords[1]*UNIT_TILE+UNIT_TILE/2};
			right_side = new double[] {dropCoords[0]*UNIT_TILE+UNIT_TILE, dropCoords[1]*UNIT_TILE+UNIT_TILE/2};
			bottom_side = new double[] {dropCoords[0]*UNIT_TILE+UNIT_TILE/2, dropCoords[1]*UNIT_TILE};
			top_side = new double[] {dropCoords[0]*UNIT_TILE+UNIT_TILE/2, dropCoords[1]*UNIT_TILE+UNIT_TILE};

			//Ordering is clockwise starting with top
			double[][] sides = {top_side,right_side,bottom_side,left_side};

			//First, find the side closest to the current location of the robot (MUST avoid going through the drop zone).
			double[] position = odometer.getPosition();
			double closest_distance = -1;
			double[] closest_side = new double[2];
			double dX, dY, distance;
			int side = 0;

			for (int i=0; i < sides.length; i++) {
				dX = Math.abs(position[0]-sides[i][0]); 
				dY = Math.abs(position[1] - sides[i][1]);
				distance = Math.sqrt(dX*dX+dY*dY);
				if (distance < closest_distance || closest_distance == -1) {
					closest_distance = distance;
					closest_side = sides[i];
					side = i;
				}
			}
			
			//Second, get aligned with the stack present and push it back. (going backwards)
			//(maybe push it with the stack that is in the bot instead of the cage doors)
			slave_connection.request(PICKUP);
			mover.goTo(closest_side[0], closest_side[1], SPEED_MED);
			
			switch (side) {
			//Face the stack
			case TOP:
				mover.turnTo(3*Math.PI/2, SPEED_ROTATE);
				facing = 3*Math.PI/2;
				break;
			case RIGHT:
				mover.turnTo(Math.PI, SPEED_ROTATE);
				facing = Math.PI;
				break;
			case BOTTOM:
				mover.turnTo(Math.PI/2, SPEED_ROTATE);
				facing = Math.PI/2;
				break;
			case LEFT:
				mover.turnTo(0, SPEED_ROTATE);
				facing = 0;
				break;
			}
			
			Button.waitForPress();
			//Probably should verify first stack's presence with US
			//TODO: Implement USSensorListener methods
			
			//Latch Stack location to get proper position
			mover.goForward(BACK_UP_DISTANCE, SPEED_MED);
			//Redundant step
			mover.turnTo(facing, SPEED_ROTATE);
			phase = 1;
			//Very arbitrary check for stack (probably will be disabled)
			while (!latchedStack) {
				mover.turn(Math.PI/8, SPEED_ROTATE);
				mover.turn(-Math.PI/4, SPEED_ROTATE);
				mover.turn(Math.PI/8, SPEED_ROTATE);
			}
			phase = 0;
			//Turn 180 to be facing away
			//TODO: Fix huge orientation error here.
			mover.turnTo(facing+Math.PI, SPEED_ROTATE);
			
			//Drop the second stack next to the first one.
			slave_connection.request(OPEN_CAGE);
			mover.goForward(BACK_UP_DISTANCE, SPEED_SLOW);
			while(mover.isMoving());
			
			//Get away
			mover.goForward(DUMP_DISTANCE, SPEED_SLOW);
			slave_connection.request(CLOSE_CAGE);
			
			success = true;
		}

		return success;
	}

	@Override
	public void newValues(int[] new_values, USSensor sensor) {
		switch (phase) {
		case 0:
			//Do Nothing
			break;
			
		case 1:
			int lastValue1 = 255;
			int lastValue0 = 255;
			
			if (new_values[0] < UNIT_TILE*1.5 && new_values[1] < UNIT_TILE*1.5 && new_values[1] < lastValue1
					&& new_values[0] < lastValue0) {
				stackAngle = odometer.getPosition()[2];
				lastValue1 = new_values[1];
				lastValue0 = new_values[0];
				latchedStack = true;
			}
			break;
		}
		
	}
}


