package dinaBOT.mech;

import lejos.nxt.*;

public class StackTest {

	public static void main(String[] args){
		
		Stacker stacker = new Stacker(Motor.A, Motor.B, Motor.C);
		
		
		Button.waitForPress();
		
		while(true){
			
			if(Button.ENTER.isPressed()){
				stacker.activateMechanicalClaw();
				Button.waitForPress();}
			if(Button.ESCAPE.isPressed()){
				stacker.openDockingBay();
				Button.waitForPress();
				stacker.closeDockingBay();
			}
		}		
		
	}
}