package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class RiemannMidPointIntegrator1DWithStreams implements Integrator1D {

	private final int numberOfEvaluationPoints;

	public RiemannMidPointIntegrator1DWithStreams(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double domainSize = upperBound-lowerBound;

		// Generate sample points

		DoubleStream randomNumbers = IntStream.range(0, numberOfEvaluationPoints).mapToDouble(
				i -> (2.0*i+1.0)*1.0/(2*numberOfEvaluationPoints)
				);

		double sum = randomNumbers.map(x -> integrand.applyAsDouble(lowerBound+domainSize*x)).sum();

		return sum/numberOfEvaluationPoints * domainSize;
	}

}
