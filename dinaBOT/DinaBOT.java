package dinaBOT;

import lejos.nxt.Motor;
import dinaBOT.debug.*;
import dinaBOT.navigation.*;

/**
 * The DinaBOT class is the central class of our project. It ties everything togethere. It <b>is</b> the robot.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOT {

	/**
	 * This is where the static main method lies. This is where execution begins.
	 *
	 * @param args This is the command line args, this is irrelevent in the NXT
	*/
	public static void main(String[] args) {
		ArcOdometer odometer = new ArcOdometer(Motor.A, Motor.B);
		
		Debug.registerOdometer(odometer);
		Debug.start(10000);
		
		Debug.println("Line 1");
		Debug.print("Line 2");
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
		Debug.println(" Line 2 continued");
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
		System.out.println(Debug.query("A Query !"));
		
		DinaBOT dinaBOT = new DinaBOT();
		
		if(Debug.prompt("A Prompt !")) dinaBOT.helloWorld();
		
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