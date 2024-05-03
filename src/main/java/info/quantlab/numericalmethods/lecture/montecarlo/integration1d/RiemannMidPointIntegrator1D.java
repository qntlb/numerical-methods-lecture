package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

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

		double domainSize = upperBound-lowerBound;

		double sum = 0.0;
		for(int i=0; i<numberOfEvaluationPoints; i++) {

			// center-points of equi-partitioning of [0,1]
			double uniformSample = (2.0*i+1.0)/(2.0*numberOfEvaluationPoints);

			double argument = lowerBound + uniformSample * domainSize;
			double value = integrand.applyAsDouble(argument);

			sum += value;
		}
		return sum / numberOfEvaluationPoints * domainSize;
	}
}
