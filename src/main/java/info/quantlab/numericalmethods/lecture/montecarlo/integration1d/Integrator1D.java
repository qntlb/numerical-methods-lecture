package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

public interface Integrator1D {

	double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound);

}
