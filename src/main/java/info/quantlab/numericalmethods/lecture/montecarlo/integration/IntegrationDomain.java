package info.quantlab.numericalmethods.lecture.montecarlo.integration;

/**
 * Interface to be implemented by integrands that are may be integrated by an {@link Integrator}.
 *
 * @author Christian Fries
 */
public interface IntegrationDomain {

	/**
	 * Applied the transformation \( f : [0,1]^{n} \mapsto R^{n} \) for a parameter in the unit cube.
	 *
	 * @param parametersOnUnitCube A parameter value x in \( [0,1]^{n} \).
	 * @return The value f(x).
	 */
	double[] fromUnitCube(double[] parametersOnUnitCube);

	/**
	 * Returns the dimension of the unit cube.
	 *
	 * @return The dimension n of the unit cube.
	 */
	int getDimension();

	/**
	 * For this transformation f : [0,1]^{n} \mapsto R^{n} this methods returns the value
	 * \[
	 *   det( df/dx (x) )
	 * \]
	 *
	 * This is required to transform the integral of a function h to the unit cube. It is
	 * \[
	 *   \int_A h(z) dz = \int_[0,1]^{n} h(f(x)) det(df/dx) dx
	 * \]
	 *
	 * @param parametersOnUnitCurve
	 * @return The determinant of df/dx - the scaleing of applied to an infinitesimal volume.
	 */
	double getDeterminantOfDifferential(double[] parametersOnUnitCurve);

}
