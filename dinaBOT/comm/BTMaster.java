package dinaBOT.comm;

import java.io.*;
import javax.bluetooth.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * The BTMaster class is the class that handles communication from the master brick to the slave brick. It establishes a bluetooth connection and then
 * creates both a data input stream and data output stream. Through the output stream, it sends out byte encoded signals for commands and waits for the
 * response delivered in the input stream to do anything else.
 *
 * @author Alexandre Courtemanche, François Ouellet-Delorme
*/
public class BTMaster implements CommConstants {
	// Haven't implemented a system of retries yet
	static final int MAX_FIND_DEVICE_ATTEMPTS = 3;
	static final int MAX_CONNECT_ATTEMPTS = 3;

	RemoteDevice btrd;

	BTConnection connection;

	DataInputStream dataIn;
	DataOutputStream dataOut;

	boolean debug;
	boolean connected = false;


	/**
	 * This constructor merely instantiates. All the connections are done in the connect() method.
	 *
	*/
	public BTMaster() {

	}

	/**
	 * This method returns the status of the connection
	 *
	 * @return true if there is a connection and false otherwise.
	*/
	public boolean isConnected() {
		return connected;
	}

	/**
	 * The connect() method should be called when you want to connect to the slave brick. This method should <b>NOT</b> be called unless the slave is waiting
	 * for a connection or the connection will fail. The method will establish a bluetooth connection and establish the input and output streams on the master
	 * side of the connection.
	 *
	 * @return true if the connect attempt is successful, and false otherwise.
	*/
	public boolean connect() {
		if(!connected) {

			btrd = Bluetooth.getKnownDevice(SLAVE_NAME);

			if (btrd == null) {
				if(debug) {
					LCD.clear();
					LCD.drawString("Cannot find device +"+ SLAVE_NAME, 0, 0);
				}
				connected = false;
				return connected;
			}

			if(debug) {
				LCD.clear();
				LCD.drawString("Connecting...", 0, 0);
			}

			connection = Bluetooth.connect(btrd);

			if (connection == null) {
				if(debug) {
					LCD.clear();
					LCD.drawString("Connect failed", 0, 0);
				}
				connected = false;
				return connected;
			}

			if(debug) {
				LCD.clear();
				LCD.drawString("Connected", 0, 0);
			}
			dataIn = connection.openDataInputStream();
			dataOut = connection.openDataOutputStream();

			connected = true;
		}

		return connected;
	}

	/**
	 * Sends the signal for request touch to the slave brick and waits for a success or failure signal from it. It then returns that signal.
	 *
	 * @param request the request code to be sent
	 * @return true if the tap succeeded and false if it didn't.
	*/
	public boolean request(int request) {
		boolean success = false;

		try {
			dataOut.writeByte(request);
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
	 * @return true if the connection has closed, false otherwise.
	*/
	public boolean disconnect() {
		try {
			dataOut.writeByte(DISCONNECT);
			dataOut.flush();
			connected = !dataIn.readBoolean();

			dataIn.close();
			dataOut.close();
			connection.close();
		} catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}

		return connected;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
