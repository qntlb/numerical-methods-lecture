package info.quantlab.numericalmethods.lecture.montecarlo.weighted;

import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.time.TimeDiscretization;

public interface MonteCarloBachelierModelFactory {

	/**
	 * Creates a Bachelier model
	 * \[
	 * d S(t) = r S(t) dt + sigma dW(t)
	 * \]
	 * with the specified parameters.
	 *
	 * @param initialValue The initial value S(0).
	 * @param riskFreeRate The risk free rate r.
	 * @param volatility The volatility sigma.
	 * @param timeDiscretization The time discretization of the Euler scheme.
	 * @param numberOfPaths The number of paths.
	 * @param seed The seed for the Monte-Carlo random number generator.
	 * @param shiftOfSimulatedValuesAfterFirstTimeStep A shift applied to the values in the first simulation time step. The Monte-Carlo weights correct for this shift.
	 * @return The corresponding model.
	 */
	AssetModelMonteCarloSimulationModel getModel(double initialValue, double riskFreeRate, double volatility,
			TimeDiscretization timeDiscretization, int numberOfPaths, int seed, double shiftOfSimulatedValuesAfterFirstTimeStep);

	/**
	 * Creates a Bachelier model
	 * \[
	 * d S(t) = r S(t) dt + sigma dW(t)
	 * \]
	 * with the specified parameters.
	 *
	 * @param initialValue The initial value S(0).
	 * @param riskFreeRate The risk free rate r.
	 * @param volatility The volatility sigma.
	 * @param timeDiscretization The time discretization of the Euler scheme.
	 * @param numberOfPaths The number of paths.
	 * @param seed The seed for the Monte-Carlo random number generator.
	 * @return The corresponding model.
	 */
	default AssetModelMonteCarloSimulationModel getModel(double initialValue, double riskFreeRate, double volatility,
			TimeDiscretization timeDiscretization, int numberOfPaths, int seed) {
		return getModel(initialValue, riskFreeRate, volatility, timeDiscretization, numberOfPaths, seed, 0.0);
	}

}