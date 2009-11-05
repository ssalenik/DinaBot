package dinaBOT;

import lejos.nxt.Motor;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import dinaBOT.navigation.*;

/**
 * The DinaBOT class is the central class of our project. It ties everything togethere. It <b>is</b> the robot.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTMaster {
	
	public static double UNIT_TILE = 30.48;
	
	Motor left_motor = Motor.A;
	Motor right_motor = Motor.B;
	
	Odometer odometer;
	Movement movement;
	
	public DinaBOTMaster() {
		odometer = new ArcOdometer(left_motor, right_motor);
		movement = new BasicNavigator(odometer, left_motor, right_motor);
	}
	
	/**
	 * This is our win method. It will be gone soon
	 *
	*/
	public void win() {
		System.out.println("Hello World");
		odometer.setDebug(true);
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			
		}
		for(int i = 0;i < 4*8;i++) {
			movement.goForward(UNIT_TILE*2, 150);
			movement.turnTo(Math.PI/2*(i+1), 150);
		}
	}
	
	/**
	 * This is where the static main method lies. This is where execution begins.
	 *
	 * @param args This is the command line args, this is irrelevent in the NXT
	*/
	public static void main(String[] args) {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				System.exit(0);
			} 
			
			public void buttonReleased(Button b) {
				System.exit(0);
			}
		});
		
		DinaBOTMaster dinaBOTmaster = new DinaBOTMaster();
		
		dinaBOTmaster.win();
		
		while(true);
	}
	
}