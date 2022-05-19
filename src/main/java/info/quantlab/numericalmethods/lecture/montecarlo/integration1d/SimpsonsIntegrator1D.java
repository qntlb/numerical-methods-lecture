package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class SimpsonsIntegrator1D implements Integrator1D {

	private int numberOfEvaluationPoints;

	public SimpsonsIntegrator1D(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
		
		if(numberOfEvaluationPoints % 2 != 1) throw new IllegalArgumentException("numberOfEvaluationPoints should be odd");
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double range = upperBound-lowerBound;

		int numberOfDoubleSizeIntervals = (numberOfEvaluationPoints-1)/2;

		double intervalSize  = range/numberOfDoubleSizeIntervals / 2.0;			// the h in the formula

		// First, second and last evaluation point will be missing, added at the end
		IntStream intervals = IntStream.range(1,numberOfDoubleSizeIntervals);

		DoubleStream integralParts = intervals.mapToDouble(
				i -> 2 * integrand.applyAsDouble(lowerBound + 2*i*intervalSize)
				+ 4 * integrand.applyAsDouble(lowerBound + (2*i+1)*intervalSize));

		double sum = integralParts.sum()
				+ 4 * integrand.applyAsDouble(lowerBound + intervalSize)
				+ integrand.applyAsDouble(lowerBound) + integrand.applyAsDouble(upperBound);

		return sum / 3 * intervalSize;
	}
}
