package info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.check;

import info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.SimpleLognormalCrossCurrencyModelAssignment;
import info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.model.SimpleCrossCurrencyModel;
import info.quantlab.computationalfinance.assignments.montecarlo.interestrates.simplehybrid.product.CrossCurrencyProduct;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class SimpleLognormalCrossCurrencyModelChecker {

	private static double evaluationTime = 0.0;
	private static double periodStart = 5.0;
	private static double periodEnd = 6.0;

	private static double dt = 1.0;
	private static int numberOfFactors = 3;
	private static int numberOfPaths = 1000000;
	private static int seed = 3141;

	private static double initialValueDomesticForwardRate = 0.04;
	private static double initialValueForeignForwardRate = 0.03;
	private static double initialValueFX = 1.2;
	private static double volatilityDomestic = 0.20;
	private static double volatilityForeign = 0.35;
	private static double volatiltiyFXForward = 0.30;
	private static double correlationDomFor = 0.3;
	private static double correlationFXDomenstic = 0.0;
	private static double correlationFXForeign = 0.8;

	private static double maturity = 6.0;
	private static double domesticZeroBond = 0.80;
	private static double foreignZeroBond = 0.90;

	private static double errorTolerance = 1E-4;

	public static boolean check(SimpleLognormalCrossCurrencyModelAssignment solution, String testCase) {

		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(evaluationTime, (int)Math.round(periodEnd/dt), dt);
		BrownianMotion brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, numberOfFactors, numberOfPaths, seed);

		SimpleCrossCurrencyModel simulationModel = solution.getSimpleCrossCurrencyModel(
				initialValueDomesticForwardRate, initialValueForeignForwardRate, initialValueFX,
				volatilityDomestic, volatilityForeign, volatiltiyFXForward, correlationDomFor,
				correlationFXDomenstic, correlationFXForeign, periodStart, maturity, domesticZeroBond, foreignZeroBond, brownianMotion);

		if(simulationModel == null) System.out.println("\tYour solution did not return a model. Implement getSimpleCrossCurrencyModel");

		/*
		 * Check model - does not require implementation of product.
		 */
		if(testCase.equals("Model")) {
			RandomVariable forwardDom = simulationModel.getForwardRate(0, periodStart, periodStart, periodEnd);
			RandomVariable forwardFor = simulationModel.getForwardRate(1, periodStart, periodStart, periodEnd);
			RandomVariable fxPay = simulationModel.getFXRate(1, periodEnd);
			RandomVariable fxEval = simulationModel.getFXRate(1, 0.0);
			RandomVariable numeraireAtPayment = simulationModel.getNumeraire(periodEnd);
			RandomVariable numeraireAtEval = simulationModel.getNumeraire(0.0);

			double payDomestic = forwardDom.div(numeraireAtPayment).mult(numeraireAtEval).expectation().doubleValue();
			double payForeign = forwardFor.mult(fxPay).div(numeraireAtPayment).mult(numeraireAtEval).div(fxEval).expectation().doubleValue();

			boolean success = true;
			if(Math.abs(payDomestic - initialValueDomesticForwardRate*domesticZeroBond) < 0.001) {
				System.out.println("\tModel domestic rates / numeraire seem ok.");
			}
			else {
				System.out.println("\tModel reports inconsisting foreign rates / fx / numeraire.");
				success = false;
			}
			if(Math.abs(payForeign - initialValueForeignForwardRate*foreignZeroBond) < 0.001) {
				System.out.println("\tModel foreign rates / fx / numeraire seem ok.");
			}
			else {
				System.out.println("\tModel reports inconsisting domestic rates / numeraire.");
				success = false;
			}

			return success;
		}


		/*
		 * Check product
		 */

		double fixingTime = periodStart;
		double strike = 0.035;

		int currency = 0;
		boolean isQuanto;
		boolean isInAdvance;
		double paymentTime;
		double valueAnalytic;

		switch(testCase) {
		case "Caplet Domestic":
		{
			currency = 0;
			isQuanto = false;
			isInAdvance = false;
			paymentTime = isInAdvance ? periodStart : periodEnd;

			valueAnalytic = AnalyticFormulas.blackModelCapletValue(initialValueDomesticForwardRate, volatilityDomestic, periodStart, strike, periodEnd-periodStart, domesticZeroBond);
		}
		break;
		case "Caplet Foreign":
		{
			currency = 1;
			isQuanto = false;
			isInAdvance = false;
			paymentTime = isInAdvance ? periodStart : periodEnd;

			valueAnalytic = AnalyticFormulas.blackModelCapletValue(initialValueForeignForwardRate, volatilityForeign, periodStart, strike, periodEnd-periodStart, foreignZeroBond)
					*initialValueFX;
		}
		break;
		case "Caplet Quanto":
		{
			currency = 1;
			isQuanto = true;
			isInAdvance = false;
			paymentTime = isInAdvance ? periodStart : periodEnd;

			double quantoAdjustment = Math.exp(-volatilityForeign*volatiltiyFXForward*correlationFXForeign*periodStart);
			valueAnalytic = AnalyticFormulas.blackModelCapletValue(initialValueForeignForwardRate*quantoAdjustment, volatilityForeign, periodStart, strike, periodEnd-periodStart, domesticZeroBond);
		}
		break;
		case "Caplet Domestic with In-Advance Payment":
		{
			currency = 0;
			isQuanto = false;
			isInAdvance = true;
			paymentTime = isInAdvance ? periodStart : periodEnd;

			double valueAnalyticDomestic = AnalyticFormulas.blackModelCapletValue(initialValueDomesticForwardRate, volatilityDomestic, periodStart, strike, periodEnd-periodStart, domesticZeroBond);
			double inAdvanceAdjustment = Math.exp(+volatilityDomestic*volatilityDomestic*periodStart*(periodEnd-periodStart));
			valueAnalytic = (
					valueAnalyticDomestic
					+
					(initialValueDomesticForwardRate*(periodEnd-periodStart))*AnalyticFormulas.blackModelCapletValue(initialValueDomesticForwardRate*inAdvanceAdjustment, volatilityDomestic, periodStart, strike, periodEnd-periodStart, domesticZeroBond)
					);
		}
		break;
		case "Caplet Foreign with In-Advance Payment":
		{
			currency = 1;
			isQuanto = false;
			isInAdvance = true;
			paymentTime = isInAdvance ? periodStart : periodEnd;

			double valueAnalyticForeign = AnalyticFormulas.blackModelCapletValue(initialValueForeignForwardRate, volatilityForeign, periodStart, strike, periodEnd-periodStart, foreignZeroBond);

			double inAdvanceAdjustment = Math.exp(+volatilityForeign*volatilityForeign*periodStart*(periodEnd-periodStart));
			valueAnalytic = (
					valueAnalyticForeign
					+
					initialValueForeignForwardRate*(periodEnd-periodStart)*AnalyticFormulas.blackModelCapletValue(initialValueForeignForwardRate*inAdvanceAdjustment, volatilityForeign, periodStart, strike, periodEnd-periodStart, foreignZeroBond)
					)
					*initialValueFX;
		}
		break;
		case "Caplet Quanto with In-Advance Payment":
		{
			currency = 1;
			isQuanto = true;
			isInAdvance = true;
			paymentTime = isInAdvance ? periodStart : periodEnd;

			double inAdvanceAdjustment = Math.exp(+volatilityForeign*volatilityForeign*periodStart*(periodEnd-periodStart));
			double quantoAdjustment = Math.exp(-volatilityForeign*volatiltiyFXForward*correlationFXForeign*periodStart);
			double valueAnalyticForeign = AnalyticFormulas.blackModelCapletValue(initialValueForeignForwardRate*quantoAdjustment, volatilityForeign, periodStart, strike, periodEnd-periodStart, domesticZeroBond);
			valueAnalytic = (
					valueAnalyticForeign
					+
					(initialValueForeignForwardRate*(periodEnd-periodStart))*AnalyticFormulas.blackModelCapletValue(initialValueForeignForwardRate*quantoAdjustment*quantoAdjustment*inAdvanceAdjustment, volatilityForeign, periodStart, strike, periodEnd-periodStart, domesticZeroBond)
					);
		}
		break;
		default:
			throw new IllegalArgumentException("Unknown test case " + testCase);
		}

		CrossCurrencyProduct product = solution.getGeneralizedCaplet(
				currency, isQuanto, fixingTime, periodStart, periodEnd, paymentTime, strike);

		if(product == null) System.out.println("\tYour solution did not return a product. Implement getGeneralizedCaplet");

		double value = Double.NaN;
		try {
			if(product != null) {
				RandomVariable valuation = product.getValue(evaluationTime, simulationModel);
				value = valuation.getAverage();
			}
		}
		catch(Exception e) {
			System.out.println("\tThe valuation failed with an exception:");
			e.printStackTrace();
		}

		System.out.println("\tValue............: " + value);
		System.out.println("\tValue analytic...: " + valueAnalytic);

		return Math.abs(valueAnalytic - value) < errorTolerance;
	}

}
