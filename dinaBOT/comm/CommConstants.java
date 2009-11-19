package dinaBOT.comm;

/**
 * This interface is a collection of static final constants which are shared by the various communication systems on the robot.
 *
*/
public interface CommConstants {

	/* -- Brick Names --*/

	public static final String SLAVE_NAME = "DinaBOTslave";
	public static final String MASTER_NAME = "DinaBOTmaster";

	/* -- Protocol Constants --*/
	public static final byte DO_NOTHING = 0;

	public static final byte OPEN_CAGE = 1;
	public static final byte CLOSE_CAGE = 2;
	public static final byte GET_CAGE_STATUS = 3;

	public static final byte PICKUP = 4;
	public static final byte TAP = 5;
	public static final byte HOLD = 6;
	public static final byte RELEASE = 7;

	public static final byte DISCONNECT = 8;

	/* -- Sound Constants -- */
	public static final byte PLAY_SONG = 10;
	public static final byte PAUSE_SONG = 11;
	public static final byte ABORT_SONG = 12;
	public static final byte NEXT_SONG = 13;
	public static final byte PREVIOUS_SONG = 14;

}