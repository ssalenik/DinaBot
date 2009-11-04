package dinaBOT.mech;

import lejos.nxt.Motor;
import dinaBOT.mech.*;

public class Stacker implements Stacking {


	Motor claw;
	Motor leftGate;
	Motor rightGate;

	final int gatesRotation;
	final int clawRotation;

	final int clawSpeed;
	final int gateSpeed;

	public Stacker(Motor leftGate, Motor rightGate, Motor claw){

		leftGate = this.leftGate;
		rightGate = this.rightGate;
		claw = this.claw;

		this.gatesRotation = 90;
		this.clawRotation = -250;

		this.clawSpeed = 175;
		this.gateSpeed = 175;
	}




	public boolean pickUp() {

		claw.setSpeed(clawSpeed);

		claw.rotateTo(clawRotation);

        try{
        Thread.sleep(1000);
        } catch (Exception e) {}

		claw.rotateTo(0);

		claw.flt();

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

     //implement a method to make the robot move forward to get rid of te blocks

	public void closeDockingBay() {

		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.rotateTo(0, true);
		rightGate.rotateTo(0);

		leftGate.stop();
        rightGate.stop();

	}

	public boolean getCageStatus() {

		//checks if the cage is open
		if(leftGate.getTachoCount() > 0 || rightGate.getTachoCount() > 0)
			return false;// cage is open
		else
			return true;// cage is closed
	}



}
