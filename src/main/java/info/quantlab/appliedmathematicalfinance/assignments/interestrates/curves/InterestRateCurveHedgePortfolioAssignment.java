package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves;

/**
 * Interface to be implemented by the solution of the assignment.
 */
public interface InterestRateCurveHedgePortfolioAssignment {

	ProductFactory getSwapFactory();
	
	InterestRateCurveHedgePortfolio getInterestRateCurveHedgePortfolio(ModelFactory modelFactory, ProductFactory swapFactory);

}