/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.check;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;

import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.InterestRateCurveHedgePortfolio;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.InterestRateCurveHedgePortfolioAssignment;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.ModelFactory;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves.ProductFactory;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.Curve;
import net.finmath.marketdata.model.curves.CurveInterpolation.ExtrapolationMethod;
import net.finmath.marketdata.model.curves.CurveInterpolation.InterpolationEntity;
import net.finmath.marketdata.model.curves.CurveInterpolation.InterpolationMethod;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.model.curves.DiscountCurveInterpolation;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveFromDiscountCurve;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.time.RegularSchedule;
import net.finmath.time.Schedule;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class InterestRateCurveHedgePortfolioChecker {

	final static double periodLength = 0.5;
	final static boolean isPayer = true;
	
	public enum Check {
		BASIC("basic functionality"),
		SWAP("valuation of a simple swap on a single discount curve"),
		PAR_RATE("calculation of par swap rate"),
		SENSITIVITIES("calculation of swap sensitivities");

		private final String name;

		Check(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * Perform the test of different sub-tasks (whatToCheck) on solution.
	 *
	 * Perform exception handling.
	 *
	 * @param solution Solution of MonteCarloIntegrationAssignment
	 * @param whatToCheck Name of the subtask.
	 * @return true if successful, otherwise false.
	 */
	public static boolean check(InterestRateCurveHedgePortfolioAssignment solution, Check whatToCheck) {
		System.out.println("Running " + whatToCheck + " test on " + solution.getClass().getCanonicalName());

		boolean success = false;
		try {
			switch(whatToCheck) {
			case BASIC:
			default:
			{
				success = checkBasics(solution);
			}
			break;
			case SWAP:
			{
				success = checkSwap(solution);
			}
			break;
			case PAR_RATE:
			{
				success = checkParRate(solution);
			}
			break;
			case SENSITIVITIES:
			{
				success = checkSensitivities(solution);
			}
			break;
			}
		}
		catch(Exception e) {
			System.out.println("\tTest '" + whatToCheck.getName() + "' failed with exception: " + e.getMessage());
			System.out.println("\nHere is a stack trace:");
			e.printStackTrace(System.out);
		}
		catch(Error e) {
			System.out.println("\tTest '" + whatToCheck.getName() + "' failed with error: " + e.getMessage());
		}


		if(!success) {
			System.out.println("Sorry, the test failed.");
		}
		else {
			System.out.println("Congratulation! You solved the " + whatToCheck + " part of the exercise.");
		}
		System.out.println("_".repeat(79));
		return success;
	}

	private static boolean checkSensitivities(InterestRateCurveHedgePortfolioAssignment solution) {
		ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		ProductFactory swapFactory = solution.getSwapFactory();
		InterestRateCurveHedgePortfolio hedgePortfolio = solution.getInterestRateCurveHedgePortfolio(modelFactory, swapFactory);

		/*
		 * Build a discount (wrapped in a model).
		 */
		String discountCurveName = "EURSTR";

		// The vector 1.0, 2.0, ..., 30.0
		double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();		
		// The vector 0.05, 0.05, ..., 0.05
		double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		/*
		 * Build a set of hedge swaps - all par-rate swaps
		 */

		int maturityStepForSwaps = 1;
		double[] maturitiesForSwap = IntStream.range(1, (int)30/maturityStepForSwaps+1).mapToDouble(x -> (double)maturityStepForSwaps * x).toArray();
		
		List<AnalyticProduct> parRateSwaps = new ArrayList<>();
		double periodLength = 0.5;
		boolean isPayer = true;
		for(double maturity : maturitiesForSwap) {
			double parRate = hedgePortfolio.getParRate(periodLength, maturity, discountCurveName, model);
			AnalyticProduct swap = swapFactory.getSwap(periodLength, maturity, parRate, isPayer, discountCurveName);
			parRateSwaps.add(swap);
		}
		
		
		/**
		 * Create non-standard swap
		 */
		AnalyticProduct swapToTest = swapFactory.getSwap(periodLength, 6.5, 0.05, isPayer, discountCurveName);
		double[] phi = hedgePortfolio.getReplicationPortfolio(parRateSwaps, maturities, zeroRates, discountCurveName, swapToTest);

		boolean success = true;
		for(int i=7; i<phi.length; i++) {
			success &= Math.abs(phi[i]) < 1E-12;
		}
		if(!success) System.out.println("\t\tSensitivity should be zero one bucket after the maturity of the product.");

		for(int i=0; i<=4; i++) {
			success &= Math.abs(phi[i]) < 1E-3;
		}
		if(!success) System.out.println("\t\tSensitivity should be small one bucket before the maturity of the product when using LINEAR.");

		success &= Math.abs(phi[5] - 0.493) < 1E-2;
		success &= Math.abs(phi[6] - 0.506) < 1E-2;
		if(!success) System.out.println("\t\tSensitivity in bucket 5Y and 6Y looks unplausible for 6.5Y swap.");

		return success;
	}

	private static boolean checkParRate(InterestRateCurveHedgePortfolioAssignment solution) {
		ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		ProductFactory swapFactory = solution.getSwapFactory();
		InterestRateCurveHedgePortfolio hedgePortfolio = solution.getInterestRateCurveHedgePortfolio(modelFactory, swapFactory);

		// The vector 1.0, 2.0, ..., 30.0
		double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();

		// The vector 0.05, 0.05, ..., 0.05
		double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		String discountCurveName = "EURSTR";

		/**
		 * Create non-standard swap
		 */
		AnalyticProduct swapToTest = swapFactory.getSwap(periodLength, 6.5, 0.05, isPayer, discountCurveName);

		AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		double parRateExpected = 0.05063;
		double parRateTolerance = 0.0001;
		double parRate = hedgePortfolio.getParRate(0.5, 6.5, discountCurveName, model);
		boolean success = Math.abs(parRate - parRateExpected) < parRateTolerance;

		if(!success) System.out.println("\t\tPar rate devitates from expected value " + parRateExpected + " by more than " + parRateTolerance + ".");
		return success;
	}

	private static boolean checkSwap(InterestRateCurveHedgePortfolioAssignment solution) {
		ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		ProductFactory swapFactory = solution.getSwapFactory();

		// The vector 1.0, 2.0, ..., 30.0
		double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();

		// The vector 0.05, 0.05, ..., 0.05
		double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		String discountCurveName = "EURSTR";

		/**
		 * Create non-standard swap
		 */
		AnalyticProduct swapToTest = swapFactory.getSwap(periodLength, 8, 0.04, isPayer, discountCurveName);

		AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		double valueExpected = 0.06922;
		double valueTolerance = 0.0001;
		double value = swapToTest.getValue(0.0, model);
		boolean success = Math.abs(value - valueExpected) < valueTolerance;

		if(!success) System.out.println("\t\tSwap valuation (" + value + ") devitates from expected value " + valueExpected + " by more than " + valueTolerance + ".");
		return success;
	}

	private static boolean checkBasics(InterestRateCurveHedgePortfolioAssignment solution) {
		ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		ProductFactory swapFactory = solution.getSwapFactory();
		InterestRateCurveHedgePortfolio hedgePortfolio = solution.getInterestRateCurveHedgePortfolio(modelFactory, swapFactory);

		/*
		 * Build a discount (wrapped in a model).
		 */
		String discountCurveName = "EURSTR";

		// The vector 1.0, 2.0, ..., 30.0
		double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();		
		// The vector 0.05, 0.05, ..., 0.05
		double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		/*
		 * Build a set of hedge swaps - all par-rate swaps
		 */

		int maturityStepForSwaps = 1;
		double[] maturitiesForSwap = IntStream.range(1, (int)30/maturityStepForSwaps+1).mapToDouble(x -> (double)maturityStepForSwaps * x).toArray();
		
		List<AnalyticProduct> parRateSwaps = new ArrayList<>();
		double periodLength = 0.5;
		boolean isPayer = true;
		for(double maturity : maturitiesForSwap) {
			double parRate = hedgePortfolio.getParRate(periodLength, maturity, discountCurveName, model);
			AnalyticProduct swap = swapFactory.getSwap(periodLength, maturity, parRate, isPayer, discountCurveName);
			parRateSwaps.add(swap);
		}
		
		/*
		 * Create non-standard swap
		 */
		AnalyticProduct swapToHedge = swapFactory.getSwap(periodLength, 6.5, 0.05, isPayer, discountCurveName);

		double parRate = hedgePortfolio.getParRate(0.5, 6.5, discountCurveName, modelFactory.getModel(maturities, zeroRates, discountCurveName));
		Assertions.assertTrue((0 < parRate && parRate < 0.1), "par swap rate pausibility: should be between 0.0 and 0.10 for our model");

		double[] phi = hedgePortfolio.getReplicationPortfolio(parRateSwaps, maturities, zeroRates, discountCurveName, swapToHedge);
		Assertions.assertTrue((maturities.length == phi.length), "length of hedge portfolio matches number of maturities");

		return true;
	}

	public static AnalyticProduct getSwap(double maturity, double periodLength, double rateFix, String forwardCurveName, String discountCurveName) {

		int numberOfPeriods = (int) Math.round(maturity/periodLength);

		TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0, numberOfPeriods, periodLength);
		Schedule legSchedule = new RegularSchedule(timeDiscretization);

		AnalyticProduct swapLegFloat = new SwapLeg(legSchedule, forwardCurveName, 0.0, discountCurveName);
		AnalyticProduct swapLegFix = new SwapLeg(legSchedule, null, rateFix, discountCurveName);

		AnalyticProduct swap = new Swap(swapLegFloat, swapLegFix);

		return swap;
	}

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
	public static AnalyticModel getModelWithShift(
			InterpolationMethod interpolationMethod, ExtrapolationMethod extrapolationMethod, InterpolationEntity interpolationEntity,
			double[] maturities, double[] zeroRates, String discountCurveName, String forwardCurveName, int bucket, double shift) {

		final LocalDate referenceDate = LocalDate.now();

		// Shift bucket in zero rates
		double[] zeroRatesShifted = Arrays.copyOf(zeroRates, zeroRates.length);
		zeroRatesShifted[bucket] += shift;

		// Get discount factors
		double[] discountFactors = getDiscountFactorsFromForwardZeroRates(maturities, zeroRatesShifted);

		// Create discount curve
		DiscountCurve discountCurve = DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(discountCurveName, referenceDate,
				maturities,
				discountFactors,
				null,
				interpolationMethod, extrapolationMethod, interpolationEntity);

		ForwardCurve forwardCurve = new ForwardCurveFromDiscountCurve(forwardCurveName, discountCurve.getName(), referenceDate, null);

		AnalyticModel model = new AnalyticModelFromCurvesAndVols(new Curve[] { discountCurve, forwardCurve });

		return model;
	}

	private static double[] getDiscountFactorsFromForwardZeroRates(double[] maturities, double[] zeroRatesShifted) {

		double[] discountFactors = new double[maturities.length];

		double discountFactor = 1.0;
		double maturityPrevious = 0.0;
		for(int i=0; i<maturities.length; i++) {
			double maturity = maturities[i];
			discountFactor *= Math.exp(- zeroRatesShifted[i] * (maturity-maturityPrevious));
			discountFactors[i] = discountFactor;
			maturityPrevious = maturity;
		}

		return discountFactors;
	}
}
