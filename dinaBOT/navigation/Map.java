package dinaBOT.navigation;

//leJOS imports
import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.lang.Math;
import java.io.*;
import java.lang.StringBuffer;

//dinaBOT imports
import dinaBOT.mech.*;
import dinaBOT.sensor.*;
import dinaBOT.util.DinaList;

/**
 *
 *
 * @author Stepan Salenikovich, Severin Smith
 * @see Navigation
 * @see Navigator
 * @see Pathing
 * @see Astar
 * @see USSensorListener
 * @see USSensor
 * @version 3
*/
public class Map implements MechConstants, USSensorListener {

	Odometer odo;

	int[][] map;
	int X, Y;

	double nodeDist;

	int[] high_Readings;
	//int[] low_Readings;

	boolean newObstacle;

	DinaList<MapListener> listeners;

	boolean stop;

	//file IO stuff
	static final String fileName = "map.txt";
	static String text;
	static byte[] byteText;
	static FileOutputStream fos;
	static File f;
	static StringBuffer sb;
	static int fileVersion;

	/* -- BT -- */
	
	BTConnection connection; //Actual BT Connection

	//I/O Streams
	DataInputStream input_stream;
	DataOutputStream output_stream;
	
	boolean bt_connected;

	/**
	 * creates a new (square) Map where the resolution squared is the number of nodes.
	 *
	 * @param odometer the odometer to be used.
	 * @param resolution the resolution (number of nodes) of each axis (the same for both).
	*/
	public Map(Odometer odometer, int resolution) {
		this(odometer, resolution, resolution, UNIT_TILE);
	}

	/**
	 * creates a new (square) Map where the resolution squared is the number of nodes.
	 *
	 * @param odometer the odometer to be used.
	 * @param resolution the resolution (number of nodes) of each axis (the same for both).
	 * @param nodeDist the distance between nodes (this should be set to the distance between the gridlines, UNIT_TILE, unless you know what you're doing!).
	*/
	public Map(Odometer odometer, int resolution, double nodeDist) {
		this(odometer, resolution, resolution, nodeDist);
	}

	/**
	 * creates a new (rectangular) Map where the product of the axis resolutions is the number of nodes.
	 *
	 * @param odometer the odometer to be used.
	 * @param resolution_X the resolution (number of nodes) of the x-axis.
	 * @param resolution_Y the resolution (number of nodes) of the y-axis.
	*/
	public Map(Odometer odometer, int resolution_X, int resolution_Y) {
		this(odometer, resolution_X, resolution_Y, UNIT_TILE);
	}

	/**
	 * creates a new (rectangular) Map where the product of the axis resolutions is the number of nodes.
	 *
	 * @param odometer the odometer to be used.
	 * @param resolution_X the resolution (number of nodes) of the x-axis.
	 * @param resolution_Y the resolution (number of nodes) of the y-axis.
	 * @param nodeDist the distance between nodes (this should be set to the distance between the gridlines, UNIT_TILE, unless you know what you're doing!).
	*/
	public Map(Odometer odometer, int resolution_X, int resolution_Y , double nodeDist) {
		this.odo = odometer;
		this.X = resolution_X;
		this.Y = resolution_Y;

		this.map = new int[X][Y];

		this.nodeDist = nodeDist;

		newObstacleSet(false);

		listeners = new DinaList<MapListener>();

		//initialize border
		for(int x = 0; x < X; x++) map[x][0] = map[x][Y-1] = WALL;
		for(int y = 0; y < Y; y++) map[0][y] = map[X-1][y] = WALL;


		//low_Readings = new int[] {255,255,255,255,255,255,255,255};
		high_Readings = new int[] {255,255,255,255,255,255,255,255};

		USSensor.high_sensor.registerListener(this);
		//USSensor.low_sensor.registerListener(this);
	}


	public void connect() {
		connection = Bluetooth.waitForConnection();
		
		input_stream = connection.openDataInputStream();
		output_stream = connection.openDataOutputStream();
		
		bt_connected = true;
	}

	/**
	 * registers the parameter as a listener of the map
	 *
	 * @param listener the listener
	*/
	public void registerListener(MapListener listener) {
		listeners.add(listener);
	}

	void notifyListeners(int x, int y) {
		for(int i = 0;i < listeners.size();i++) {
			listeners.get(i).newObstacle(x,y);
		}
	}

	/**
	 * Checks if the coordinate is inside the map.
	 *
	 * @param coord the coordinate (x,y).
	*/
	public boolean checkCoordBounds( double[] coord ) {
		if(coord[0] < 0 || coord[0] > (X - 1) * nodeDist
						 || coord[1] < 0
						 || coord[1] > (Y - 1) * nodeDist) return false;
		else return true;
	}

	/**
	 * Returns the coordinate (in cm) of an object the given distance away from the front of the robot based on the current location (x,y, theta) of the robot.
	 *
	 * Always returns a coordinate inside the map. If the given distance maps to a coordinate outside the map, the the closest coordinate to that one which is inside the map is returned.
	 *
	 * @param distance the distance (in cm) of the object from the centre of the robot.
	*/
	public double[] getCoord(int distance) {
		double angle = odo.getPosition()[2];

		return getCoord(distance, angle);
	}

	/**
	 * Checks if an object the given distance away is inside the map, an obstacle, or danger zone.
	 *
	 * @param distance the distance (in cm) of the object from the centre of the robot.
	 *
	 * @return false if the object is outside the map, inside obstacle/wall/drop-off, or inside a danger zone; true otherwise.
	*/
	public boolean checkUSCoord(int distance) {
		double angle = odo.getPosition()[2];

		return checkCoord(distance, angle);
	}

	/**
	 * Returns the coordinate (in cm) on the map of an object the given distance in the given direction (absolute angle) away from the center of the robot based on the current location (x,y) of the robot.
	 *
	 * Always returns a coordinate inside the map. If the given distance maps to a coordinate outside the map, the the closest coordinate to that one which is inside the map is returned.
	 *
	 * @param distance the distance (in cm) of the object from the centre of the robot.
	 * @param angle the absolute angle (in rads) of the object with respect to the centre of the robot (ie: the absolute angle the robot would have to turn to to face the object).
	*/
	public double[] getCoord(double distance, double angle) {
		double[] pos = new double[3];
		double[] coord = new double[2];

		pos = odo.getPosition();

		pos[2] = angle;

		coord[0] = Math.cos(pos[2])*distance + pos[0];
		coord[1] = Math.sin(pos[2])*distance + pos[1];

		// make sure there are no negative coords
		if(coord[0] < 0) {
			coord[0] = 0;
		} else if(coord[0] > (X - 1) * nodeDist) coord[0] = (X -1)*nodeDist;
		if(coord[1] < 0) {
			coord[1] = 0;
		} else if(coord[1] > (Y - 1) * nodeDist) coord[1] = (Y -1)*nodeDist;

		return coord;

	}

	/**
	 * Checks if an object the given distance in the given direction (absolute angle) away is inside the map, an obstacle, or danger zone.
	 *
	 * @param distance the distance (in cm) of the object from the centre of the robot.
	 * @param angle the absolute angle (in rads) of the object with respect to the centre of the robot (ie: the absolute angle the robot would have to turn to to face the object).
	 *
	 * @return false if the object is outside the map, inside obstacle/wall/drop-off, or inside a danger zone; true otherwise.
	*/
	public boolean checkCoord(double distance, double angle) {
		double[] pos = new double[3];
		double[] coord = new double[2];

		pos = odo.getPosition();

		pos[2] = angle;

		coord[0] = Math.cos(pos[2])*distance + pos[0];
		coord[1] = Math.sin(pos[2])*distance + pos[1];

		// make sure there are no coords out of bounds or in obstacles/danger zones
		// !!! do we want to exclude coords in danger zones?
		if(!checkCoordBounds(coord)) return false;
		else if(coordValue(coord) > 0) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Returns the node corresponding to the given coordinate.
	 *
	 * Does not check bounds.
	 *
	 * @param coord the coordinate (x,y).
	*/
	public int[] getNode(double[] coord) {
		int[] node = new int[2];

		node[0] = (int)Math.round(coord[0]/nodeDist);
		node[1] = (int)Math.round(coord[1]/nodeDist);

		return node;
	}

	/**
	 * Checks that the given node is inside the bounds of the map.
	 *
	 * @param node the coordinate (x,y).
	 *
	 * @return true if inside the bounds of the map; false otherwise.
	*/
	public boolean checkNodeBounds(int[] node) {
		if( node[0] < 0 || node[0] > X - 1
						|| node[1] < 0
						|| node[1] > Y - 1) return false;
		else return true;
	}

	/**
	 * Checks that the given node is inside the bounds of the map.
	 *
	 * @param x the x coordinate to check
	 * @param y the y coordinate to check
	 *
	 * @return true if inside the bounds of the map; false otherwise.
	*/
	public boolean checkNodeBounds(int x, int y) {
		if( x < 0 || x > X - 1
						|| y < 0
						|| y > Y - 1) return false;
		else return true;
	}

	/**
	 * Checks if the given node is inside the map, an obstacle/wall/drop-off, or danger zone.
	 *
	 * @param node the node.
	 *
	 * @return false if the node is outside the map, inside obstacle/wall/drop-off, or inside a danger zone; true otherwise.
	*/
	public boolean checkNode(int[] node) {
		if(!checkNodeBounds(node)) return false;
		else if( map[node[0]][node[1]] > DANGER ) return false;
		else return true;
	}

	/**
	 * Returns the value of the given node.
	 *
	 * @param node the node.
	*/
	public int nodeValue(int[] node) {
		if(!checkNodeBounds(node)) return -1;
		else return map[node[0]][node[1]];
	}

	/**
	 * Returns the value of the given node.
	 *
	 * @param x
	 * @param y
	*/
	public int nodeValue(int x, int y) {
		if(!checkNodeBounds(x,y)) return -1;
		else return map[x][y];
	}

	/**
	 * Returns the value of the given coord.
	 *
	 * @param coord the coord.
	 *
	 * @return returns -1 if the coord is not inside the boundries of the map; otherwise the value of the node.
	*/
	public int coordValue(double[] coord) {
		int[] node = getNode(coord);
		if(!checkNodeBounds(node)) return -1;
		else return map[node[0]][node[1]];
	}

	/**
	 * Returns the current map.
	*/
	public int[][] getMap() {

		// FIX THIS SO IT RETURNS A COPY OF THE ARRAY

		return map;
	}

	/**
	 * Returns the resolution of each axis of the map.
	 *
	 * @return (resolution of x-axis, resolution of y-axis).
	*/
	public int[] getRez() {
		return new int[] {X,Y};
	}

	/**
	 * Interrupts the map with new values from the USSensor
	*/
	public void newValues(int[] new_values, USSensor sensor) { //This is only called by the high sensor because we didn't register with the low one
		double[] coord = new double[2];
		int[] node = new int[2];
		int distance = 255;
		int[] curr_node = new int[2];	//node at which the robot is currently at
		double[] curr_coord = new double[2];	// coordinate at which the robot is currently at
		int minHigh;

		// checks stop bool
		if(stop) return;

		// only care about high US sensor values
		else if(sensor == USSensor.high_sensor) high_Readings = new_values;
		else return; //should never happen

		//minLow = low_Readings[0];
		minHigh = high_Readings[0];

		distance = high_Readings[0];
		/*if(minLow < minHigh
					&& minHigh < threshold
					&& (minHigh - minLow) < 2) {

			distance = high_Readings[0];

		} else distance = 255;
		*/

		// if ostacle distance is close enough, mark appropriate node
		if(distance < OBSTACLE_THRESHOLD) {

			// get abs. coords from relative distance
			coord = getCoord(distance);
			curr_coord = getCoord(0);

			// get node associated with coords
			node = getNode(coord);
			curr_node = getNode(curr_coord);



			// if obstacle is not detected as in current node
			//if(!((node[0] == curr_node[0]) && (node[1] == curr_node[1]))) {


				// mark map with obstacle
				/* 10 = WALL
				 * 5 = DROP ZONE
				 * 3 = OBSTACLE
				 * 2 = DANGER ZONE
				 * 0 = clear
				 * 20 = drop off area corner?? -not yet decided
				*/
				if(map[node[0]][node[1]] < OBSTACLE) {
					Sound.twoBeeps();

					//mark obstacle
					map[node[0]][node[1]] = OBSTACLE;
					
					if(bt_connected) {
						try {
							output_stream.writeInt(-1);
							output_stream.writeInt(node[0]);
							output_stream.writeInt(node[1]);
							output_stream.writeInt(OBSTACLE);
						} catch(Exception e) {
							
						}
					}
					
					//mark danger zone(s) all those adjacent to obstacle except the one behind the obstacle
					if(checkNode(new int[] {node[0] + 1, node[1]})) {
						map[node[0] + 1][node[1]] = DANGER;
						if(bt_connected) {
							try {
								output_stream.writeInt(-1);
								output_stream.writeInt(node[0]+1);
								output_stream.writeInt(node[1]);
								output_stream.writeInt(DANGER);
							} catch(Exception e) {
							
							}
						}
					}
					if(checkNode(new int[] {node[0] - 1, node[1]})) {
						map[node[0] - 1][node[1]] = DANGER;
						if(bt_connected) {
							try {
								output_stream.writeInt(-1);
								output_stream.writeInt(node[0]-1);
								output_stream.writeInt(node[1]);
								output_stream.writeInt(DANGER);
							} catch(Exception e) {
							
							}
						}
					}
					if(checkNode(new int[] {node[0], node[1] + 1})) {
						map[node[0]][node[1] + 1] = DANGER;
						if(bt_connected) {
							try {
								output_stream.writeInt(-1);
								output_stream.writeInt(node[0]);
								output_stream.writeInt(node[1]+1);
								output_stream.writeInt(DANGER);
							} catch(Exception e) {
						
							}
						}
					}
					if(checkNode(new int[] {node[0], node[1] - 1})) {
						map[node[0]][node[1] - 1] = DANGER;
						if(bt_connected) {
							try {
								output_stream.writeInt(-1);
								output_stream.writeInt(node[0]);
								output_stream.writeInt(node[1]-1);
								output_stream.writeInt(DANGER);
							} catch(Exception e) {
							
							}
						}
					}

					notifyListeners(node[0], node[1]);
				}
			//}
		} else {
			// mark nodes within threshold as clear
			/*distance = threshold;
			for(int i = 2; threshold >= UNIT_TILE; i++) {

				coord = getCoord(distance);
				node = getNode(coord);

				if(map[node[0]][node[1]] > 0) {
					map[node[0]][node[1]] = 0;
				}

				distance = threshold/i;
			}*/
		}

	}

	/**Assigns the given node of the map with given value.
	 *
	 * This value is not guranteed to not be changed (eg: if the map is reset).
	*/
	public synchronized boolean editMap(int x, int y, int value) {
		this.map[x][y] = value;

		return true;	//sucess
	}

	private synchronized void newObstacleSet(boolean set) {
		newObstacle = set;
	}


	/**Checks if the map has been interrupted with a new obstacle. Sets the boolean interrupt back to false (ie: can only return true once per new obstacle).
	*/
	public boolean obstacleCheck() {
		if(newObstacle) {
			newObstacleSet(false);
			return true;
		} else return false;
	}

	/**Stops the map (ie: it does not map new obstacles).
	*/
	public synchronized void stop() {
		stop = true;
	}

	/**Starts the map.
	*/
	public synchronized void start() {
		stop = false;
	}

	/**Resets the map. All node values except those which are marked as a wall or drop-off zone are reset to 0.
	*/
	public void reset() {
		this.stop();
		for(int x = 0; x < X; x++) {
			for(int y = 0; y < Y; y++) {
				if(this.map[x][y] != DROP_ZONE && this.map[x][y] != WALL) this.map[x][y] = 0;
			}
		}
		this.start();
	}

	//IO methods
	private byte[] getBytes(String inputText) {
 	//Debug Point
 byte[] nameBytes = new byte[inputText.length()+1];

 for(int i=0;i<inputText.length();i++) {
 nameBytes[i] = (byte) inputText.charAt(i);
 }
 nameBytes[inputText.length()] = 0;

 return nameBytes;
 }

	private void appendToFile(String text) throws IOException{
 byteText = getBytes(text);

 //Critic to add a useless character into file
 //byteText.length-1
 for(int i=0;i<byteText.length-1;i++) {
 fos.write((int) byteText[i]);
		} 
 }

	public boolean printMap() {

		fileVersion = 1;

 try{
 f = new File(fileName);
 if(!f.exists()) {
 f.createNewFile();
 }else{
 	f.delete(); 
 	f.createNewFile();
 }
 
 fos = new FileOutputStream(f);

			text = "";
 for(int x = 0; x < X; x++) {
				for(int y = 0; y < Y; y++) {
					text += this.map[x][y];
					if(y != Y-1) text += "\t";
				}
				text +="\n";
			}

 appendToFile(text);

 fos.close();

 }catch(IOException e) {
			return false;
 }

 return true;
	}

}
