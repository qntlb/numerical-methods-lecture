package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves;

import net.finmath.marketdata.model.AnalyticModel;

public interface ModelFactory {

	/**
	 * Create a model with a discount curve and a forward curve using the given zero rates in the discount curve.
	 *
	 * @param maturities The maturities.
	 * @param zeroRates The zero rates.
	 * @param discountCurveName The discount curve name.
	 * @param forwardCurveName The forward curve name.
	 * @return A model providing the two curves with the given zero rates.
	 */
	AnalyticModel getModel(double[] maturities, double[] zeroRates, String discountCurveName, String forwardCurveName);

	/**
	 * Create a model with a discount curve and a forward curve using a flat zero rate curve and a shift in one bucket.
	 *
	 * @param maturities The maturities.
	 * @param zeroRates The zero rates.
	 * @param discountCurveName The discount curve name.
	 * @param forwardCurveName The forward curve name.
	 * @param bucket The bucket to shift.
	 * @param shift A shift to be applied to the zero rate.
	 * @return A model providing the two curves with a zero rate of zeroRate for all but the shifted bucket.
	 */
	AnalyticModel getModelWithShift(double[] maturities, double[] zeroRates, String discountCurveName,
			String forwardCurveName, int bucket, double shift);

}