package dinaBOT.navigation;

import lejos.nxt.*;

import dinaBOT.mech.*;
import java.lang.Math;
import java.util.ArrayList;

/**
 * The ManhattanPather class is basically a wrapper class for the Astar pathfinding class.
 *
 * ManhattanPather allows you to find the shortest path between two given points. It uses the Astar class to do this; however, it takes care of deciding which node the robot is currently at, which node it is going to, and that the Astar algorithm uses the most updated verion of the map.
 *
 * It is called "ManhattanPather" becuase the path found always travels along the grid, never diagonally.
 *
 * @author Stepan Salenikovich
 * @see Astar
 * @see Map
 * @version 1
*/
public class ManhattanPather implements Pathing, MechConstants {
	Map mapper;
	Movement movement;

	int X, Y;

	static final int NORTH = 90;
	static final int SOUTH = 270;
	static final int EAST = 0;
	static final int WEST = 180;



	/**
	 * creates a new ManhattanPather
	 *
	 * @param mapper the Map object that you wish the ManhattanPather to check for the map.
	 * @param movement the movement object that you wish the ManhattanPather to use.
	*/
	public ManhattanPather(Map mapper, Movement movement) {
		this.mapper = mapper;
		this.movement = movement;

		this.X = mapper.getRez()[0];
		this.Y = mapper.getRez()[1];
	}


	/**
	 * Generates the shortest path from (x1,y1) to (x2,y2) taking into acount the initial heading. As in Astar, all the nodes of the shortest path are adjacent.
	 *
	 * @param x1 the initial x position in cm.
	 * @param y1 the initial y position in cm.
	 * @param heading the initial heading in rads.
	 * @param x2 the final desired x position in cm.
	 * @param y2 the final desired y position in cm.
	 *
	 * @return a 2D array of the nodes of the shortest path in order from start to destination (not including the start node)
	*/
	public double[][] generatePath(double x1, double y1, double heading, double x2, double y2) {
		double nodeDist;
		Astar pather;
		int[] start, end;
		int direction;
		double[][] path;
		int[][] rawPath;
		int i, j;
		int nodes;

		nodeDist = UNIT_TILE;


		pather = new Astar(X,Y);

		updateMap(pather);	//sends coords of obstacles to path

		// determine starting coordinate
		// ensures coords fit into the grid
		start = new int[2];

		start[0] = (int)Math.round(x1/nodeDist);
		if(start[0] < 0) start[0] = 0;
		else if(start[0] > X - 1) start[0] = X - 1;

		start[1] = (int)Math.round(y1/nodeDist);
		if(start[1] < 0) start[1] = 0;
		else if(start[1] > Y - 1) start[1] = Y - 1;

		//determine ending coordinate
		// again, ensures coords fit into the grid
		end = new int[2];

		end[0] = (int)Math.round(x2/nodeDist);
		if(end[0] < 0) end[0] = 0;
		else if(end[0] > X - 1) end[0] = X - 1;

		end[1] = (int)Math.round(y2/nodeDist);
		if(end[1] < 0) end[1] = 0;
		else if(end[1] > Y - 1) end[1] = Y - 1;

		//determine current direction (closest)
		heading = (180*heading/Math.PI)%360;

		direction = NORTH;
		if((SOUTH + 45) > heading && (SOUTH - 45) < heading) direction = SOUTH;
		else if((EAST + 45) > heading && (EAST - 45) < heading) direction = EAST;
		else if((WEST + 45) > heading && (WEST - 45) < heading) direction = WEST;

		//get raw path

		rawPath = pather.getPath(start, direction, end);

		if(rawPath != null) {
			//convert to centimeters
			path = new double[rawPath.length][2];


			for(i = 0; i < rawPath.length; i++) {
				path[i][0] = rawPath[i][0] * UNIT_TILE;
				path[i][1] = rawPath[i][1] * UNIT_TILE;

			}
		} else path = null;	//no path found


		return path;

	}

	private void updateMap(Astar pather) {
		int[][] map;

		map = mapper.getMap();

		for(int x = 0; x < X; x++) {
			for(int y = 0; y < Y; y++) {
				if(map[x][y] == DANGER) pather.addObstacle(new int[] {x, y}, DANGER);	//danger zone
				else if(map[x][y] > DANGER) pather.addObstacle(new int[] {x, y}, OBSTACLE);	//obstacle
			}
		}


	}


}
