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
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
 */

public class BTslave {
	
	public static final byte DO_NOTHING = 0;
	public static final byte PICKUP = 1;
	public static final byte OPEN_CAGE = 2;
	public static final byte CLOSE_CAGE = 3;
	
	public DataInputStream dataIn;
	public DataOutputStream dataOut;
	public BTConnection connection;
	
	
	/**
	 * This constructor merely instantiates. All the connections are done in the connect() method.
	 * @see #BTslave()
	 */
	public BTslave() {
		
	}
	
	/**
	 * The waitForConnection() method should be called when you want the slave brick to wait for an initiated connection from the master brick.  
	 * for a connection or the connection will fail. The method will establish a bluetooth connection and establish the input and output streams  
	 * on the slave side of the connection. 
	 * @see #waitForConnection()
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
	 * @see #waitForCommand()
	 * @return returns the instruction sent by the master brick
	 */	
	public byte waitForCommand() {
		
		byte nextInstruction = 0;
		
		try {
			nextInstruction = dataIn.readByte();
		}
		catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("Error reading next command from master: \n"+ioe.toString(), 0, 0);
		}
		
		return nextInstruction;
		
	}
	
	/**
	 * The sendStatus() method is used to send success or failure signals to the master brick.  
	 * @see #sendStatus()
	 */		
	public void sendStatus(boolean success) {
		
		try {
			dataOut.writeBoolean(success);
			dataOut.flush();
		}
		catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("Error sending success status: \n"+ioe.toString(), 0, 0);
		}
		
	}
	
	/**
	 * The closeConnection() closes the data streams and the bluetooth connection.  
	 * @see #closeConnection()
	 */	
	public void closeConnection() {
		
		try {
			dataIn.close();
			dataOut.close();
			connection.close();			
		}
		catch (IOException ioe) {
			System.out.println(" Error closing connection " + ioe);
		}
		
	}

}
