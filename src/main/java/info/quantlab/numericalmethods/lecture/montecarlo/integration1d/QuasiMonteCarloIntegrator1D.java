package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

/**
 * This is a Riemann sum with left points approximation of the integral.
 */
public class QuasiMonteCarloIntegrator1D implements Integrator1D {

	private final int numberOfEvaluationPoints;

	public QuasiMonteCarloIntegrator1D(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double domainSize = upperBound-lowerBound;

		double sum = 0.0;
		for(int i=0; i<numberOfEvaluationPoints; i++) {

			// left-points of equi-partitioning of [0,1] - x_i = i /n
			double uniformSample = (2.0*i+0.0)/(2.0*numberOfEvaluationPoints);

			double argument = lowerBound + uniformSample * domainSize;
			double value = integrand.applyAsDouble(argument);

			sum += value;
		}
		return sum / numberOfEvaluationPoints * domainSize;
	}
}
