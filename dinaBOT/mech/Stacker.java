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
		this.clawRotation = -180;
		
		this.clawSpeed = 90;
		this.gateSpeed = 90;
	}
	
	
	
	
	public boolean pickUp() {
		
		claw.setSpeed(clawSpeed);
		claw.resetTachoCount();
		
		claw.rotateTo(clawRotation);
		
		claw.rotateTo(0);
		return true;		
	}
	
	public void openDockingBay() {
		
		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);
		
		leftGate.resetTachoCount();
		rightGate.resetTachoCount();
		
		leftGate.rotateTo(gatesRotation, true);
		rightGate.rotateTo(gatesRotation);
		
	}
	
	public void closeDockingBay() {
		
		leftGate.setSpeed(gateSpeed);
		rightGate.setSpeed(gateSpeed);
						
		leftGate.rotateTo(0, true);
		rightGate.rotateTo(0);
		
	}
	
	public boolean getCageStatus() {
		
		//checks if the cage is open
		if(leftGate.getTachoCount() > 0 || rightGate.getTachoCount() > 0)
			return false;// cage is open
		else
			return true;// cage is closed
	}
	
	
	
}