package dinaBOT.comm;

import java.io.*;
import javax.bluetooth.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * The BTmaster class is the class that handles communication from the master brick to the slave brick. It establishes a bluetooth connection and then
 * creates both a data input stream and data output stream. Through the output stream, it sends out byte encoded signals for commands and waits for the 
 * response delivered in the input stream to do anything else. 
 *
 * @author Alexandre Courtemanche
 * @see BTSlave
 * @version 1
*/
public class BTMaster implements CommConstants {

	RemoteDevice btrd;
	
	BTConnection connection;
	
	DataInputStream dataIn;
	DataOutputStream dataOut;
	
	boolean connected;

	/**
	 * This constructor merely instantiates. All the connections are done in the connect() method.
	 *
	 * @see #connect()
	*/
	public BTMaster() {

	}

	/**
	 * This method returns the status of the connection
	 *
	 * @return Returns true if there is a connection and returns false otherwise.
	 * @see #connect()
	 * @see #disconnect()
	*/
	public boolean isConnected() {
		return connected;
	}

	/**
	 * The connect() method should be called when you want to conect to the slave brick. This method should <b>NOT</b> be called unless the slave is waiting
	 * for a connection or the connection will fail. The method will establish a bluetooth connection and establish the input and output streams on the master 
	 * side of the connection.
	 *
	 * @return Returns true if the connect attempt is successful, and returns false otherwise.
	 * @see #isConnected()
	 * @see #disconnect()
	*/
	public boolean connect() {

		btrd = Bluetooth.getKnownDevice(SLAVE_NAME);

		if(btrd == null) {
			LCD.clear();
			LCD.drawString("Cannot find device +"+ SLAVE_NAME, 0, 0);
			connected = false;
			return connected;
		}

		LCD.clear();
		LCD.drawString("Connecting...", 0, 0);

		connection = Bluetooth.connect(btrd);

		if(connection == null) {
			LCD.clear();
			LCD.drawString("Connect fail", 0, 0);
			connected = false;
			return connected;
		}

		LCD.clear();
		LCD.drawString("Connected", 0, 0);

		dataIn = connection.openDataInputStream();
		dataOut = connection.openDataOutputStream();

		connected = true;
		return connected;
	}

	/**
	 * Sends the signal for request pickup to the slave brick and waits for a success or failure signal from it. It then returns that signal.
	 *
	 * @return true if the pickup succeeded and false if it didn't. 
	*/
	public boolean requestPickup() {

		boolean success = false;

		try {
			dataOut.writeByte(PICKUP);
			dataOut.flush();
			success = dataIn.readBoolean();
		} catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}

		return success;
	}

	/**
	 * Sends the signal for open cage to the slave brick and waits for a success or failure signal from it. It then returns that signal.
	 *
	 * @return true if the pickup succeeded and false if it didn't. 
	 * @see #closeCage()
	*/
	public boolean openCage() {

		boolean success = false;

		try {
			dataOut.writeByte(OPEN_CAGE);
			dataOut.flush();
			success = dataIn.readBoolean();
		} catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}

		return success;
	}

	/**
	 * Sends the signal for close cage to the slave brick and waits for a success or failure signal from it. It then returns that signal.
	 *
	 * @return returns true if the pickup succeeded and false if it didn't. 
	 * @see #openCage()
	*/
	public boolean closeCage() {

		boolean success = false;

		try {
			dataOut.writeByte(CLOSE_CAGE);
			dataOut.flush();
			success = dataIn.readBoolean();
		} catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}

		return success;
	}

	/**
	 * Method to close the bluetooth connection properly. 
	 *
	 * @see #connect()
	 * @see #isConnected()
	*/
	public void disconnect() {

		try {
			dataIn.close();
			dataOut.close();
			connection.close();
		} catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}
		
		connected = false;

	}

}
