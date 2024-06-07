package info.quantlab.numericalmethods.lecture.montecarlo.integration;

/**
 * Interface to be implemented by integrands that are may be integrated by an {@link Integrator}.
 *
 * @author Christian Fries
 */
public interface IntegrationDomain {

	/**
	 * Applies the transformation \( g : [0,1]^{n} \mapsto R^{n} \) for a parameter in the unit cube.
	 *
	 * @param parametersOnUnitCube A parameter value x in \( [0,1]^{n} \).
	 * @return The value g(x).
	 */
	double[] fromUnitCube(double[] parametersOnUnitCube);

	/**
	 * Returns the dimension of the unit cube.
	 *
	 * @return The dimension n of the unit cube.
	 */
	int getDimension();

	/**
	 * For this transformation g : [0,1]^{n} \mapsto R^{n} this methods returns the value
	 * \[
	 *   det( dg/dx (x) )
	 * \]
	 *
	 * This is required to transform the integral of a function f to the unit cube. It is
	 * \[
	 *   \int_A f(z) dz = \int_[0,1]^{n} f(g(x)) det(dg/dx) dx
	 * \]
	 *
	 * @param parametersOnUnitCurve the argument x in \( [0,1]^{n} \)
	 * @return The determinant of dg/dx - the scaling applied to an infinitesimal volume.
	 */
	double getDeterminantOfDifferential(double[] parametersOnUnitCurve);

}
