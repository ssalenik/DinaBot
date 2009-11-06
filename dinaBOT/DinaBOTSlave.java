package dinaBOT;

import lejos.nxt.Motor;
import dinaBOT.mech.*;

/**
 * The DinaBOT class is the central class of our project. It ties everything togethere. It <b>is</b> the robot.
 *
 * @author Alexandre Courtemanche, Francois Ouellet Delorme, Gabriel Olteanu, Severin Smith, Stepan Salenikovich, Vinh Phong Buu
*/
public class DinaBOTSlave {
	Stacking stacker;
	
	public DinaBOTSlave() {
		stacker = new Stacker(Motor.A, Motor.B, Motor.C);
	}
	/**
	 * This is our Hello World method. It will be gone soon
	 *
	*/
	public void test() {
		stacker.activateMechanicalClaw();
		stacker.openDockingBay();
		stacker.getDockStatus();
		stacker.closeDockingBay();
		stacker.getDockStatus();
		
	}
	/**
	 * This is where the static main method lies. This is where execution begins.
	 *
	 * @param args This is the command line args, this is irrelevent in the NXT
	*/
	public static void main(String[] args) {		
		
		DinaBOTSlave dinaBOTslave = new DinaBOTSlave();
		dinaBOTslave.test();
		while(true);
	}
}