package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves;

import net.finmath.marketdata.products.AnalyticProduct;

public interface ProductFactory {

	AnalyticProduct getSwap(double maturity, double rateFix, String forwardCurveName, String discountCurveName);

}