package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.Test;

import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.Integrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.QuasiMonteCarloIntegrator1D;

public class QuasiMonteCarloIntegratorTest {

	@Test
	public void test() {

		double lowerBound = 1.0;
		double upperBound = 5.0;
		DoubleUnaryOperator integrand = x -> Math.cos(x);

		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		int numberOfEvaluationPoints = 10000;

		Integrator1D integrator = new QuasiMonteCarloIntegrator1D(numberOfEvaluationPoints);

		double integralNumeric = integrator.integrate(integrand, lowerBound, upperBound);
		double integralAnalyticValue = integralAnalytic.applyAsDouble(upperBound)-integralAnalytic.applyAsDouble(lowerBound);

		double error = integralNumeric-integralAnalyticValue;

		System.out.println("numeric: " + integralNumeric + "\tanalytic: " + integralAnalyticValue + "\t" + error);

		double tolerance = 3.0/Math.sqrt(numberOfEvaluationPoints);
		Assert.assertEquals("Integral", integralNumeric, integralAnalyticValue, tolerance);
	}

}
