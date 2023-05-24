package info.quantlab.numericalmethods.lecture.montecarlo.integration;

/**
 * Interface defining Integrator factories that only depend on the
 * specification of the numberOfEvaluationPoints.
 *
 * Examples are classical integrators that use a structured grid of evaluation points,
 * e.g. the (composite) Simpson's rule, but also Quasi- and Pseudo-Monte-Carlo methods
 * with fixed random number generators.
 *
 * @author Christian Fries
 */
public interface IntegratorFactory {

	/**
	 * Create an Integrator using specific minimum of evaluation points of numberOfValuationPoints.
	 *
	 * It is admissible that the integrator uses a higher than numberOfValuationPoints
	 * number of valuation points. For example, if the integrator requires a specific
	 * structure. For example, a Simpson's integrator in d dimensions may use
	 * m^d valuation points, where m is the smallest odd number such that m^d is
	 * larger or equal to numberOfValuationPoints.
	 *
	 * @param numberOfValuationPoints The number of valuation points to be used.
	 * @return A class implementing the Integrator interface.
	 */
	Integrator getIntegrator(long numberOfValuationPoints);

}