package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.Test;

public class QuasiMonteCarloIntegratorTest {

	@Test
	public void test() {

		double lowerBound = 0.0;
		double upperBound = 1.5;
		DoubleUnaryOperator integrand = x -> Math.cos(x);

		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		int numberOfEvaluationPoints = 101;

		Integrator1D integrator = new QuasiMonteCarloIntegrator1D(numberOfEvaluationPoints);

		double integralNumeric = integrator.integrate(integrand, lowerBound, upperBound);
		double integralAnalyticValue = integralAnalytic.applyAsDouble(upperBound)-integralAnalytic.applyAsDouble(lowerBound);

		double error = integralNumeric-integralAnalyticValue;

		System.out.println(String.format("%30s:", integrator.getClass().getSimpleName()) + "\t" + "numeric: " + integralNumeric + "\tanalytic: " + integralAnalyticValue + "\t" + error);

		double tolerance = 3.0/Math.sqrt(numberOfEvaluationPoints);
		Assert.assertEquals("Integral", integralNumeric, integralAnalyticValue, tolerance);
	}

}
