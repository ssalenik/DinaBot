package dinaBOT.mech;

import lejos.nxt.Motor;

public class BrickAlignment {

	static void align() {

		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);

		Motor.A.rotate(204, true);
		Motor.B.rotate(-204);

		//close claw


		Motor.A.setSpeed(300);
		Motor.B.setSpeed(300);

		Motor.A.rotate(720, true);
		Motor.B.rotate(720);
	}

	public static void main(String[] args) {

		align();
	}
}
