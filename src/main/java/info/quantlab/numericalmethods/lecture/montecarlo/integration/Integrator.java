package info.quantlab.numericalmethods.lecture.montecarlo.integration;

/**
 * Interface to be implemented by integrators, i.e., classes that allow the integration
 * of functions (Integrand) over a given domain (IntegrationDomain).
 *
 * @author Christian Fries
 */
public interface Integrator {

	/**
	 * Calculate the integral \( \int_A f(x) dx \).
	 *
	 * @param integrand The integrand f.
	 * @param integrationDomain The integration domain A.
	 * @return The integral \( \int_A f(x) dx \).
	 */
	double integrate(Integrand integrand, IntegrationDomain integrationDomain);

}
