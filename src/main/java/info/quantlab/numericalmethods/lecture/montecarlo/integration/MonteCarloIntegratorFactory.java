package info.quantlab.numericalmethods.lecture.montecarlo.integration;

import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator;

/**
 * Interface defining Integrator factories that require randomNumberGenrators (Monte-Carlo method).
 *
 * @author Christian Fries
 */
public interface MonteCarloIntegratorFactory extends IntegratorFactory {

	/**
	 * Create a Monte-Carlo integrator using a specific RandomNumberGenerator and numberOfSamplePoints.
	 *
	 * @param randomNumberGenerator The RandomNumberGenerator to be used.
	 * @param numberOfSamplePoints The number of sample points to be used.
	 * @return A class implementing the Integrator interface.
	 */
	Integrator getIntegrator(RandomNumberGenerator randomNumberGenerator, long numberOfSamplePoints);

	@Override
	default Integrator getIntegrator(long numberOfSamplePoints) { return getIntegrator(new MersenneTwister(3141), numberOfSamplePoints); }

}
