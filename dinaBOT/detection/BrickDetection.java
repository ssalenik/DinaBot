package dinaBOT.detection;
import lejos.nxt.*;

public class BrickDetection extends Thread {
	
	/*
	 * So far I'm not really familiar on how the current US sensor poller
	 * proceeds to pass its "Distances" array to the USlistener, so instead I just
	 * declared a UltraSonic sensor for the purpose of writing this code 
	 */
	
	UltrasonicSensor sensor = new UltrasonicSensor(SensorPort.S2);
	int[][] lastTenDistances = new int[10][8];
	int currentPing = 0;
	int pingValues = 0;
	int badSensorValues = 0;
	int goodSensorValues = 0;
	
	/**	uses a "window" of 10 arrays of size 8 to get US distances
	 * 	(window might be reduced - maybe one array is sufficient)
	 * 	First, it checks the first value of each array to see if it is
	 *  a 255 value of less. If a value less than 255 appears, a brick is in sight.
	 *  If all the first values are 255, then every data value in each of the 10 arrays 
	 *  will be checked to see if there's a value of less than 255 that appears somewhere.
	 *  If that is the case, a brick is in sight. 
	 *  If no such value is detected, there is no brick in sight
	 * 
	 * 
	 * @return true is a brick is in sight, false otherwise
	 */
	boolean IncomingCollision(){
	
		boolean collision = false;
		currentPing = 0;
		pingValues = 0;
		badSensorValues = 0;
		goodSensorValues = 0;
		
		while(currentPing < 10){
		
			sensor.getDistances(lastTenDistances[currentPing]);// temporary method
		
			currentPing += 1;
			}
		
		while(currentPing >= 0){
			
			if(lastTenDistances[currentPing][0] == 255)
				badSensorValues += 1;
			if(lastTenDistances[currentPing][0] != 255)
				goodSensorValues += 1;
			
			currentPing -= 1;
		}
				
		if(goodSensorValues > 0)			
			collision = true;
		if(badSensorValues == 10){
			 
			while(currentPing < 10){

				while(pingValues < 8){
				
					if(lastTenDistances[currentPing][currentPing] != 255){
						
						collision = true;
						return collision;
						}
						
					pingValues += 1;
				}
				currentPing += 1;
			}
		}			
		
		return collision;
	}
	
}


