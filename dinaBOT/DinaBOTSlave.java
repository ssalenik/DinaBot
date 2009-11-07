package dinaBOT;
import lejos.nxt.*;
import lejos.nxt.Motor;
import dinaBOT.mech.*;
import dinaBOT.comm.*;

/**
 * The DinaBOT class is the central class of our project. It ties everything togethere. It <b>is</b> the robot.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/

/*
 class 16
 method 134
 */

public class DinaBOTSlave {
	
	public static final byte DO_NOTHING = 0;
	public static final byte PICKUP = 1;
	public static final byte OPEN_CAGE = 2;
	public static final byte CLOSE_CAGE = 3;	
	
	Stacking stacker;
	BTslave BTconnect;
	
	public DinaBOTSlave() {
		stacker = new Stacker(Motor.A, Motor.B, Motor.C);
		LCD.drawString("Going into constructor",0 ,0);
		Button.waitForPress();
		BTconnect = new BTslave();
		BTconnect.waitForConnection();
	}
	/**
	 * This is our Hello World method. It will be gone soon
	 *
	*/
	
	public void obey() {
		
		byte nextCommand = 0;
		boolean success = false;
		
		while(true) {
			nextCommand = BTconnect.waitForCommand();
			
			switch (nextCommand) {
					
				case DO_NOTHING:
					
					break;
				case PICKUP:
					success = stacker.activateMechanicalClaw();
					break;
					
				case OPEN_CAGE:
					stacker.openDockingBay();
					success = true;
					break;
					
				case CLOSE_CAGE:
					stacker.closeDockingBay();
					success = true;
					break;
					
			}
			BTconnect.sendStatus(success);
		}
		
	}
	
	/**
	 * This is where the static main method lies. This is where execution begins.
	 *
	 * @param args This is the command line args, this is irrelevent in the NXT
	*/
	public static void main(String[] args) {
		
		Button.ESCAPE.addButtonListener(new ButtonListener() {
										public void buttonPressed(Button b) {
										System.exit(0);
										} 
										
										public void buttonReleased(Button b) {
										System.exit(0);
										}
										});
		
		DinaBOTSlave dinaBOTslave = new DinaBOTSlave();
		dinaBOTslave.obey();
		
	}
	
	
	
	
}