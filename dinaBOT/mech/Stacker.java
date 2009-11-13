package dinaBOT.mech;

import lejos.nxt.Motor;

/**
 * Description here
 *
 * @author Gabriel Olteanu
 * @see Stacking
 * @version 1
*/
public class Stacker implements Stacking {

	Motor claw;
	Motor leftGate;
	Motor rightGate;

	final int gatesRotation = 110;
	final int gatesPickUpRotation = 50;
	final int clawRotation = -250;
	
	final int clawSpeed = 175;
	final int gateSpeed = 175;

	
	int brickCount = 0;

	/**	Constructor for the Stacker - it takes 3 motors are parameters
	 * 
	 * @param leftGate motor corresponding to left gate
	 * @param rightGate motor corresponding to right gate
	 * @param claw motor corresponding to claw
	 */
	public Stacker(Motor leftGate, Motor rightGate, Motor claw){
		this.leftGate = leftGate;
		this.rightGate = rightGate;
		this.claw = claw;
	}
	
	/**	This method executes the pickup mechanism
	 * 
	 */
	public boolean activateMechanicalClaw() {

		claw.setSpeed(clawSpeed);
		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);

		leftGate.resetTachoCount();
		rightGate.resetTachoCount();

		if(brickCount < 2) {
			leftGate.rotate(gatesPickUpRotation-10, true);
			rightGate.rotate(gatesPickUpRotation-10);
		}
		
		claw.rotateTo(clawRotation);
		claw.stop();

		try{
			Thread.sleep(1000);
		} catch(Exception e) {

		} 

		claw.rotateTo(0);

		claw.flt();

		if(brickCount < 2) {
			leftGate.rotateTo(0, true);
			rightGate.rotateTo(0);
		}

		brickCount += 1;

		return true;
	}
	
	/**	Closes the claws on the block to align it
	 * 
	 */
	public boolean tap() {
		claw.setSpeed(clawSpeed);
		claw.rotateTo(clawRotation + 165);
		claw.stop();
		try {Thread.sleep(500);} catch(Exception e) {}
		claw.rotateTo(0);
		claw.flt();
		return true;
	}

	/**	Opens the cage doors
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

	/**	Closes the cage doors
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
	
	/**	return true if the cage is closed and false otherwise
	 * 
	 */
	public boolean getDockStatus() {
		//checks if the cage is open
		if(leftGate.getTachoCount() > 0 || rightGate.getTachoCount() > 0) return false;//cage is open
		else return true;//cage is closed
	}

}