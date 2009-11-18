package dinaBOT.mech;

import lejos.nxt.Motor;

/**
 * This class defines a claw object and all the procedures that can be performed by the it.
 *
 * @author François Ouellet Delorme
 * @version 1
*/
public class Claw {
	
	Motor claw;
		
	final double gearRatio = 0.6;
	final int clawSpeed = 175;	
	
	/**	
	 * Creates a Claw instance using the specified motor.
	 * 
	 * @param claw the claw's motor
	*/
	public Claw(Motor claw){
		this.claw = claw;
	}
	
	
	/**	
	 * Opens the claw by the specified angle
	 * 
	 * @param angle angle by which the claw should open
	 * 
	*/
	public void open(int angle){
		claw.setSpeed(clawSpeed);
		claw.rotate((int) (angle * gearRatio));
	}
	
	/**	
	 * Drops the claw by the specified angle
	 * 
	 * @param angle angle by which the claw should be dropped
	 * 
	*/
	public void drop(int angle){
		claw.setSpeed(clawSpeed);
		claw.rotate(angle);
	}
	
	/**	
	 * Closes the claw by the specified angle
	 * 
	 * @param angle angle by which claw should close
	 * 
	*/
	public void close(int angle){
		claw.setSpeed(clawSpeed);
		claw.rotate((int)(-angle * gearRatio));
	}
	
	/**	
	 * Lifts the claw by the specified angle
	 * 
	 * @param angle angle by which the claw should be lifted
	 * 
	*/
	public void lift(int angle){
		claw.setSpeed(clawSpeed);
		claw.rotate(-angle);
	}
	
	/**	
	 * Extension of the Motor.stop() method
	 * 
	*/
	public void stop(){
		claw.stop();
	}
	
	/**	
	 * Extension of the Motor.flt() method
	 * 
	*/
	public void flt(){
		claw.flt();
	}
	
	/**	
	 * Opens the claw completely and sets its tachometer to 0
	 * 
	*/
	public void reset(){
		claw.setSpeed(clawSpeed);
		claw.rotateTo(0);
		open(45);
		claw.resetTachoCount();
	}
}
