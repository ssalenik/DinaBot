package dinaBOT.navigation;

public class Navigator implements Navigation {
	boolean interrupted;
	
	public boolean goTo(double x, double y) {
		
			path = pather.generatePath( position[0], position[1], position[2], end[0], end[1]);

			if( path != null) {
				for(int i = 0; i < path.length; i++) {
					if( mapper.obstacleCheck() ) {
						position = odometer.getPosition();
						path = pather.generatePath( position[0], position[1], position[2], end[0], end[1]);
						i = 0;
					}
					movement.goTo(path[i][0], path[i][1], 150);
					if(interrupted) repath
				}
			}

			// go back to 0,0
			end =  new double[] {0*UNIT_TILE, 0*UNIT_TILE};

		
	}

	public void interrupt() {
		interrupt = stop;
		movement.stop();
	}
	public void softInterrupt(int x, int y);
	
}
