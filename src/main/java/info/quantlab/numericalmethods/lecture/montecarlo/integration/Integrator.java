package info.quantlab.numericalmethods.lecture.montecarlo.integration;

/**
 * Interface to be implemented by integrators, i.e., classes that allow the integration
 * of functions (Integrand) over a given domain (IntegrationDomain).
 *
 * @author Christian Fries
 */
public interface Integrator {

	/**
	 * Calculate the integral \( \int_A f(z) dz \).
	 *
	 * @param integrand The integrand f.
	 * @param integrationDomain The integration domain A (the transformation z = g(x)) from a unit cube to A).
	 * @return The integral \( \int_A f(z) dz \).
	 */
	double integrate(Integrand integrand, IntegrationDomain integrationDomain);

}
