package dinaBOT.util;

public interface Controller {

	public void setSetPoint(float set_point);

	public float getSetPoint();

	public float output(float new_value, float dt);

	public void reset();

}