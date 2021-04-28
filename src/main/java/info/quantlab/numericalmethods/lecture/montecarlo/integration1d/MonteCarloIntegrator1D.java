package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

import net.finmath.randomnumbers.MersenneTwister;

public class MonteCarloIntegrator1D implements Integrator1D {

	private final int seed = 3141;
	
	private int numberOfEvaluationPoints;

	public MonteCarloIntegrator1D(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double range = upperBound-lowerBound;

		DoubleStream randomNumbers = DoubleStream.generate(new MersenneTwister(seed)).limit(numberOfEvaluationPoints);

		double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+range*x)).sum();

		return sum/numberOfEvaluationPoints * range;
	}

}
