package info.quantlab.appliedmathematicalfinance.assignments.interestrates.discountcurve;

public interface DiscountCurve {

	double getDiscountFactor(double maturity);

	default double getForwardRate(double periodStart) {
		return getForwardRate(periodStart, periodStart+1);
	}	

	default double getForwardRate(double periodStart, double periodEnd) {
		return (getDiscountFactor(periodStart)/getDiscountFactor(periodEnd) - 1.0) / (periodEnd-periodStart);
	}	
}
