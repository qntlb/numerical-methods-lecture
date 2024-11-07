package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves;

import net.finmath.marketdata.products.AnalyticProduct;

public interface ProductFactory {

	/**
	 * Create a simple text-book swap that is valued on a single curve.
	 * 
	 * The two swap legs should have the same frequency (periodLength).
	 * 
	 * @param periodLength The period length (as a fraction of one year.
	 * @param maturity The maturity (in years from t=0 (now))
	 * @param rateFix The fixed swap rate K.
	 * @param isPayer If true, the swap pays the fixed rate (rateFix) and receives the float rate (L-K).
	 * @param discountCurveName The name of the discount curve that should be requested from the model. This curve is used to calculate the forward rate L.
	 * @return A swap implementing the interface <code>AnalyticProduct</code>.
	 */
	AnalyticProduct getSwap(double periodLength, double maturity, double rateFix, boolean isPayer, String discountCurveName);

}