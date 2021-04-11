package info.quantlab.numericalmethods.lecture.montecarlo.integration;

public interface MonteCarloIntegratorFactory {

	Integrator getIntegrator(long seed, long numberOfSamplePoints);

}
