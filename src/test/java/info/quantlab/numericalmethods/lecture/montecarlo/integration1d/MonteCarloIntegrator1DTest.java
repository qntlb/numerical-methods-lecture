package info.quantlab.numericalmethods.lecture.montecarlo.integration1d;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;

/**
 * Test of the Monte-Carlo integrator.
 *
 * The test may also be used to explore the impact of the seed. For some configurations
 * (function, domain, numberOfSEvaluationPoints, seed) we get a poor approximation
 * (large error). This is clear, because the convergence rate holds only in probability.
 *
 * An example is: lowerBound = 0.0; uppderBound = 5.0; integrand = x -> Math.cos(x);
 * numberOfEvaluationPoints = 1000; seed = 3141;
 *
 * @author Christian Fries
 */
public class MonteCarloIntegrator1DTest {

	@Test
	public void test() {

		double lowerBound = 0.0;
		double upperBound = 5.0;
		DoubleUnaryOperator integrand = x -> Math.cos(x);

		DoubleUnaryOperator integralAnalytic = x -> Math.sin(x);

		int numberOfEvaluationPoints = 100000;
		long seed = 3141;

		DoubleSupplier randomNumberGenerator = new MersenneTwister(seed);
		Integrator1D integrator = new MonteCarloIntegrator1DWithStreams(randomNumberGenerator, numberOfEvaluationPoints);

		double integralValueNumeric = integrator.integrate(integrand, lowerBound, upperBound);
		double integralValueAnalytic = integralAnalytic.applyAsDouble(upperBound)-integralAnalytic.applyAsDouble(lowerBound);

		double error = integralValueNumeric-integralValueAnalytic;

		System.out.println(String.format("%30s:", integrator.getClass().getSimpleName()) + "\t" + "numeric: " + integralValueNumeric + "\tanalytic: " + integralValueAnalytic + "\t" + error);

		double tolerance = 3.0/Math.sqrt(numberOfEvaluationPoints);

		Assert.assertEquals("Integral", integralValueNumeric, integralValueAnalytic, tolerance);
	}

}
