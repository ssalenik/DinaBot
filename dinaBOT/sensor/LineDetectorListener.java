package dinaBOT.sensor;

public interface LineDetectorListener {
	
	public enum Position { LEFT, RIGHT }
	
	public void lineDetected(Position position);
	
}