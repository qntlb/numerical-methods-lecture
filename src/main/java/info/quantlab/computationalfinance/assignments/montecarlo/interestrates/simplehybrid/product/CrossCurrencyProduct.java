package info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.product;

import info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.model.SimpleCrossCurrencyModel;
import net.finmath.montecarlo.MonteCarloProduct;
import net.finmath.stochastic.RandomVariable;

public interface CrossCurrencyProduct extends MonteCarloProduct {

	/**
	 * The method should return a random variable V such that \( E(V | F_{t}) \) represents the valuation of the
	 * financial product in evaluation time. For t=0 it is expected that the unconditional expectation E(V) represents
	 * the value. For an European option we have V = X(T) N(t)/N(T) where X is the payoff in time T.
	 * 
	 * @param evaluationTime The evaluation time t.
	 * @param model The valuation model.
	 * @return A random variable such the E(V) represents the value of the derivative.
	 */
	RandomVariable getValue(double evaluationTime, SimpleCrossCurrencyModel model);
	
	default double getValue(SimpleCrossCurrencyModel model) {
		return getValue(0.0, model).getAverage();
	}
	
}
