package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.function.DoubleUnaryOperator;

public interface Integrator {

	double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound);

}
