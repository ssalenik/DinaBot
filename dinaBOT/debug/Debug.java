package dinaBOT.debug;

import dinaBOT.navigation.Odometer;
import dinaBOT.debug.BTDebugDaemon;

/**
 * This Debug class, serves as a universally accessible (public and static) wrapper for bluetooth debugging.
 * <p>
 * It is designed to be used in conjuction with the penultimate Bluetooth debugging console.
 * <p> 
 * By default the Debug system is not active. It must be turned on using the {@link #start()} method. All methods of the Debug class may still be called while it is not active, but they will simply return their default values.
 *
 * @author Severin Smith
 * @version 1
*/
public class Debug {

	static BTDebugDaemon debug_daemon = new BTDebugDaemon();

	/**
	 * The Debug class is only a wrapper for a collection of static methods, for this reason it should never and can never (private construtor) be instantiated.
	 *
	*/
	private Debug() {

	}

	/**
	 * Print a string to the penultimate debug console
	 *
	 * @param s the string to be printed
	 * @see #println(String s)
	*/
	public static void print(String s) {
		debug_daemon.print(s);
	}

	/**
	 * Print a string to the penultimate debug console followed by a new line
	 *
	 * @param s the string to be printed
	 * @see #print(String s)
	*/
	public static synchronized void println(String s) {
		debug_daemon.println(s);
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
	public static boolean prompt(String s) {
		return debug_daemon.prompt(s);
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
	public static String query(String s) {
		return debug_daemon.query(s);
	}

	/**
	 * Starts the underlying {@link BTDebugDaemon}. This daemon established connection to the penultimate debug console and handles the communication details.
	 *
	 * @see #start(int timeout)
	 * @see #stop()
	*/
	public static void start() {
		debug_daemon.start(0);
	}

	/**
	 * Starts the underlying {@link BTDebugDaemon}.This daemon established connection to the penultimate debug console and handles the communication details.
	 * <p>
	 * This method will block until either a connection is established or the timeout is exceeded. 
	 * 
	 * @param timeout the maximum timeout in ms to wait for a connection to establish.
	 * @return indicates true if the method returned because of a successfull connection and false if it returned because of a timeout.
	 * @see #start()
	 * @see #stop()
	*/
	public static boolean start(int timeout) {
		return debug_daemon.start(timeout);
	}

	/**
	 * Stops the underlying {@link BTDebugDaemon}.
	 *
	 * @see #start()
	 * @see #start(int timeout)
	*/
	public static void stop() {
		debug_daemon.stop();
	}

	/**
	 * Register an odometer to regularly update the robot position on the penultimate debug console. Only one odometer may be register at any given time. Registering a new odometer overwrites the first.
	 *
	 * @param odometer the odometer to register
	*/
	public static void registerOdometer(Odometer odometer) {
		debug_daemon.registerOdometer(odometer);
	}

}
