package dinaBOT;

import lejos.nxt.Motor;
import dinaBOT.debug.*;
import dinaBOT.navigation.*;

/**
 * The DinaBOT class is the central class of our project. It ties everything togethere. It <b>is</b> the robot.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTMaster {

	/**
	 * This is where the static main method lies. This is where execution begins.
	 *
	 * @param args This is the command line args, this is irrelevent in the NXT
	*/
	public static void main(String[] args) {		
		
		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster();
				
		while(true);
	}
	
	/**
	 * This is our Hello World method. It will be gone soon
	 *
	*/
	public void helloWorld() {
		System.out.println("Hello World");
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);
		Motor.A.rotate(1044, true);
		Motor.B.rotate(-1044);
		System.out.print("Goodbye World");
	}
	
}