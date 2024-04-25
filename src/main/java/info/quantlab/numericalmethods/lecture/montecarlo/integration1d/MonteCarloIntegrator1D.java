package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;

public class MonteCarloIntegrator1D implements Integrator1D {

	private final int numberOfEvaluationPoints;
	private final int seed;

	/**
	 * 
	 * @param numberOfEvaluationPoints The number of sample points to be used.
	 * @param seed The seed for the random number generator.
	 */
	public MonteCarloIntegrator1D(int numberOfEvaluationPoints, int seed) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
		this.seed = seed;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		// Create random number sequence generator (we use MersenneTwister)
		DoubleSupplier uniformRandomNumberGenerator = new MersenneTwister(3141);
		double domainSize = upperBound-lowerBound;

		double sum = 0.0;
		for(int i=0; i<numberOfEvaluationPoints; i++) {

			double randomNumber = uniformRandomNumberGenerator.getAsDouble();
			double argument = lowerBound + randomNumber * domainSize;
			double value = integrand.applyAsDouble(argument);

			sum += value;
		}
		return sum / numberOfEvaluationPoints * domainSize;
	}
}
