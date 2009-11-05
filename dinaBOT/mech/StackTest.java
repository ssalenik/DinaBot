package dinaBOT.mech;

import lejos.nxt.*;

public class StackTest {

	public static void main(String[] args){
		
		Stacker stacker = new Stacker(Motor.A, Motor.B, Motor.C);
		
		
		Button.waitForPress();
		
		stacker.activateMechanicalClaw();
		Button.waitForPress();
		stacker.openDockingBay();
		Button.waitForPress();
		stacker.closeDockingBay();
		Button.waitForPress();
	}
}