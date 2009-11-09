package dinaBOT;

import lejos.nxt.*;

import dinaBOT.mech.*;
import dinaBOT.comm.*;

/**
 * The DinaBOT class is the central class of our project. It ties everything togethere. It <b>is</b> the robot.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
 */

public class DinaBOTSlave implements CommConstants{
	
	Stacking stacker;
	BTSlave master_connection;
	
	boolean stayConnected;
	boolean listeningForInstructions = true;
	
	public DinaBOTSlave() {
		stacker = new Stacker(Motor.A, Motor.B, Motor.C);
		master_connection = new BTSlave();
		master_connection.waitForConnection();
	}
	/**
	 * This is our Hello World method. It will be gone soon
	 *
	 */
	
	public void obey() {
		
		byte nextCommand = 0;
		boolean success = false;
		
		while(listeningForInstructions) {
			
			if (master_connection.isConnected()) {
				nextCommand = master_connection.waitForCommand();
				LCD.clear();
				LCD.drawString("Received  "+ nextCommand,0,0);
				//Button.waitForPress();
				switch (nextCommand) {
						
					case DO_NOTHING:
						success = true;
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
						
					case GET_CAGE_STATUS:
						success = stacker.getDockStatus();
						break;
					
					case TAP:
						success = stacker.tap();
						break;
						
					case DISCONNECT:
						success = true;
						LCD.clear();
						LCD.drawString("Send sever connection confirmation?", 0, 0);
						master_connection.disconnectFlag();
						master_connection.sendStatus(success);
						LCD.clear();
						LCD.drawString("About to closeConnection()", 0, 0);
						break;						
						
				}
				if(master_connection.isConnected())
					master_connection.sendStatus(success);				
			}
			
			else {
				// I'm not sure why, but you have to make the thread wait for this minimum amount of time or else a null exception comes up
				// This might be the result of the slave brick waiting for a connection that already exists and there ends up being a null pointer.
				try {
					Thread.sleep(3500);
				}
				catch (Exception e) {
					
				}
				master_connection.waitForConnection();
			}
			
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
		try {
			dinaBOTslave.obey();
		}
		catch(Exception e) {
			LCD.clear();
			LCD.drawString(e.toString(), 0, 0);
			Button.waitForPress();
		}
		
	}
}