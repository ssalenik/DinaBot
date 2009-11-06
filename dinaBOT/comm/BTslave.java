import java.io.*;
import javax.bluetooth.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;


public class BTslave {
	
	public static final byte DO_NOTHING = 0;
	public static final byte PICKUP = 1;
	public static final byte OPEN_CAGE = 2;
	public static final byte CLOSE_CAGE = 3;
	
	public DataInputStream dataIn;
	public DataOutputStream dataOut;
	public BTConnection connection;
	
	public BTslave() {
		
		LCD.drawString("waiting for Connection",0, 0);
		BTConnection connection = Bluetooth.waitForConnection();
		LCD.clear();
		LCD.drawString("Connected",0, 0);
		
		dataIn = connection.openDataInputStream();
		dataOut = connection.openDataOutputStream();
		
	}
	
	public byte waitForCommand() {
		
		byte nextInstruction = DO_NOTHING;
		
		try {
			nextInstruction = dataIn.readByte();
		}
		catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("Error reading next command from master: "+ioe.toString(), 0, 0);
		}
		
		return nextInstruction;
		
	}
	
	public void sendStatus(boolean success) {
		
		try {
			dataOut.writeBoolean(success);
			dataOut.flush();
		}
		catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("Error sending status signal", 0, 0);
		}
		
	}
	
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
