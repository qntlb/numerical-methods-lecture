package info.quantlab.numericalmethods.assignments.montecarlo.check;

import net.finmath.montecarlo.assetderivativevaluation.products.AssetMonteCarloProduct;
import net.finmath.time.TimeDiscretization;

/**
 * Interface to be implemented by the solution of the assignment.
 */
public interface AsianOptionWithBSControlVariateAssignment {

	/**
	 * The solution of the exercise consists of the implementation of a class,
	 * implementing a AssetMonteCarloProduct providing the valuation code
	 * of an Asian option with a control variate for Black-Scholes models.
	 * 
	 * Construct a product representing an Asian option on an asset S (where S the asset with index 0 from the model - single asset case).
	 * A(T) = 1/n sum_{i=1,...,n} S(t_i), where t_i are given observation times.
	 *
	 * @param strike The strike K in the option payoff max(A(T)-K,0).
	 * @param maturity The maturity T in the option payoff maxAS(T)-K,0)
	 * @param timesForAveraging The times t_i used in the calculation of A(T) = 1/n sum_{i=1,...,n} S(t_i).
	 * @return A class implementing AssetMonteCarloProduct
	 */
	AssetMonteCarloProduct getAsianOption(Double maturity, Double strike, TimeDiscretization timesForAveraging);
}
