package dinaBOT.navigation;

//leJOS imports
import lejos.nxt.*;
import java.lang.Math;

//dinaBOT imports
import dinaBOT.mech.*;
import dinaBOT.sensor.*;

/**
 * 
 * 
 * @author Stepan Salenikovich, Severin Smith
 * @see Navigation, Navigator, Pathing, Astar, USSensorListener, USSensor
 * @version 3
 */
public class Map implements MechConstants, USSensorListener {

	Odometer odo;

	int[][] map;
	int resolution;
	
	double nodeDist;
	int threshold;
	
	protected USSensor lowUS = USSensor.low_sensor;
	protected USSensor highUS = USSensor.high_sensor;
	int[] low_Readings;
	int[] high_Readings;
	
	boolean newObstacle;

	MapListener listener;
	

	public Map( Odometer odo, int rez, int threshold, double nodeDist ) {

		this.odo = odo;
		this.resolution = rez;

		this.threshold = threshold;
		this.map = new int [resolution][resolution];
		
		this.nodeDist = nodeDist;
		
		this.newObstacle = false;
		
		lowUS.registerListener(this);
		highUS.registerListener(this);
	}
	
	public void registerListener(MapListener listener) {
		this.listener = listener;
	}
	
	void notifyListeners( int x, int y) {
		if(listener != null) {
			listener.newObstacle(x,y);
		}
	}
	
	private double[] getUSCoord( int distance ) {
		double[] pos = new double[3];
		double[] coord = new double[2];
		
		pos = odo.getPosition();

		coord[0] = Math.cos(pos[2])*distance + pos[0];
		coord[1] = Math.sin(pos[2])*distance + pos[1];

		// make sure there are no negative coords
		if ( coord[0] < 0 ) {
			coord[0] = 0;
		} else if ( coord[0] > (resolution - 1) * nodeDist) coord[0] = (resolution -1)*nodeDist;
		if ( coord[1] < 0 ) {
			coord[1] = 0;
		} else if ( coord[1] > (resolution - 1) * nodeDist) coord[1] = (resolution -1)*nodeDist;

		return coord;
		
	}

	private int[] getNode( double[] coord ) {
		int[] node = new int[2];

		node[0] = (int)Math.round( coord[0]/nodeDist );
		node[1] = (int)Math.round( coord[1]/nodeDist );

		return node;
	}

	public int[][] getMap() {
				
		// FIX THIS SO IT RETURNS A COPY OF THE ARRAY

		return map;
	}
	
	public int getRez() {
		return resolution;
	}
	
	public void newValues(int[] new_values, Position position) {
		double[] coord = new double[2];
		int[] node = new int[2];
		int distance = 255;
		
		// only care about high US sensor values
		if (position == USSensorListener.Position.HIGH) {
			this.high_Readings = new_values;

			distance = high_Readings[0];
			
			// if ostacle distance is close enough, mark appropriate node
			if ( distance < threshold ) {

				// get abs. coords from relative distance
				coord = getUSCoord(distance);

				// get node associated with coords
				node = getNode( coord );
				
				Sound.twoBeeps();
				
				// mark node with obstacle 
				if( map[node[0]][node[1]] == 0) {
					map[node[0]][node[1]] = 2;
					
					notifyListeners(node[0], node[1]);
				}
			}
		} 

	}
	
	//Do not pass reference
	public boolean editMap(int x, int y, int value) {
		this.map[x][y] = value;
		
		return true;	//sucess
	}
	
	private synchronized void newObstacleSet(boolean set) {
		newObstacle = set;
	}
	
	public synchronized boolean obstacleCheck() {
		if(newObstacle) {
			newObstacle = false;
			return true;
		} else return false;
	}

}
