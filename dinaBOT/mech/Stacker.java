package dinaBOT.mech;

import lejos.nxt.Motor;
import dinaBOT.mech.*;

public class Stacker implements Stacking {


	Motor claw;
	Motor leftGate;
	Motor rightGate;

	final int gatesRotation = 110;
	final int gatesPickUpRotation = 50;
	final int clawRotation = -250;

	final int clawSpeed = 175;
	final int gateSpeed = 175;

	public Stacker(Motor leftGate, Motor rightGate, Motor claw){

		this.leftGate = leftGate;
		this.rightGate = rightGate;
		this.claw = claw;			
	}

	public boolean activateMechanicalClaw() {
				
		claw.setSpeed(clawSpeed);		
		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.resetTachoCount();
		rightGate.resetTachoCount();

		leftGate.rotate(gatesPickUpRotation-15, true);
		rightGate.rotate(gatesPickUpRotation-15);

		claw.rotateTo(clawRotation);	
		claw.stop();
		
        try{
        Thread.sleep(1000);
        } catch (Exception e) {}        

		claw.rotateTo(0);

		claw.flt();
		
		leftGate.rotateTo(0, true);
		rightGate.rotateTo(0);

		return true;
	}

	public void openDockingBay() {

		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.resetTachoCount();
		rightGate.resetTachoCount();

		leftGate.rotate(gatesRotation, true);
		rightGate.rotate(gatesRotation);

	}

     //implement a method to make the robot move forward to get rid of the blocks
	//to be implemented in master via BTB communication

	public void closeDockingBay() {

		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.rotateTo(0, true);
		rightGate.rotateTo(0);

		leftGate.stop();
        rightGate.stop();

	}

	public boolean getDockStatus() {

		//checks if the cage is open
		if(leftGate.getTachoCount() > 0 || rightGate.getTachoCount() > 0)
			return false;// cage is open
		else
			return true;// cage is closed
	}



}