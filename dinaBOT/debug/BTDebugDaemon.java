
package dinaBOT.debug;

import dinaBOT.navigation.Odometer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.BTConnection;
import java.io.IOException;
import lejos.nxt.Battery;

/**
 * The Bluetooth Debugging Daemon for the penultimate debug console i/o
 *
 * @author Severin Smith
 * @version 1
*/
public class BTDebugDaemon implements Runnable {

	/* Protocol Constants */

	static final int ERROR = -1;

	static final int PRINT = 0;
	static final int UPDATE = 1;
	static final int PROMPT = 2;
	static final int QUERY = 3;

	static final int PROMPT_RESPONSE = 3;
	static final int QUERY_RESPONSE = 3;
	
	/* Vars */
	
	boolean connected; //Indicates connection status
	boolean running; //Controls thread execution
	
	Thread debug_daemon_thread; //Maintain connection, update odometry and battery levels
	
	BTConnection connection; //Actual BT Connection
	
	//I/O Streams
	DataInputStream input_stream;
	DataOutputStream output_stream;
	
	Odometer odometer; //Attached odometer if any
	
	/**
	 * Construct a new BTDebugDaemon. There should only exist one static BTDebugDaemon in the {@link Debug} class. For this reason the constructor is protected.
	 *
	*/
	protected BTDebugDaemon() {
		connected = false;
		running = false;
	}
	
	/**
	 * Start the underlying thread that manages the connection and blocks for <code>timeout</code> ms waiting for the connection to establish.
	 *
	 * @param timeout the time to wait for a connection to establish before returning
	 * @return true if and only if a connection was successfully established before the timeout was reached
	 * @see #stop()
	*/
	boolean start(int timeout) {
		if(!running) {
			debug_daemon_thread = new Thread(this);
			debug_daemon_thread.setDaemon(true);
			debug_daemon_thread.start();
		}
		if(timeout == 0) return false;
		else {
			long start_time = System.currentTimeMillis();
			while(System.currentTimeMillis()-start_time < timeout) {
				if(connected) return true;
				Thread.yield();
			}
			return false;
		}
	}
	
	/**
	 * Stop the underlying thread that manages the connection and closes the BT connection
	 *
	 * @see #stop(int timeout)
	*/
	void stop() {
		running = false;
		closeConnection();
	}
	
	/**
	 * Register an odometer to regularly update the robot position on the penultimate debug console. Only one odometer may be register at any given time. Registering a new odometer overwrites the first.
	 *
	 * @param odometer the odometer to register
	*/
	void registerOdometer(Odometer odometer) {
		this.odometer = odometer;
	}
	
	/**
	 * Print a string to the penultimate debug console
	 *
	 * @param s the string to be printed
	 * @see #println(String s)
	*/
	synchronized void print(String s) {
		if(connected) {
			try {
				output_stream.writeInt(PRINT);
				output_stream.writeInt(s.length());
				output_stream.writeChars(s);
			} catch(IOException e) {
				//The stream seems to have died, close up and reopen it
				closeConnection();
			}
		}
	}
	
	/**
	 * Print a string to the penultimate debug console followed by a new line
	 *
	 * @param s the string to be printed
	 * @see #print(String s)
	*/
	synchronized void println(String s) {
		if(connected) {
			try {
				output_stream.writeInt(PRINT);
				output_stream.writeInt(s.length()+1);
				output_stream.writeChars(s+"\n");
			} catch(IOException e) {
				//The stream seems to have died, close up and reopen it
				closeConnection();
			}
		}
	}
	
	/**
	 * Prompts the user via the penultimate debug console to answer a boolean value question.
	 * <p>
	 * If there is currently no connected user of if there is connection error, the function returns true by default. This behavior allows code execution to continue unhinderd when the penultimate debug is disconnected from the robot.
	 *
	 * @param s the question to prompt the user with
	 * @return the boolean valued answer to the question, true by default (no connected user/connection error)
	 * @see #query(String s)
	*/
	boolean prompt(String s) {
		if(connected) {
			try {
				synchronized(this) {
					output_stream.writeInt(PROMPT);
					output_stream.writeInt(s.length());
					output_stream.writeChars(s);	
				}
		
				if(input_stream.readInt() == PROMPT_RESPONSE) return input_stream.readBoolean();
				else input_stream.skip(input_stream.available());
			} catch(IOException e) {
				//The stream seems to have died, close up and reopen it
				closeConnection();
			}
		}
		return true;
	}
	
	/**
	 * Prompts the user via the penultimate debug console to answer a String value question (this string could obviously also be evaluated to it's numerical value for number value questions).
	 * <p>
	 * If there is currently no connected user of if there is connection error, the function returns null.
	 *
	 * @param s the question to prompt the user with
	 * @return the String valued answer to the question, null if there is no connected user or a connection error
	 * @see #query(String s)
	*/
	String query(String s) {
		if(connected) {
			try {
				synchronized(this) {
					output_stream.writeInt(QUERY_RESPONSE);
					output_stream.writeInt(s.length());
					output_stream.writeChars(s);			
				}
		
				if(input_stream.readInt() == QUERY_RESPONSE) {
					int len = input_stream.readInt();
					String response = "";
					for(int i = 0;i < len;i++) {
						response += input_stream.readChar();
					}
					return response;
				}
				else input_stream.skip(input_stream.available());
			} catch(IOException e) {
				//The stream seems to have died, close up and reopen it
				closeConnection();
			}
		}
		return null;
	}
	
	/**
	 * The run method which is the target of the underlying thread that manages the connection and updates.
	 *
	*/
	public void run() {
		double[] position = new double[3];
		while(running) {
			if(!connected) {
				connection = Bluetooth.waitForConnection(1000, 0);
				if(connection != null) {
					input_stream = connection.openDataInputStream();
					output_stream = connection.openDataOutputStream();
					connected = true;
				}
			} else {
				try {
					synchronized(this) {
						output_stream.writeInt(UPDATE);
						output_stream.writeInt(Battery.getVoltageMilliVolt());
						if(odometer != null) odometer.getPosition(position);
						output_stream.writeDouble(position[0]);
						output_stream.writeDouble(position[1]);
						output_stream.writeDouble(position[2]);
					}
					try {
						Thread.sleep(500);
					} catch(InterruptedException e) {
						
					}
				} catch(IOException e) {
					//The stream seems to have died, close up ...
					closeConnection();
				}
			}
		}
	}
	
	/**
	 * Close all aspects of the bluetooth connection, ignore errors
	 *
	*/
	void closeConnection() {
		try {
			input_stream.close();
		} catch(IOException e) {
			
		}

		try {
			output_stream.close();
		} catch(IOException e) {
			
		}

		connection.close();

		connected = false;
	}
	
}