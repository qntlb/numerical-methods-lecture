package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class SimpsonsIntegrator implements Integrator {

	private int numberOfEvaluationPoints;


	public SimpsonsIntegrator(int numberOfEvaluationPoints) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
	}


	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double range = upperBound-lowerBound;

		int numberOfIntervals = (numberOfEvaluationPoints-1)/2;

		// same as range / (numberOfEvaluationPoints-1) - the h in the formula
		double intervalHalfSize  = range/numberOfIntervals / 2.0;

		IntStream intervals = IntStream.range(1,numberOfIntervals);

		DoubleStream integralParts = intervals.mapToDouble(i -> 2 * integrand.applyAsDouble(lowerBound + 2*i*intervalHalfSize)
				+ 4 * integrand.applyAsDouble(lowerBound + (2*i+1)*intervalHalfSize));

		double sum = integralParts.sum();
		sum += 4 * integrand.applyAsDouble(lowerBound + intervalHalfSize);
		sum += integrand.applyAsDouble(lowerBound);
		sum += integrand.applyAsDouble(upperBound);


		return sum / 3 * intervalHalfSize;
	}

}
