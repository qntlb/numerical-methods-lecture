package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

/**
 * This is a Riemann sum with center points approximation of the integral.
 */
public class RiemannMidPointIntegrator1D implements Integrator1D {

	private final int numberOfEvaluationPoints;

	public RiemannMidPointIntegrator1D(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		final double domainSize = upperBound-lowerBound;

		double sum = 0.0;
		for(int i=0; i<numberOfEvaluationPoints; i++) {

			// center-points of equi-partitioning of [0,1]
			final double uniformSample = (2.0*i+1.0)/(2.0*numberOfEvaluationPoints);

			final double argument = lowerBound + uniformSample * domainSize;
			final double value = integrand.applyAsDouble(argument);

			sum += value;
		}
		return sum / numberOfEvaluationPoints * domainSize;
	}
}
