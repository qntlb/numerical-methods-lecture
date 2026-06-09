package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;

/**
 * Implementation of Integrator1D using Monte-Carlo integration with a given RandomNumberGenerator1D.
 */
public class MonteCarloIntegrator1DFromRandomGenerator1D implements Integrator1D {

	private final int numberOfEvaluationPoints;
	private final Supplier<RandomNumberGenerator1D> randomGenerator1DFactory;

	/**
	 * Create a Monte-Carlo integration with MersenneTwister (random number generator) and the given seed.
	 *
	 * @param numberOfEvaluationPoints The number of sample points to be used.
	 * @param randomGenerator1DFactory Random number generator factory.
	 */
	public MonteCarloIntegrator1DFromRandomGenerator1D(int numberOfEvaluationPoints, Supplier<RandomNumberGenerator1D> randomGenerator1DFactory) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
		this.randomGenerator1DFactory = randomGenerator1DFactory;
	}

	/**
	 * Create a Monte-Carlo integration with MersenneTwister (random number generator) and the given seed.
	 *
	 * @param numberOfEvaluationPoints The number of sample points to be used.
	 * @param seed The seed for the random number generator.
	 */
	public MonteCarloIntegrator1DFromRandomGenerator1D(int numberOfEvaluationPoints, int seed) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
		this.randomGenerator1DFactory = () -> new MersenneTwister(seed);
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		// Create random number sequence with given generator
		final DoubleSupplier uniformRandomNumberGenerator = randomGenerator1DFactory.get();
		final double domainSize = upperBound-lowerBound;

		double sum = 0.0;
		for(int i=0; i<numberOfEvaluationPoints; i++) {

			final double randomNumber = uniformRandomNumberGenerator.getAsDouble();

			final double argument = lowerBound + randomNumber * domainSize;
			final double value = integrand.applyAsDouble(argument);

			sum += value;
		}
		return sum / numberOfEvaluationPoints * domainSize;
	}
}
