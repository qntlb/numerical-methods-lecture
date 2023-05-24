package info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid;

import info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.model.SimpleCrossCurrencyModel;
import info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.product.CrossCurrencyProduct;
import net.finmath.montecarlo.BrownianMotion;

public interface SimpleLognormalCrossCurrencyModelAssignment {

	/**
	 * The solution of the exercise consists of the implementation of a class,
	 * implementing a SimpleCrossCurrencyModel providing a simulation of
	 * a simple cross currency model simulating a single interest rate period.
	 *
	 */
	SimpleCrossCurrencyModel getSimpleCrossCurrencyModel(double initialValueDomesticForwardRate,
			double initialValueForeignForwardRate, double initialValueFX, double volatilityDomestic,
			double volatilityForeign, double volatiltiyFXForward, double correlationDomFor,
			double correlationFXDomenstic, double correlationFXForeign, double periodStart, double maturity,
			double domesticZeroBond, double foreignZeroBond, BrownianMotion brownianMotion);

	CrossCurrencyProduct getGeneralizedCaplet(int currency, boolean isQuanto, double fixingTime, double periodStart,
			double periodEnd, double paymentTime, double strike);

}
