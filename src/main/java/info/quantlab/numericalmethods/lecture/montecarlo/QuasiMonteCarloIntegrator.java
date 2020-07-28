package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class QuasiMonteCarloIntegrator implements Integrator {

	private int numberOfEvaluationPoints;

	public QuasiMonteCarloIntegrator(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double range = upperBound-lowerBound;

		DoubleStream randomNumbers = IntStream.range(0, numberOfEvaluationPoints).mapToDouble(
				i -> i*1.0/numberOfEvaluationPoints
				);

		double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+range*x)).sum();

		return sum/numberOfEvaluationPoints * range;
	}

}
