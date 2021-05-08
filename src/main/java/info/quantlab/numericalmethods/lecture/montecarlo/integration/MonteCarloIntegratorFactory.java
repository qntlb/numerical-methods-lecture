package info.quantlab.numericalmethods.lecture.montecarlo.integration;

import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator;

public interface MonteCarloIntegratorFactory {

	Integrator getIntegrator(RandomNumberGenerator randomNumberGenerator, long numberOfSamplePoints);

}
