package info.quantlab.appliedmathematicalfinance.assignments.interestrates.discountcurve;

public interface DiscountCurveFactory {

	/**
	 * Create a discount curve for discount factors (zero bond values) with a given interpolation method.
	 * The length of the array of times needs to match the length of the array of discountFactors and it is assumed that the times array is ordered ascending.
	 * 
	 * @param times The maturities. Assumed to be ordered ascending.
	 * @param discountFactors The discount factors, i.e. zero bond values.
	 * @param interpolationMethod An interpolation method ("linear" or "log-linear")
	 * @return An object implementing DiscountCurve.
	 */
	DiscountCurve createDiscountCurve(double[] times, double[] discountFactors, String interpolationMethod);

}
