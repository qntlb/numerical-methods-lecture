package info.quantlab.numericalmethods.lecture.montecarlo;

import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.Test;

public class SimpsonsIntegratorTest {

	@Test
	public void test() {

		double lowerBound = 1.0;
		double upperBound = 5.0;
		DoubleUnaryOperator integrand = x -> Math.cos(x);

		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		int numberOfEvaluationPoints = 100;

		Integrator integrator = new SimpsonsIntegrator(numberOfEvaluationPoints);

		double integralNumeric = integrator.integrate(integrand, lowerBound, upperBound);
		double integralAnalyticValue = integralAnalytic.applyAsDouble(upperBound)-integralAnalytic.applyAsDouble(lowerBound);

		double error = integralNumeric-integralAnalyticValue;

		System.out.println("numeric: " + integralNumeric + "\tanalytic: " + integralAnalyticValue + "\t" + error);

		double tolerance = 3.0/Math.sqrt(numberOfEvaluationPoints);
		Assert.assertEquals("Integral", integralNumeric, integralAnalyticValue, tolerance);
	}

}
