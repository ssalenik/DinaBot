package dinaBOT.navigation;

import lejos.nxt.*;

import dinaBOT.mech.*;
import java.lang.Math;
import java.util.ArrayList;

public class ManhattanPather implements Pathing, MechConstants{
	static Map mapper;
	static Movement movement;
		
	static final int NORTH = 90;
	static final int SOUTH = 270;
	static final int EAST = 0;
	static final int WEST = 180;
	
	public ManhattanPather(Map mapper, Movement movement) {
		this.mapper = mapper;
		this.movement = movement;
	}
		
	public double[][] generatePath(double x1, double y1, double heading, double x2, double y2) {
		int rez;
		double nodeDist;
		Astar pather;
		int[] start, end;
		int direction;
		double[][] path;
		int[][] rawPath;
		int i, j;
		int nodes;
		
		rez = 12;	//should be done via constructor later?
		
		nodeDist = UNIT_TILE;
		
		
		pather = new Astar(rez);
		
		updateMap(pather);	//sends coords of obstacles to path
		
		//determine starting coordinate
		start = new int[2];
		start[0] = (int)Math.round( x1/nodeDist );
		start[1] = (int)Math.round( y1/nodeDist );
		
		//determine ending coordinate
		end = new int[2];
		end[0] = (int)Math.round( x2/nodeDist );
		end[1] = (int)Math.round( y2/nodeDist );
		
		//determine current direction (closest)
		heading = (180*heading/Math.PI)%360;
		
		direction = NORTH;
		if( (SOUTH + 45) > heading && (SOUTH - 45) < heading ) direction = SOUTH;
		else if( (EAST + 45) > heading && (EAST - 45) < heading ) direction = EAST;
		else if( (WEST + 45) > heading && (WEST - 45) < heading ) direction = WEST;
		
		//get raw path
		
		rawPath = pather.getPath(start, direction, end);
		
		if( rawPath != null ) {
			//convert to centimeters
			path = new double[rawPath.length][2];

			
			for( i = 0; i < rawPath.length; i++) {
				path[i][0] = rawPath[i][0] * UNIT_TILE;
				path[i][1] = rawPath[i][1] * UNIT_TILE;
				
			}
		} else path = null;	//no path found

		
		return path;

	}
	
	private void updateMap(Astar pather) {
		int[][] map;
		int rez;
		
		map = mapper.getMap();
		rez = mapper.getRez();
		
		for(int i = 0; i < rez; i++) {
			for(int j = 0; j < rez; j++) {
				if(map[i][j] != 0) {
					pather.addObstacle(new int[] {i, j}, 2);
				}
			}
		}
		
	}
	
}
