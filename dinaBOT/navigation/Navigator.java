package dinaBOT.navigation;

import dinaBOT.mech.*;
import dinaBOT.sensor.*;

import lejos.nxt.*;

/**
 * 
 * 
 * @author Stepan Salenikovich, Severin Smith
 * @see Navigation, Map, Pathing, Astar, MapListener, USSensorListener, USSensor
 * @version 1
 */
public class Navigator implements Navigation, MechConstants, USSensorListener {

	Odometer odometer;
	Movement movement;

	Map map;
	Pathing pather;
	
	int[] low_Readings;
	int[] high_Readings;
	
	int node; //the current location in the path array
	double[][] path;
	
	boolean active, hard_interrupt, soft_interrupt, full_mode;
	
	public Navigator(Odometer odometer, Movement movement, Map map, Pathing pather) {
		this.odometer = odometer;
		this.movement = movement;

		this.map = map;
		this.pather = pather;

		map.registerListener(this);
		
		low_Readings = new int[] {255,255,255,255,255,255,255,255};
		high_Readings = new int[] {255,255,255,255,255,255,255,255};
		
		USSensor.high_sensor.registerListener(this);
		USSensor.low_sensor.registerListener(this);
	}


	public int goTo(double x, double y, boolean full) {
		this.full_mode = full;
		hard_interrupt = false;
		
		while(repath(x, y)) {
			soft_interrupt = false;
			for(node = 0; node < path.length; node++) {
				active = true;
				if(hard_interrupt || soft_interrupt || !movement.goTo(path[node][0], path[node][1], SPEED_MED)) break;
				active = false;
				if(node == path.length-1) return 0;
			}
			active = false;
			if(hard_interrupt) return 1;
		}
		
		return -1;
	}
	
	boolean repath(double x, double y) {
		double[] position;
		
		if(path == null || node ==  0) {
			position = odometer.getPosition();
		} else position = path[node - 1];
		
		path = pather.generatePath(position[0], position[1], position[2], x, y);
		if(path == null) return false;
		else return true;
	}

	public void interrupt() {
		hard_interrupt = true;
		movement.stop();
	}

	public void newObstacle(int x, int y) {
		if(active) {
			for(int i = node ; i < path.length; i++) {
				// check if new obstacle lies on current path
				if((path[i][0] == x*UNIT_TILE) && (path[i][1] == y*UNIT_TILE)) {
					soft_interrupt = true;
					movement.stop();
				}
			}
		}
	}
	
	public void newValues(int[] new_values, USSensor sensor) {
		
		if(active && !full_mode) {
			int minLow, minHigh;
			
			if(sensor == USSensor.low_sensor) low_Readings = new_values;
			else if (sensor == USSensor.high_sensor) high_Readings = new_values;
			else return; //should never happen
			
			minLow = low_Readings[0];
			minHigh = high_Readings[0];
			
			if(minLow < 20
						&& Math.abs(minLow - minHigh) > DETECTION_THRESHOLD
						&& low_Readings[1] < 100) {
				
				interrupt();
							
			}
		}
	}
}
