package dinaBOT.navigation;

import dinaBOT.mech.MechConstants;
import dinaBOT.sensor.*;

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

/**
 *This class contains the drop off routine
 *
 */

public class DropOff implements MechConstants{
	
	public Odometer odometer;
	public Movement mover;
	public BTmaster slave_connection;
	
	public int dropOffX, dropOffY;
	
	//Add other instances you'll need
	
	public DropOff(Odometer odometer, Movement mover, BTmaster slave_connection, int dropOffX, int dropOffY) {
		this.odometer = odometer;
		this.mover = mover;
		this.slave_connection = slave_connection;
	}
	
	public void dropOff() {
		//implement dropOff routine here
	}
}


