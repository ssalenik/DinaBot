package dinaBOT.comm;

import java.io.*;
import javax.bluetooth.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * The BTSlave class is the class that handles the communication with the master brick. By default, it is always the initiated of the bluetooth
 * connection. When connected, it will establish an data input stream to receive information about the commands it needs to do, and it will establish
 * a data output stream to send information about the success of the methods it has initiated.
 *
 * @author Alexandre Courtemanche
*/
public class BTSlave implements CommConstants {
		
	public DataInputStream dataIn;
	public DataOutputStream dataOut;
	public BTConnection connection;
	public boolean connected;
	
	
	/**
	 * This constructor merely instantiates. All the connections are done in the connect() method.
	 *
	*/
	public BTSlave() {
		
	}
	
	/**
	 * This method returns the status of the connection
	 *
	 * @return true if there is a connection and returns false otherwise.
	*/
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * The waitForConnection() method should be called when you want the slave brick to wait for an initiated connection from the master brick.  
	 * for a connection or the connection will fail. The method will establish a bluetooth connection and establish the input and output streams  
	 * on the slave side of the connection. 
	 *
	*/	
	public void waitForConnection() {
		LCD.clear();
		LCD.drawString("Waiting for Connection",0, 0);
		
		BTConnection connection = Bluetooth.waitForConnection();
		
		dataIn = connection.openDataInputStream();
		dataOut = connection.openDataOutputStream();

		LCD.clear();
		LCD.drawString("Connected",0, 0);
		
		connected = true;
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
		} catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("Error reading next command from master: \n"+ioe.toString(), 0, 0);
		}
		
		return nextInstruction;
	}
	
	/**
	 * The sendStatus() method is used to send success or failure signals to the master brick.
	 *
	 * @param success the boolean to return as the send status  
	*/		
	public void sendStatus(boolean success) {
		
		try {
			dataOut.writeBoolean(success);
			dataOut.flush();
		} catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("Error sending success status: \n"+ioe.toString(), 0, 0);
		}
		
	}
	
	/**
	 * The disconnectFlag() method changes the connection boolean variable to false.  
	 *
	*/	
	public void disconnectFlag() {
		connected = false;
	}
	
}
