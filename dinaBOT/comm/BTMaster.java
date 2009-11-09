package dinaBOT.comm;

import java.io.*;
import javax.bluetooth.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * The BTMaster class is the class that handles communication from the master brick to the slave brick. It establishes a bluetooth connection and then
 * creates both a data input stream and data output stream. Through the output stream, it sends out byte encoded signals for commands and waits for the 
 * response delivered in the input stream to do anything else. 
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
 */

public class BTMaster implements CommConstants{
		
	public BTConnection connection;
	public RemoteDevice btrd;
	public DataInputStream dataIn;
	public DataOutputStream dataOut;
	public boolean connected = false;
	
	// Haven't implemented a system of retries yet
	public static final int MAX_FIND_DEVICE_ATTEMPTS = 3;
	public static final int MAX_CONNECT_ATTEMPTS = 3;
	
	
	/**
	 * This constructor merely instantiates. All the connections are done in the connect() method.
	 */
	public BTMaster() {
		
	}
	
	/**
	 * This method returns the status of the connection
	 * @return Returns true if there is a connection and returns false otherwise.
	 */
	
	public boolean isConnected() {
		return connected;
	}	
	
	/**
	 * The connect() method should be called when you want to conect to the slave brick. This method should <b>NOT</b> be called unless the slave is waiting
	 * for a connection or the connection will fail. The method will establish a bluetooth connection and establish the input and output streams on the master 
	 * side of the connection. 
	 * @return Returns true if the connect attempt is successful, and returns false otherwise.
	 */
	public boolean connect() {
		
		if(!connected) {
			
			btrd = Bluetooth.getKnownDevice(SLAVE_NAME);

			if (btrd == null) {
				LCD.clear();
				LCD.drawString("Cannot find device +"+ SLAVE_NAME, 0, 0);
				connected = false;
				return connected;
			}
						
			LCD.clear();
			LCD.drawString("Connecting...", 0, 0);
			
			connection = Bluetooth.connect(btrd);
			
			if (connection == null) {
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
			
		}
		return connected;
	}
	
	/**
	 * Sends the signal for request pickup to the slave brick and waits for a success or failure signal from it. It then returns that signal.
	 *
	 * @return Returns true if the pickup succeeded and false if it didn't. 
	 */
	public boolean requestPickup() {
		
		boolean success = false;
		
		try{
			dataOut.writeByte(PICKUP);
			dataOut.flush();
			success = dataIn.readBoolean();
		}
		catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}
		
		return success;
	}
	
	/**
	 * Sends the signal for open cage to the slave brick and waits for a success or failure signal from it. It then returns that signal.
	 *
	 * @return Returns true if the pickup succeeded and false if it didn't. 
	 */	
	public boolean openCage() {
		
		boolean success = false;
		
		try{
			dataOut.writeByte(OPEN_CAGE);
			dataOut.flush();
			success = dataIn.readBoolean();
		}
		catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}
		
		return success;
	}
	
	/**
	 * Sends the signal for close cage to the slave brick and waits for a success or failure signal from it. It then returns that signal.
	 *
	 * @return Returns true if the pickup succeeded and false if it didn't. 
	 */		
	public boolean closeCage() {
		
		boolean success = false;
		
		try{
			dataOut.writeByte(CLOSE_CAGE);
			dataOut.flush();
			success = dataIn.readBoolean();
		}
		catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}
		
		return success;
	}
	
	/**
	 * Sends the signal to receive the status of the cage to the slave brick and waits for an open or closed signal from it. It then returns that signal.
	 *
	 * @return Returns true if the cage is open and false if it's closed. 
	 */			
	public boolean getCageStatus() {
		boolean success = false;
		
		try{
			dataOut.writeByte(GET_CAGE_STATUS);
			dataOut.flush();
			success = dataIn.readBoolean();
		}
		catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}
		
		return success;
	}
	
	/**
	 * Sends the signal to make the slave brick "tap" the brick. It then returns a boolean if it was successful or not.
	 *
	 * @return Returns true if the tap was successful and false if it wasn't. 
	 */				
	public boolean tap() {
		
		boolean success = false;
		
		try{
			dataOut.writeByte(TAP);
			dataOut.flush();
			success = dataIn.readBoolean();
		}
		catch(IOException ioe) {
			LCD.clear();
			LCD.drawString("IOError: "+ ioe.toString(), 0, 0);
		}
		
		return success;
	}
	
	/**
	 * Method to close the bluetooth connection properly. 
	 * @return Returns true if the connection has closed, false otherwise.
	 */	
	public boolean disconnect() {
		
		try {
			dataOut.writeByte(DISCONNECT);
			dataOut.flush();
			connected = !dataIn.readBoolean();
			
			dataIn.close();
			dataOut.close();
			connection.close();			
		}
		catch (IOException ioe) {
			System.out.println(" I/O Error: " + ioe);
		}
		return connected;
		
	}
	
}