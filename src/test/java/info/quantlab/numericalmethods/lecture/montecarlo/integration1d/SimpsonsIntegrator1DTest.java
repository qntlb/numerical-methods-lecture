package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.Test;

public class SimpsonsIntegrator1DTest {

	@Test
	public void test() {

		final double lowerBound = 0.0;
		final double upperBound = 1.5;
		final DoubleUnaryOperator integrand = x -> Math.cos(x);

		final DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		final int numberOfEvaluationPoints = 101;

		final Integrator1D integrator = new SimpsonsIntegrator1DWithStreams(numberOfEvaluationPoints);

		final double integralValueNumeric = integrator.integrate(integrand, lowerBound, upperBound);
		final double integralValueAnalytic = integralAnalytic.applyAsDouble(upperBound)-integralAnalytic.applyAsDouble(lowerBound);

		final double error = integralValueNumeric-integralValueAnalytic;

		System.out.println(String.format("%30s:", integrator.getClass().getSimpleName()) + "\t" + "numeric: " + integralValueNumeric + "\tanalytic: " + integralValueAnalytic + "\t" + error);

		final double tolerance = 3.0/Math.sqrt(numberOfEvaluationPoints);
		Assert.assertEquals("Integral", integralValueNumeric, integralValueAnalytic, tolerance);
	}

}
