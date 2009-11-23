package dinaBOT.navigation;

/**
 * The SearchPatterns interface contains all the possible SearchPatterns we could use.
 *
 * @author Alexandre Courtemanche
 */

public interface SearchPatterns {

	/**
	 * ZigZag search pattern that makes the robot go up and down the x-axis.
	*/
	public static final int[][] ZIGZAG_X = {
		new int[] {6,1},
		new int[] {6,2},
		new int[] {1,2},
		new int[] {1,3},
		new int[] {6,3},
		new int[] {6,4},
		new int[] {1,4},
		new int[] {1,5},
		new int[] {6,5},
		new int[] {6,6},
		new int[] {1,1} // Go back to starting node
	};

	/**
	 * ZigZag search pattern that makes the robot go up and down the x-axis.
	*/
	public static final int[][] ZIGZAG_Y = {
		new int[] {1,6},
		new int[] {2,6},
		new int[] {2,1},
		new int[] {3,1},
		new int[] {3,6},
		new int[] {4,6},
		new int[] {4,1},
		new int[] {5,1},
		new int[] {5,6},
		new int[] {6,6},
		new int[] {1,1} // Go back to starting node
	};

	public static final int[][] DROP_TEST = {
		new int[] {2,2},
		new int[] {3,3},
		new int[] {4,4},
		new int[] {5,5},
		new int[] {6,6}
	};

	/**
	 * Moving test pattern
	*/
	public static final int[][] MOVE_TEST = {
		new int[] {1,4},
		new int[] {1,7},
		new int[] {4,7},
		new int[] {4,4},
		new int[] {7,4},
		new int[] {7,7},
		new int[] {4,7},
		new int[] {4,4},
		new int[] {1,4},
		new int[] {1,1},
		new int[] {4,1},
		new int[] {4,4},
		new int[] {7,4},
		new int[] {7,1},
		new int[] {4,1},
		new int[] {4,4}
	};


}