package dinaBOT.sensor;

public interface LineDetector {
	
	public static abstract LineDector left_detector;
	public static abstract LineDector right_detector;
	
	public void registerListener(LineDetectorListener listener);
	
}