package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import net.finmath.randomnumbers.MersenneTwister;

public class MonteCarloIntegrator1DWithStreams implements Integrator1D {

	private final DoubleSupplier randomNumberGenerator;
	private final int numberOfEvaluationPoints;

	public MonteCarloIntegrator1DWithStreams(DoubleSupplier randomNumberGenerator, int numberOfEvaluationPoints) {
		super();
		this.randomNumberGenerator = randomNumberGenerator;
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	public MonteCarloIntegrator1DWithStreams(int numberOfEvaluationPoints) {
		this(new MersenneTwister(3141), numberOfEvaluationPoints);
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double domainSize = upperBound-lowerBound;

		DoubleStream randomNumbers = DoubleStream.generate(randomNumberGenerator).limit(numberOfEvaluationPoints);

		double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+domainSize*x)).sum();

		return sum/numberOfEvaluationPoints * domainSize;
	}

}
