package info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.model;

import net.finmath.montecarlo.MonteCarloSimulationModel;
import net.finmath.stochastic.RandomVariable;

/**
 * Interface describing a simple (Monte-Carlo) cross-currency interest rate model.
 *
 * @author Christian Fries
 */
public interface SimpleCrossCurrencyModel extends MonteCarloSimulationModel {

	/**
	 * Return the forward rate observed at time t for the period T<sub>1</sub> to T<sub>2</sub>
	 * for the given currency. Currency are enumerated via integers here (0 = domestic).
	 *
	 * @param currency Index of the currency.
	 * @param time Observation time.
	 * @param periodStart Period start time.
	 * @param periodEnd Period end time.
	 * @return RandomVariable representing the forward rate.
	 */
	RandomVariable getForwardRate(int currency, double time, double periodStart, double periodEnd);

	/**
	 * Return the currency exchange rate observed at time t
	 * for the given currency. Currency are enumerated via integers here (0 = domestic).
	 * The fx rate will always be numerared in domestic currency. It is
	 * the value of 1 unit of the given currency in domestic currency.
	 * Unit is 1 dom/1 for.
	 *
	 * @param currency Index of the currency.
	 * @param time Observation time.
	 * @return RandomVariable representing the fx rate.
	 */
	RandomVariable getFXRate(int currency, double time);

	/**
	 * The models numeraire.
	 *
	 * @param time The time at which the numerarie is observed.
	 * @return RandomVariable representing the numeraire at the given time.
	 */
	RandomVariable getNumeraire(double time);
}
