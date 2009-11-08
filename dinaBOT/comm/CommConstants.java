package dinaBOT.comm;

/**
 * This interface is a collection of static final constants which are shared by the various communication systems on the robot.
 *
*/
public interface CommConstants {

	/* -- Brick Names -- */
	
	public static final String SLAVE_NAME = "DinaBOTslave";
	public static final String MASTER_NAME = "DinaBOTmaster";

	/* -- Protocol Constants -- */
	public static final byte DO_NOTHING = 0;
	public static final byte PICKUP = 1;
	public static final byte OPEN_CAGE = 2;
	public static final byte CLOSE_CAGE = 3;
	

}