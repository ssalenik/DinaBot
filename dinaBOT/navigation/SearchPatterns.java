package dinaBOT.navigation;

/**
 * The SearchPatterns interface contains all the possible SearchPatterns we could use.
 *
 * @author Alexandre Courtemanche
*/

public interface SearchPatterns {


	/**
	 * Pattern list for when going through them in the user input module.
	*/
	public static String[] PatternNames = {
		"BASIC",
		"PRACTICE_ZIGZAG_X",
		"PRACTICE_ZIGZAG_Y",
		"PRACTICE_SPIRAL",
		"ZIGZAG_X",
		"ZIGZAG_Y",
		"CCW_SPIRAL"
	};

	/**
	 * Basic cross-map search pattern.
	 * (Practice map)
	*/
	public static final int[][] BASIC = {
		new int[] {7,7},
		new int[] {1,1}
	};

	/**
	 * ZigZag search pattern that makes the robot go up and down the x-axis.
	 * (Practice map)
	*/
	public static final int[][] PRACTICE_ZIGZAG_X = {
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
	 * (Practice map)
	*/
	public static final int[][] PRACTICE_ZIGZAG_Y = {
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

	/**
	 * Spiral patterns that makes the robot go around the obstacle course in a spiral.
	 * (Practice map)
	*/
	public static final int[][] PRACTICE_SPIRAL = {
		new int[] {7,1},
		new int[] {7,7},
		new int[] {1,7},
		new int[] {1,2},
		new int[] {6,2},
		new int[] {6,6},
		new int[] {2,6},
		new int[] {2,2},
		new int[] {5,2},
		new int[] {5,5},
		new int[] {3,5},
		new int[] {3,3},
		new int[] {4,4},
		new int[] {4,5},
	};

	/**
	 * ZigZag search pattern that makes the robot go up and down the x-axis.
	*/
	public static final int[][] ZIGZAG_X = {
		new int[] {11,1},
		new int[] {11,3},
		new int[] {1,3},
		new int[] {1,5},
		new int[] {11,5},
		new int[] {11,7},
		new int[] {1,7},
		new int[] {1,9},
		new int[] {11,9},
		new int[] {11,11},
		new int[] {1,11},
		new int[] {1,1} // Go back to starting node
	};

	/**
	 * ZigZag search pattern that makes the robot go up and down the y-axis.
	*/
	public static final int[][] ZIGZAG_Y = {
		new int[] {1,11},
		new int[] {3,11},
		new int[] {3,1},
		new int[] {5,1},
		new int[] {5,11},
		new int[] {7,11},
		new int[] {7,1},
		new int[] {9,1},
		new int[] {9,11},
		new int[] {11,11},
		new int[] {11,1},
		new int[] {1,1} //Go back to starting node
	};

	/**
	 * Search pattern that makes th robot go in a counter-clockwise spiral
	*/
	public static final int[][] CCW_SPIRAL = {
		new int[] {11,1},
		new int[] {11,11},
		new int[] {1,11},
		new int[] {1,2},
		new int[] {10,2},
		new int[] {10,10},
		new int[] {2,10},
		new int[] {2,3},
		new int[] {9,3},
		new int[] {9,9},
		new int[] {3,9},
		new int[] {3,4},
		new int[] {8,4},
		new int[] {8,8},
		new int[] {4,8},
		new int[] {4,5},
		new int[] {7,5},
		new int[] {7,7},
		new int[] {5,7},
		new int[] {5,6},
		new int[] {1,1}
	//Go back to starting node
	};



	/*public static final int[][] DROP_TEST = {
		new int[] {2,2},
		new int[] {3,3},
		new int[] {4,4},
		new int[] {5,5},
		new int[] {6,6}
	};*/



}