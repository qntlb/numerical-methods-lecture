package info.quantlab.numericalmethods.lecture.montecarlo.integration;

/**
 * Interface to be implemented by integrands that are may be integrated by an {@link Integrator}.
 *
 * @author Christian Fries
 */
public interface Integrand {

	double value(double[] arguments);

}
