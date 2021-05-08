package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

import org.junit.jupiter.api.Test;

import org.junit.Assert;

public class MonteCarloIntegratorTest {

	@Test
	public void test() {

		double lowerBound = 0.0;
		double upperBound = 1.5;
		DoubleUnaryOperator integrand = x -> Math.cos(x);

		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		int numberOfEvaluationPoints = 100;

		Integrator1D integrator = new MonteCarloIntegrator1D(numberOfEvaluationPoints);

		double integralNumeric = integrator.integrate(integrand, lowerBound, upperBound);
		double integralAnalyticValue = integralAnalytic.applyAsDouble(upperBound)-integralAnalytic.applyAsDouble(lowerBound);

		double error = integralNumeric-integralAnalyticValue;

		System.out.println(String.format("%30s:", integrator.getClass().getSimpleName()) + "\t" + "numeric: " + integralNumeric + "\tanalytic: " + integralAnalyticValue + "\t" + error);

		double tolerance = 3.0/Math.sqrt(numberOfEvaluationPoints);
		
		Assert.assertEquals("Integral", integralNumeric, integralAnalyticValue, tolerance);
	}

}
