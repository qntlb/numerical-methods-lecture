package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

/**
 * Interface to be implemented by classes providing a 1-D integrator.
 * 
 * @author Christian Fries
 */
public interface Integrator1D {

	/**
	 * Calculate (an approximation to) the integral \( \int_{a}^{b} f(x) \mathrm{d}x \).
	 * 
	 * @param integrand The integrand f
	 * @param lowerBound The lower bound a
	 * @param upperBound The upper bound b
	 * @return (an approximation to) the integral \( \int_{a}^{b} f(x) \mathrm{d}x \).
	 */
	double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound);

}
