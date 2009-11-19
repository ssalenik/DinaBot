package dinaBOT.mech;

import lejos.nxt.Motor;

public class CloseClaw {

	Motor claw;

	final int clawSpeed = 175;
	final int clawRotation = -70;

	public CloseClaw(Motor claw) {

		this.claw = claw;
	}

	public boolean close() {

		claw.setSpeed(clawSpeed);

		claw.rotateTo(clawRotation);
		claw.stop();
		
		claw.rotateTo(0);

		claw.flt();

		return true;
	}
}
