package dinaBOT.util;

public class PIDController implements Controller {

	float proportional_constant, integral_constant, derivative_constant;

	float integral, previous_error;

	float set_point;

	boolean started;

	public PIDController(float set_point, float proportional_constant, float integral_constant, float derivative_constant) {
		this.set_point = set_point;

		this.proportional_constant = proportional_constant;
		this.integral_constant = integral_constant;
		this.derivative_constant = derivative_constant;

		integral = 0;
		previous_error = 0;
	}

	public PIDController(float proportional_constant, float integral_constant, float derivative_constant) {
		this(0, proportional_constant, integral_constant, derivative_constant);
	}

	public void configure(float proportional_constant, float integral_constant, float derivative_constant) {
		this.proportional_constant = proportional_constant;
		this.integral_constant = integral_constant;
		this.derivative_constant = derivative_constant;

	}

	public float getProportionalConstant() {
		return proportional_constant;
	}

	public float getIntegralConstant() {
		return integral_constant;
	}

	public float getDerivateConstant() {
		return derivative_constant;
	}

	public void setSetPoint(float set_point) {
		this.set_point = set_point;
	}

	public float getSetPoint() {
		return set_point;
	}

	public void reset() {
		integral = 0;
		previous_error = 0;
		started = false;
	}

	public float output(float new_value, float dt) {
		//Typicial PID Implementation
		float error = set_point - new_value;
		integral = integral + (error*dt);
		float derivative = (error - previous_error)/dt;
		if(!started) { //Set the very first derivative term to zero to avoid erratic behavior
			started = true;
			derivative = 0;
		}
		float output = (proportional_constant*error) + (integral_constant*integral) + (derivative_constant*derivative);
		previous_error = error;
		return output;
	}

}