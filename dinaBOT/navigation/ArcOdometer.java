package dinaBOT.navigation;

import java.lang.Math;

import lejos.robotics.Encoder;
import lejos.nxt.LCD;

import dinaBOT.navigation.Odometer;

public class ArcOdometer implements Odometer {

	/* -- Constants -- */
	
	//Precise wheel radius and wheel base computed from Lab 1 and other experiments
	static final double wheel_radius = 2.747;
	static final double wheel_base = 15.78;
	
	/* -- Variables -- */

	Encoder left_encoder, right_encoder;

	double tacho_left, tacho_right;
	
	double[] position;
	
	boolean debug;
	
	public ArcOdometer(Encoder left_encoder, Encoder right_encoder) {
		this.left_encoder = left_encoder;
		this.right_encoder = right_encoder;
		
		left_encoder.resetTachoCount();
		right_encoder.resetTachoCount();
		
		position = new double[3];
		
		Thread odometer_thread = new Thread(this);
		odometer_thread.setDaemon(true);
		odometer_thread.start();		
	}
	
	public void run() {
		while(true) { //Forever
			//Compute the change in tacho count (in radians)
			double d_tacho_left = (double)left_encoder.getTachoCount()/360*(2*Math.PI)-tacho_left; 
			double d_tacho_right = (double)right_encoder.getTachoCount()/360*(2*Math.PI)-tacho_right;
		
			double dC = (d_tacho_right*wheel_radius+d_tacho_left*wheel_radius)/2; //Compute the arc length travelled
			double dTheta = (d_tacho_right*wheel_radius-d_tacho_left*wheel_radius)/wheel_base; //Compute the change in angle
			
			//We are going to recursively modify the position array, we don't want it to be modified while we're doing so
			//So enter a synchronized block
			synchronized(this) {
				position[0] += dC*cos(position[2]+dTheta/2); //Compute the new X position
				position[1] += dC*sin(position[2]+dTheta/2); //Compute the new Y position
				position[2] += dTheta; //Compute the new angle
			}
			//Update the latest left and right tacho counts
			tacho_left += d_tacho_left;
			tacho_right += d_tacho_right;
		}
	}
	
	/**
	 * This method isn't synchronized by design. There is a tiny 
	 * risk that the position array will get updated while arraycopy
	 * is running (this is HIGHLY unlikley given that array copy is
	 * a native call, not a java call, so it probably executes in a
	 * single sweep everytime). But the normal updates from the run
	 * method make such tiny increments to the position array that it
	 * won't make much difference if a partially updated position array
	 * is returned . In contrast, if the method was synchronized
	 * we risk creating a serious bottle neck between this method and
	 * the run method should this method be repeatedly polled
	 * (which will probably happen). Such a bottle neck would significantly
	 * reduce the speed and therefor the accuracy of our odometer.
	*/
	public void getPosition(double[] position) throws IndexOutOfBoundsException, ArrayStoreException, NullPointerException {
		//This method throws standard array related exceptions if a malformed array is passed (wrong type, wrong size, etc)
		
		//Copy our current position array into the user's position array
		System.arraycopy(this.position, 0, position, 0, this.position.length);
	}
	
	public synchronized void setPosition(double[] position, boolean[] update) {
		//Make sure the position and update arrays are of accepatable sizes
		if(this.position.length == position.length && this.position.length == update.length) {	
			for(int i = 0;i < this.position.length;i++) {
				if(update[i]) this.position[i] = position[i]; //Update the necessary fields
			}
		}
	}

	//Place sin and cos is a special methods in case we want to reimplement them later to make our code faster
	
	double cos(double angle) {
		return Math.cos(angle);
	}
	
	double sin(double angle) {
		return Math.sin(angle);
	}

	public void setDebug(boolean state) {
		//Toggle the odometer printout thread for debugging
		if(!debug && state) { //If it's not already running and we want to activate it
			debug = true; //Set it's state to true
			//Instantiate a new thread whose target is an annonymous class implementing Runnable
			Thread debug_thred = new Thread(
				new Runnable() {
					public void run() {
						while(debug) { //As long as the debug state is true
							LCD.clear(); //Clear the screen
							//Print X,Y and Theta
							LCD.drawString("x = "+((Float)(float)position[0]).toString(),0,5);
							LCD.drawString("y = "+((Float)(float)position[1]).toString(),0,6);
							LCD.drawString("t = "+((Float)(float)(position[2]%(Math.PI*2)/(Math.PI*2)*360)).toString(),0,7);

							try {
								Thread.sleep(500); //Pause for 0.5 seconds
							} catch(Exception e) {

							}
						}
					}
				}
			);
			debug_thred.setDaemon(true); //Configure it as a daemon thread
			debug_thred.start(); //Start the thread
		} else { //If we want to deactivate it
			debug = false; //Set it's state to false
			//This will cause the run method of any running debug threads instantiated by this instance of ArcOdometer to return
		}
	}

}