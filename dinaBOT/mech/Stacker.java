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
	Claw claw;
	
	//in degrees
	final int clawOpenAngle = 30;
	final int clawClosedAngle = 17;
	
	final int gatesRotation = 110;
	final int gatesPickUpRotation = 50;

	final int gateSpeed = 175;
	
	int brickCount = 0;

	/**	
	 * Constructor for the Stacker - it takes 3 motors as parameters
	 * 
	 * @param leftGate motor corresponding to left gate
	 * @param rightGate motor corresponding to right gate
	 * @param claw motor corresponding to claw
	 */
	public Stacker(Motor leftGate, Motor rightGate, Motor clawMotor){
		this.leftGate = leftGate;
		this.rightGate = rightGate;
		this.claw = new Claw(clawMotor);
	}
	
	/**	
	 * This method executes the pickup mechanism
	 * 
	 * @return true if the pickup succeeded.
	 */
	public boolean activateMechanicalClaw() {

		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.resetTachoCount();
		rightGate.resetTachoCount();

		if(brickCount < 2) {
			leftGate.rotate(gatesPickUpRotation/(brickCount+1), true);
			rightGate.rotate(gatesPickUpRotation/(brickCount+1));
		}
		
		claw.close(clawOpenAngle);
		claw.lift(190);
		claw.stop();

		try {Thread.sleep(1000);} catch(Exception e) {} 

		claw.reset();
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
	public boolean touch() {
		
		claw.reset();

		claw.close(clawOpenAngle);
		claw.stop();
		
		try {Thread.sleep(500);} catch(Exception e) {}
		
		return true;
		
	}
	
	/**	
	 * Assumes the claw is touching the block and opens the claw to release the block.
	 * 
	 * @return true if the opening succeeded.
	 * 
	 */
	public boolean untouch() {
		
		claw.reset();
		claw.flt();
		
		try {Thread.sleep(500);} catch(Exception e) {}
		
		return true;
		
	}

	/**	
	 * Opens the cage doors
	 * 
	 */
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

	/**	
	 * Closes the cage doors
	 * 
	 */
	public void closeDockingBay() {

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
	public boolean getDockStatus() {
		//checks if the cage is open
		if(leftGate.getTachoCount() > 0 || rightGate.getTachoCount() > 0) return false;//cage is open
		else return true;//cage is closed
	}

}