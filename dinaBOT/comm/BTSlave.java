package dinaBOT.comm;

import java.io.*;
import javax.bluetooth.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * The BTslave class is the class that handles the communication with the master brick. By default, it is always the initiated of the bluetooth
 * connection. When connected, it will establish an data input stream to receive information about the commands it needs to do, and it will establish
 * a data output stream to send information about the success of the methods it has initiated.
 *
 * @author Alexandre Courtemanche
 * @see BTMaster
 * @version 1
*/
public class BTSlave implements CommConstants {

	public BTConnection connection;

	public DataInputStream dataIn;
	public DataOutputStream dataOut;
	
	boolean connected;

	/**
	 * This constructor merely instantiates. All the connections are done in the connect() method.
	 *
	*/
	public BTSlave() {

	}

	/**
	 * The waitForConnection() method should be called when you want the slave brick to wait for an initiated connection from the master brick. 
	 * for a connection or the connection will fail. The method will establish a bluetooth connection and establish the input and output streams 
	 * on the slave side of the connection. 
	 *
	 * @see #disconnect()
	*/
	public void waitForConnection() {

		LCD.clear();
		LCD.drawString("Waiting for Connection",0, 0);
		BTConnection connection = Bluetooth.waitForConnection();
		LCD.clear();
		LCD.drawString("Connected",0, 0);

		dataIn = connection.openDataInputStream();
		dataOut = connection.openDataOutputStream();

	}

	/**
	 * The waitForCommand() method should be called when you want the slave brick to read the next instruction sent by the master brick. 
	 *
	 * @return returns the instruction sent by the master brick
	*/
	public byte waitForCommand() {

		byte nextInstruction = 0;

		try {
			nextInstruction = dataIn.readByte();
		} catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("Error reading next command from master: \n"+ioe.toString(), 0, 0);
		}

		return nextInstruction;

	}

	/**
	 * The sendStatus() method is used to send success or failure signals to the master brick. 
	 *
	 * @param success the status to be sent back to the master brick
	*/
	public void sendStatus(boolean success) {

		try {
			dataOut.writeBoolean(success);
			dataOut.flush();
		} catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("Error sending success status: \n"+ioe.toString(), 0, 0);
		}

	}

	/**
	 * Method to close the bluetooth connection properly. 
	 *
	 * @see #waitForConnection()
	*/
	public void disconnect() {

		try {
			dataIn.close();
			dataOut.close();
			connection.close();
		} catch(IOException ioe) {
			System.out.println(" Error closing connection " + ioe);
		}

	}

}
