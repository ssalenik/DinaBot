package dinaBOT.sensor;

public interface USSensorListener {
	static enum Position {HIGH, LOW};
	
	public void newValues(int[] new_values, Position position);
	
}