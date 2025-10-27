package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves;

import java.util.List;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.AnalyticProduct;

/**
 * Interface to be implemented by the solution of the assignment.
 */
public interface InterestRateCurveHedgePortfolio {

	/**
	 * Return the par rate of an interest rate swap that is valued on a single curve.
	 * (Classical single curve interest rates, where forward is calculated from the discount curve).
	 * 
	 * The swap should be idealized in the sense that fixed leg and float leg have the same periodLength and
	 * periodLength is the year fraction used in the payment.
	 * 
	 * @param periodLength The period length of the float and fixed leg.
	 * @param maturity The maturity of the swap (should be an integer multiple of periodLength)
	 * @param discountCurveName The discount curve name to be used to value the swap (using the given model).
	 * @param model The model to be used to value the swap.
	 * @return The par swap rate.
	 */
	double getParRate(double periodLength, double maturity, String discountCurveName, AnalyticModel model);
	
	/**
	 * Calculate the amounts \( \phi_{j} \) to be held in a hedge portfolio of the products <code>portfolioInstruments</code>,
	 * such that the portfolio replicates the risk (partial derivatives) of the given financial product productToHedge. 
	 * 
	 * @param portfolioInstruments The list of instruments the compose the hedge portfolio.
	 * @param maturities The maturities \( T_{i} \) that define the discount curve.
	 * @param zeroRates The zero rates \( r_{i} \) of the zero coupon forward bonds \( (T_{i-1} to T_{i}) \) for the given maturities (such that the discount factor is \( df(T_{i}) = df(T_{i-1}) \exp(-r_{i} (T_{i}-T-{i-1})) \) with df(0) = 1).
	 * @param discountCurveName The name of the discount curve.
	 * @param productToHedge The financial product for which the hedge portfolio should be created.
	 * @return the composition vector of a replication portfolio that hedges the given product.
	 */
	double[] getReplicationPortfolio(List<AnalyticProduct> portfolioInstruments, double[] maturities, double[] zeroRates, String discountCurveName, AnalyticProduct productToHedge);
}