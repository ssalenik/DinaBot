package dinaBOT.navigation;

import dinaBOT.mech.*;

import lejos.nxt.*;


public class Navigator implements Navigation, MechConstants {
	boolean interrupted;
	
	Pathing pather;
	Movement movement;
	Map map;
	Odometer odometer;
	
	int[] position;
		
	double[][] path;
	int node;	//the current location in the path array
	
	public Navigator( Pathing pather, Movement movement, Map map, Odometer odometer ) {
		this.pather = pather;
		this.movement = movement;
		this.map = map;
		this.odometer = odometer;
				
		map.registerListener(this);
	}
	
	public boolean goTo(double x, double y) {
			double[] position = new double[3];
			
			position = odometer.getPosition();
			
			path = pather.generatePath( position[0], position[1], position[2], x, y);

			if( path != null) {
				for(node = 0; node < path.length; node++) {
					
					movement.goTo(path[node][0], path[node][1], SPEED_MED);
					
					if(interrupted) {
						// get current position
						position = odometer.getPosition();
						// generate new path
						path = pather.generatePath( position[0], position[1], position[2], x, y);
						// reset position in path
						node = 0;
						// reset interrupt flag
						setInterrupt(false);
					}
				}
			} else {
				LCD.drawString("no path", 0, 0);
				return false;	//fail
			}
			
		return true;	//success
		
	}

	public void interrupt() {
		movement.stop();
		setInterrupt(true);
	}
	
	public void newObstacle(int x, int y) {
		for( ; node < path.length; node++) {
			// check if new obstacle lies on current path
			if( (path[node][0] == x) && (path[node][1] == y) ) {
				//new obstacle in the way, stop all movement and set interrupt flag
				movement.stop();
				setInterrupt(true);
			}
		}
	}
	
	private synchronized void setInterrupt( boolean set ) {
		interrupted = set;
	}
}
