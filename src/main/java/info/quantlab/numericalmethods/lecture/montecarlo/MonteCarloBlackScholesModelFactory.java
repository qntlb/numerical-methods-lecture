package info.quantlab.numericalmethods.lecture.montecarlo;

import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.time.TimeDiscretization;

public interface MonteCarloBlackScholesModelFactory {

	/**
	 * Creates a Black-Scholes model with the specified parameters.
	 * 
	 * @param initialValue The initial value S(0).
	 * @param riskFreeRate The risk free rate r.
	 * @param volatility The volatility sigma.
	 * @param timeDiscretization The time discretization of the Euler scheme.
	 * @param numberOfPaths The number of paths.
	 * @return The corresponding model.
	 */
	AssetModelMonteCarloSimulationModel getModel(double initialValue, double riskFreeRate, double volatility,
			TimeDiscretization timeDiscretization, int numberOfPaths);

}