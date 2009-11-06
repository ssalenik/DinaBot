import java.io.*;
import javax.bluetooth.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BTmaster {
	
	public static final String SLAVE_NAME = "DinaBOTslave";
	public static final byte DO_NOTHING = 0;
	public static final byte PICKUP = 1;
	public static final byte OPEN_CAGE = 2;
	public static final byte CLOSE_CAGE = 3;

	public BTConnection connection;
	public RemoteDevice btrd;
	public DataInputStream dataIn;
	public DataOutputStream dataOut;
	
	public BTmaster() {
		
		btrd = Bluetooth.getKnownDevice(SLAVE_NAME);
		
		if (btrd == null) {
			LCD.clear();
			LCD.drawString("No such device", 0, 0);
			Button.waitForPress();
			System.exit(1);
		}		
		
		connection = Bluetooth.connect(btrd);
		
		if (connection == null) {
			LCD.clear();
			LCD.drawString("Connect fail", 0, 0);
			Button.waitForPress();
			System.exit(1);
		}
		
		LCD.clear();
		LCD.drawString("Connected", 0, 0);
		
		dataIn = connection.openDataInputStream();
		dataOut = connection.openDataOutputStream();
		
	}
	
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
	
	public void closeConnection() {
		
		try {
			dataIn.close();
			dataOut.close();
			connection.close();			
		}
		catch (IOException ioe) {
			System.out.println(" I/O Error: " + ioe);
		}
		
	}
	
	
}
