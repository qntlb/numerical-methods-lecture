package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
	
import net.finmath.randomnumbers.MersenneTwister;

public class MonteCarloIntegrator implements Integrator {

	private int numberOfEvaluationPoints;
	
	public MonteCarloIntegrator(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {
		
		double range = upperBound-lowerBound;

		DoubleStream randomNumbers = DoubleStream.generate(new MersenneTwister(3141)).limit(numberOfEvaluationPoints);

		double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+range*x)).sum();
		
		return sum/numberOfEvaluationPoints * range;
	}

}
