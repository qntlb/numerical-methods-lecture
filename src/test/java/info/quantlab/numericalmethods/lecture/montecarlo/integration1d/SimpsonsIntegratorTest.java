package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.Test;

import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.Integrator1D;
import info.quantlab.numericalmethods.lecture.montecarlo.integration1d.SimpsonsIntegrator;

public class SimpsonsIntegratorTest {

	@Test
	public void test() {

		double lowerBound = 1.0;
		double upperBound = 5.0;
		DoubleUnaryOperator integrand = x -> Math.cos(x);

		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		int numberOfEvaluationPoints = 100;

		Integrator1D integrator = new SimpsonsIntegrator(numberOfEvaluationPoints);

		double integralNumeric = integrator.integrate(integrand, lowerBound, upperBound);
		double integralAnalyticValue = integralAnalytic.applyAsDouble(upperBound)-integralAnalytic.applyAsDouble(lowerBound);

		double error = integralNumeric-integralAnalyticValue;

		System.out.println("numeric: " + integralNumeric + "\tanalytic: " + integralAnalyticValue + "\t" + error);

		double tolerance = 3.0/Math.sqrt(numberOfEvaluationPoints);
		Assert.assertEquals("Integral", integralNumeric, integralAnalyticValue, tolerance);
	}

}
