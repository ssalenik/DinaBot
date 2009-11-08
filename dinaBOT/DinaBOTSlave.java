package dinaBOT;

import lejos.nxt.*;

import dinaBOT.mech.*;
import dinaBOT.comm.*;

/**
 * The DinaBOTSlave is the main class the slave brick. It <b>is</b> the robot. It contains the main() for the slave.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTSlave implements CommConstants {

	BTSlave master_connection;
	Stacking stacker;
	
	/**
	 * This is the contructor for the DinaBOT slave
	 *
	*/
	public DinaBOTSlave() {
		stacker = new Stacker(Motor.A, Motor.B, Motor.C);
		
		master_connection = new BTSlave();
		master_connection.waitForConnection();
	}

	/**
	 * Obey waits for new bluetooth commands and obey them. Finally it returns sucess or failure to the Master.
	 *
	*/
	public void obey() {

		byte nextCommand = 0;
		boolean success = false;

		while(true) {
			nextCommand = master_connection.waitForCommand();

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
			
			master_connection.sendStatus(success);
		}

	}
	
	/**
	 * Makes a simple push button interface for testing our stacking system. Press enter to lift and escape to open/close the bay doors
	 *
	*/
	public void stackTest() {
		boolean dock_status = false;
		
		while(true) {
			Button.waitForPress();
			if(Button.ENTER.isPressed()) {
				stacker.activateMechanicalClaw();
			} else if(Button.ESCAPE.isPressed()) {
				if(!dock_status) stacker.openDockingBay();
				else stacker.closeDockingBay();
				dock_status = !dock_status;
			}
		}
	}

	/**
	 * This is where the static main method lies. This is where execution begins.
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

		DinaBOTSlave dinaBOTslave = new DinaBOTSlave(); //Instantiate the DinaBOT Slave
		//Run some tests
		dinaBOTslave.obey(); //Wait for commands and obey
		//dinaBOTslave.stackTest(); //Test stacking system
		
		while(true); //Never quit
	}
	
}