package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class QuasiMonteCarloIntegrator1D implements Integrator1D {

	private final int numberOfEvaluationPoints;

	public QuasiMonteCarloIntegrator1D(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double range = upperBound-lowerBound;

		// Generate sample points

		DoubleStream randomNumbers = IntStream.range(0, numberOfEvaluationPoints).mapToDouble(
				i -> (2*i+1)*1.0/(2*numberOfEvaluationPoints)
				);

		double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+range*x)).sum();

		return sum/numberOfEvaluationPoints * range;
	}

}
