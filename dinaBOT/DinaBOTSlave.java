package dinaBOT;

import lejos.nxt.*;

import dinaBOT.mech.*;
import dinaBOT.comm.*;
//import dinaBOT.sound.*;

/**
 * The DinaBOTSlave is the main class the slave brick. It <b>is</b> the robot. It contains the main() for the slave.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTSlave implements CommConstants{

//	MusicPlayer music;
	Stacking stacker;
	BTSlave master_connection;

	boolean stayConnected;
	boolean listeningForInstructions = true;

	/**
	 * This is the contructor for the DinaBOT slave
	 *
	*/
	public DinaBOTSlave() {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
			}

			public void buttonReleased(Button b) {
				stacker.close();
				System.exit(0);
			}
		});
		stacker = new Stacker(Motor.A, Motor.B, Motor.C);
		master_connection = new BTSlave();
		master_connection.waitForConnection();
//		Song[] songSet = {Songs.marioOverworld2};
//		music = new MusicBox(songSet);
	}

	/**
	 * Obey waits for new bluetooth commands and obey them. Finally it returns sucess or failure to the Master.
	 *
	*/
	public void obey() {

		byte nextCommand = 0;
		boolean success = false;

		while(listeningForInstructions) {

			if (master_connection.isConnected()) {
				nextCommand = master_connection.waitForCommand();
				LCD.clear();
				LCD.drawString("Received "+ nextCommand,0,0);
				//Button.waitForPress();
				switch (nextCommand) {

					case DO_NOTHING:
						success = true;
						break;

					case OPEN_CAGE:
						stacker.openCage();
						success = true;
						break;

					case CLOSE_CAGE:
						stacker.closeCage();
						success = true;
						break;

					case GET_CAGE_STATUS:
						success = stacker.getCageStatus();
						break;

					case PICKUP:
						success = stacker.pickUp();
						break;

					case TAP:
						success = stacker.tap();
						break;

					case HOLD:
						success = stacker.hold();
						break;

					case RELEASE:
						success = stacker.release();
						break;

					case DISCONNECT:
						success = true;
						LCD.clear();
						LCD.drawString("Sending sever connection confirmation", 0, 0);
						master_connection.disconnectFlag();
						master_connection.sendStatus(success);
						LCD.clear();
						LCD.drawString("About to closeConnection()", 0, 0);
						break;

					/*case PLAY_SONG:
						success = music.play();
						break;

					case PAUSE_SONG:
						success = music.pause();
						break;

					case ABORT_SONG:
						success = music.abort();
						break;

					case NEXT_SONG:
						success = music.next();
						break;

					case PREVIOUS_SONG:
						success = music.previous();
						break;*/

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

		DinaBOTSlave dinaBOTslave = new DinaBOTSlave();
		dinaBOTslave.obey();
	}
}