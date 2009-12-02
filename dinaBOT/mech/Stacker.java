package dinaBOT.mech;

import lejos.nxt.Motor;

/**
 * This is the class for all the methods that control the stacking processes which use the claw and cage.
 *
 * @author Gabriel Olteanu, François Ouellet Delorme, Severin Smith
 * @see Stacking
 * @version 2
*/
public class Stacker implements Stacking {

	Motor leftGate;
	Motor rightGate;
	Motor claw;

	//in degrees
	final int clawOpenAngle = 85;
	final int clawClosedAngle = 20;
	final int clawPickupAngle = -130;
	final int clawRaiseAngle = -165;

	final int gatesRotation = 110;
	final int gatesPickUpRotation = 30;	//doesn't do anything!!!

	final int gateSpeed = 175;
	final int clawSpeedLift = 95;
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

		claw.setSpeed(clawSpeed);
		claw.rotateTo(clawRaiseAngle);
		claw.rotateTo(clawPickupAngle);
	}

	/**
	 * This method executes the pickup mechanism
	 *
	 * @return true if the pickup succeeded.
	*/
	public boolean pickUp() {


		claw.setSpeed(clawSpeedLift);
		claw.rotateTo(clawPickupAngle);
		try {Thread.sleep(1000);} catch(Exception e) {}
		claw.stop();

		brickCount += 1;

		return true;
	}

	/**
	 * Moves the claw to the hold position (straight)
	 *
	 * @return true if the hold succeeded.
	 *
	*/
	public boolean hold() {

		claw.setSpeed(clawSpeed);
		claw.rotateTo(clawClosedAngle);
		claw.stop();

		return true;

	}

	/**
	 * Moves the claw to the release position (open)
	 *
	 * @return true if the opening succeeded.
	 *
	*/
	public boolean release() {

		claw.setSpeed(clawSpeed);
		claw.rotateTo(clawOpenAngle);
		claw.stop();

		return true;

	}

	/**
	 * Moves the claw to the hold position and then to the open position. This "taps" the the brick and hopefully straightens it.
	 *
	 * @return true if the tap succeeded.
	 *
	*/
	public boolean tap() {
		hold();
		release();

		return true;
	}

	/**
	 * Moves the claw to arms up position
	 *
	 * @return true if the tap succeeded.
	 *
	*/
	public boolean armsUp() {

		claw.setSpeed(clawSpeed);
		claw.rotateTo(clawRaiseAngle);
		claw.stop();
		try {
			Thread.sleep(500);
		} catch(Exception e) {

		}
		claw.setSpeed(clawSpeedLift);
		claw.rotateTo(clawPickupAngle);
		claw.stop();

		return true;
	}

	/**
	 * Moves the claw to the closed or zero tacho point. (Mainly used to reset the claw to zero before shutdown).
	 *
	 * @return true if the closing succeeded.
	 *
	*/
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

		leftGate.rotateTo(gatesRotation, true);
		rightGate.rotateTo(gatesRotation);

		leftGate.stop();
		rightGate.stop();
	}

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
	 * Checks wether the cage doors are closed or open
	 *
	 * @return true if the cage is closed and false otherwise
	*/
	public boolean getCageStatus() {
		//checks if the cage is open
		if(leftGate.getTachoCount() > 5 || rightGate.getTachoCount() > 5) return false;//cage is open
		else return true;//cage is closed
	}

}
