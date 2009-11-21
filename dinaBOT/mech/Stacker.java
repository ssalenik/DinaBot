package dinaBOT.mech;

import lejos.nxt.Motor;

/**
 * This is the class for all the methods that control the stacking processes which use the claw and cage.
 *
 * @author Gabriel Olteanu, François Ouellet Delorme
 * @see Stacking
 * @version 2
*/
public class Stacker implements Stacking {

	Motor leftGate;
	Motor rightGate;
	Motor claw;

	//in degrees
	final int clawOpenAngle = 95;
	final int clawClosedAngle = 20;
	final int clawTopAngle = -200;

	final int gatesRotation = 110;
	final int gatesPickUpRotation = 40;

	final int gateSpeed = 175;
	final int clawSpeed = 200;

	int brickCount = 0;

	/**
	 * Constructor for the Stacker - it takes 3 motors as parameters
	 *
	 * @param leftGate motor corresponding to left gate
	 * @param rightGate motor corresponding to right gate
	 * @param claw motor corresponding to claw
	*/
	public Stacker(Motor leftGate, Motor rightGate, Motor claw) {
		this.leftGate = leftGate;
		this.rightGate = rightGate;
		this.claw = claw;

		leftGate.resetTachoCount();
		rightGate.resetTachoCount();
		claw.resetTachoCount();
		
		claw.rotateTo(clawOpenAngle);
	}

	/**
	 * This method executes the pickup mechanism
	 *
	 * @return true if the pickup succeeded.
	*/
	public boolean pickUp() {

		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);
		claw.setSpeed(clawSpeed);

		leftGate.resetTachoCount();
		rightGate.resetTachoCount();

		if(brickCount < 2) {
			leftGate.rotate(gatesPickUpRotation, true);
			rightGate.rotate(gatesPickUpRotation);
		}

		claw.rotateTo(clawTopAngle);
		claw.stop();

		try {Thread.sleep(1000);} catch(Exception e) {}

		claw.rotateTo(clawOpenAngle);
		claw.flt();

		if(brickCount < 2) {
			leftGate.rotateTo(0, true);
			rightGate.rotateTo(0);
		}

		brickCount += 1;

		return true;
	}

	/**
	 * Attempts to align the block at right angles from the robot by closing the claw on it.
	 *
	 * @return true if the closing succeeded.
	 *
	*/
	public boolean hold() {

		claw.setSpeed(clawSpeed);
		claw.rotateTo(clawClosedAngle);
		claw.stop();

		return true;

	}

	/**
	 * Assumes the claw is touching the block and opens the claw to release the block.
	 *
	 * @return true if the opening succeeded.
	 *
	*/
	public boolean release() {

		claw.setSpeed(clawSpeed);
		claw.rotateTo(clawOpenAngle);
		claw.flt();

		return true;

	}


	public boolean tap() {
		hold();
		release();

		return true;
	}
	
	public boolean close() {
		claw.setSpeed(clawSpeed);
		claw.rotateTo(0);
		claw.stop();

		return true;
	
	}

	/**
	 * Opens the cage doors
	 *
	*/
	public void openCage() {

		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.resetTachoCount();
		rightGate.resetTachoCount();

		leftGate.rotate(gatesRotation, true);
		rightGate.rotate(gatesRotation);

	}

	//implement a method to make the robot move forward to get rid of the blocks
	//to be implemented in master via BTB communication

	/**
	 * Closes the cage doors
	 *
	*/
	public void closeCage() {

		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.rotateTo(0, true);
		rightGate.rotateTo(0);

		leftGate.stop();
		rightGate.stop();

		brickCount = 0;

	}

	/**
	 * Checks whether the cage doors are closed or open
	 *
	 * @return true if the cage is closed and false otherwise
	*/
	public boolean getCageStatus() {
		//checks if the cage is open
		if(leftGate.getTachoCount() > 0 || rightGate.getTachoCount() > 0) return false;//cage is open
		else return true;//cage is closed
	}

}