package info.quantlab.numericalmethods.lecture.montecarlo.integration;

public interface Integrator {

	double integrate(Integrand integrand, IntegrationDomain integrationDomain);

}
