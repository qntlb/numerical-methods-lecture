package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import net.finmath.randomnumbers.MersenneTwister;

public class MonteCarloIntegrator1DWithStreams implements Integrator1D {

	private final int numberOfEvaluationPoints;
	private final int seed;

	/**
	 * 
	 * @param numberOfEvaluationPoints The number of sample points to be used.
	 * @param seed The seed for the random number generator.
	 */
	public MonteCarloIntegrator1DWithStreams(int numberOfEvaluationPoints, int seed) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
		this.seed = seed;
	}

	public MonteCarloIntegrator1DWithStreams(int numberOfEvaluationPoints) {
		this(numberOfEvaluationPoints, 3141);
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		DoubleSupplier uniformRandomNumberGenerator = new MersenneTwister(3141);

		DoubleStream randomNumbers = DoubleStream.generate(uniformRandomNumberGenerator).limit(numberOfEvaluationPoints);

		double domainSize = upperBound-lowerBound;

		double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+domainSize*x)).sum();

		return sum/numberOfEvaluationPoints * domainSize;
	}

}
