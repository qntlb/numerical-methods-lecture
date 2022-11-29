package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.AnalyticProduct;

/**
 * Interface to be implemented by the solution of the assignment.
 */
public interface InterestRateCurveHedgePortfolioAssignment {

	double getParRate(double maturity, String forwardCurveName, String discountCurveName, AnalyticModel model);

	double[] getReplicationPortfolio(double[] maturities, double[] zeroRates, String discountCurveName, String forwardCurveName, AnalyticProduct swapToHedge);

}