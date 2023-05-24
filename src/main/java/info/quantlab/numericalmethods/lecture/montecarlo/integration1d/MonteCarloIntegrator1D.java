package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public class MonteCarloIntegrator1D implements Integrator1D {

	private final int numberOfEvaluationPoints;
	private final DoubleSupplier uniformRandomNumberGenerator;

	public MonteCarloIntegrator1D(int numberOfEvaluationPoints, DoubleSupplier uniformRandomNumberGenerator) {
		super();
		this.numberOfEvaluationPoints = numberOfEvaluationPoints;
		this.uniformRandomNumberGenerator = uniformRandomNumberGenerator;
	}

	@Override
	public double integrate(DoubleUnaryOperator integrand, double lowerBound, double upperBound) {

		double domainSize = upperBound-lowerBound;

		double sum = 0.0;
		for(int i=0; i<numberOfEvaluationPoints; i++) {

			double randomNumber = uniformRandomNumberGenerator.getAsDouble();
			double argument = lowerBound + randomNumber * domainSize;
			double value = integrand.applyAsDouble(argument);

			sum += value;
		}
		return sum / numberOfEvaluationPoints * domainSize;
	}

}
