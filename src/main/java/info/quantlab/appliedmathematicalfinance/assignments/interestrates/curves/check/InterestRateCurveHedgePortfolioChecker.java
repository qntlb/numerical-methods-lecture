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
import net.finmath.functions.LinearAlgebra;
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

	static final double periodLength = 0.5;
	static final boolean isPayer = true;

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
		catch(final Exception e) {
			System.out.println("\tTest '" + whatToCheck.getName() + "' failed with exception: " + e.getMessage());
			System.out.println("\nHere is a stack trace:");
			e.printStackTrace(System.out);
		}
		catch(final Error e) {
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
		final ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		final ProductFactory swapFactory = solution.getSwapFactory();
		final InterestRateCurveHedgePortfolio hedgePortfolio = solution.getInterestRateCurveHedgePortfolio(modelFactory, swapFactory);

		/*
		 * Build a discount (wrapped in a model).
		 */
		final String discountCurveName = "EURSTR";

		// The vector 1.0, 2.0, ..., 30.0
		final double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();
		// The vector 0.05, 0.05, ..., 0.05
		final double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		final AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		/*
		 * Build a set of hedge swaps - all par-rate swaps
		 */

		final int maturityStepForSwaps = 1;
		final double[] maturitiesForSwap = IntStream.range(1, 30/maturityStepForSwaps+1).mapToDouble(x -> (double)maturityStepForSwaps * x).toArray();

		final List<AnalyticProduct> parRateSwaps = new ArrayList<>();
		final double periodLength = 0.5;
		final boolean isPayer = true;
		for(final double maturity : maturitiesForSwap) {
			final double parRate = hedgePortfolio.getParRate(periodLength, maturity, discountCurveName, model);
			final AnalyticProduct swap = swapFactory.getSwap(periodLength, maturity, parRate, isPayer, discountCurveName);
			parRateSwaps.add(swap);
		}


		/**
		 * Create non-standard swap
		 */
		final AnalyticProduct swapToTest = swapFactory.getSwap(periodLength, 6.5, 0.05, isPayer, discountCurveName);
		final double[] phi = hedgePortfolio.getReplicationPortfolio(parRateSwaps, maturities, zeroRates, discountCurveName, swapToTest);
		assertReplicationWeights(phi, parRateSwaps.size(), "30 buckets, 30 instruments");

		boolean success = true;
		for(int i=7; i<phi.length; i++) {
			success &= Math.abs(phi[i]) < 1E-12;
		}
		if(!success) {
			System.out.println("\t\tSensitivity should be zero one bucket after the maturity of the product.");
		}

		for(int i=0; i<=4; i++) {
			success &= Math.abs(phi[i]) < 1E-3;
		}
		if(!success) {
			System.out.println("\t\tSensitivity should be small one bucket before the maturity of the product when using LINEAR.");
		}

		success &= Math.abs(phi[5] - 0.493) < 1E-2;
		success &= Math.abs(phi[6] - 0.506) < 1E-2;
		if(!success) {
			System.out.println("\t\tSensitivity in bucket 5Y and 6Y looks unplausible for 6.5Y swap.");
		}

		// The target need not be exactly replicable when there are fewer instruments than buckets.
		checkRectangularReplication(hedgePortfolio, modelFactory, swapFactory,
				new double[] { 1.0, 2.0, 3.0 }, new double[] { 0.03, 0.04, 0.05 },
				new double[] { 3.0, 1.0 }, new double[] { 0.02, 0.06 }, 2.5, 0.04);
		// The last instrument is essential; padding a square-only solution with zeros must fail.
		// Duplicate instruments also allow valid solutions other than the minimum-norm solution.
		checkRectangularReplication(hedgePortfolio, modelFactory, swapFactory,
				new double[] { 1.0, 2.0 }, new double[] { 0.03, 0.04 },
				new double[] { 1.0, 1.0, 2.0 }, new double[] { 0.01, 0.01, 0.08 }, 2.0, 0.08);

		return success;
	}

	private static void checkRectangularReplication(InterestRateCurveHedgePortfolio hedgePortfolio,
			ModelFactory modelFactory, ProductFactory swapFactory, double[] maturities, double[] zeroRates,
			double[] instrumentMaturities, double[] fixedRates, double targetMaturity, double targetFixedRate) {
		final String discountCurveName = "EURSTR";
		final String scenario = maturities.length + " buckets, " + instrumentMaturities.length + " instruments";
		final List<AnalyticProduct> instruments = new ArrayList<>();
		for(int j=0; j<instrumentMaturities.length; j++) {
			instruments.add(swapFactory.getSwap(periodLength, instrumentMaturities[j], fixedRates[j], isPayer, discountCurveName));
		}
		final AnalyticProduct target = swapFactory.getSwap(periodLength, targetMaturity, targetFixedRate, isPayer, discountCurveName);
		final double[] weights = hedgePortfolio.getReplicationPortfolio(instruments, maturities, zeroRates, discountCurveName, target);
		assertReplicationWeights(weights, instruments.size(), scenario);

		// Independently measure sensitivities with smaller, symmetric shifts. All contractual terms stay fixed.
		final double shift = 1E-5;
		final double[][] sensitivities = new double[maturities.length][instruments.size()];
		final double[] targetSensitivities = new double[maturities.length];
		for(int i=0; i<maturities.length; i++) {
			final AnalyticModel up = modelFactory.getModelWithShift(maturities, zeroRates, discountCurveName, i, shift);
			final AnalyticModel down = modelFactory.getModelWithShift(maturities, zeroRates, discountCurveName, i, -shift);
			targetSensitivities[i] = (target.getValue(0.0, up) - target.getValue(0.0, down)) / (2 * shift);
			for(int j=0; j<instruments.size(); j++) {
				sensitivities[i][j] = (instruments.get(j).getValue(0.0, up) - instruments.get(j).getValue(0.0, down)) / (2 * shift);
			}
		}
		final double[] referenceWeights = LinearAlgebra.solveLinearEquationSVD(sensitivities, targetSensitivities);
		final double minimumResidual = getResidualNorm(sensitivities, targetSensitivities, referenceWeights);
		final double actualResidual = getResidualNorm(sensitivities, targetSensitivities, weights);
		double targetNorm = 0.0;
		for(final double sensitivity : targetSensitivities) {
			targetNorm = Math.hypot(targetNorm, sensitivity);
		}
		// Permit the forward difference with shift 0.001 suggested by the exercise.
		final double tolerance = 2E-3 * targetNorm + 1E-10;
		Assertions.assertTrue(Double.isFinite(actualResidual) && actualResidual <= minimumResidual + tolerance,
				scenario + ": hedge sensitivity residual " + actualResidual
				+ " exceeds least-squares minimum " + minimumResidual + " (tolerance " + tolerance + ")");
	}

	private static void assertReplicationWeights(double[] weights, int numberOfInstruments, String scenario) {
		Assertions.assertNotNull(weights, scenario + ": replication weights must not be null");
		Assertions.assertEquals(numberOfInstruments, weights.length, scenario + ": return one weight per supplied instrument");
		for(int j=0; j<weights.length; j++) {
			Assertions.assertTrue(Double.isFinite(weights[j]), scenario + ": weight " + j + " must be finite");
		}
	}

	private static double getResidualNorm(double[][] sensitivities, double[] targetSensitivities, double[] weights) {
		double norm = 0.0;
		for(int i=0; i<targetSensitivities.length; i++) {
			double residual = -targetSensitivities[i];
			for(int j=0; j<weights.length; j++) {
				residual += sensitivities[i][j] * weights[j];
			}
			norm = Math.hypot(norm, residual);
		}
		return norm;
	}

	private static boolean checkParRate(InterestRateCurveHedgePortfolioAssignment solution) {
		final ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		final ProductFactory swapFactory = solution.getSwapFactory();
		final InterestRateCurveHedgePortfolio hedgePortfolio = solution.getInterestRateCurveHedgePortfolio(modelFactory, swapFactory);

		// The vector 1.0, 2.0, ..., 30.0
		final double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();

		// The vector 0.05, 0.05, ..., 0.05
		final double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		final String discountCurveName = "EURSTR";

		/**
		 * Create non-standard swap
		 */
		final AnalyticProduct swapToTest = swapFactory.getSwap(periodLength, 6.5, 0.05, isPayer, discountCurveName);

		final AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		final double parRateExpected = 0.05063;
		final double parRateTolerance = 0.0001;
		final double parRate = hedgePortfolio.getParRate(0.5, 6.5, discountCurveName, model);
		final boolean success = Math.abs(parRate - parRateExpected) < parRateTolerance;

		if(!success) {
			System.out.println("\t\tPar rate devitates from expected value " + parRateExpected + " by more than " + parRateTolerance + ".");
		}
		return success;
	}

	private static boolean checkSwap(InterestRateCurveHedgePortfolioAssignment solution) {
		final ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		final ProductFactory swapFactory = solution.getSwapFactory();

		// The vector 1.0, 2.0, ..., 30.0
		final double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();

		// The vector 0.05, 0.05, ..., 0.05
		final double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		final String discountCurveName = "EURSTR";

		/**
		 * Create non-standard swap
		 */
		final AnalyticProduct swapToTest = swapFactory.getSwap(periodLength, 8, 0.04, isPayer, discountCurveName);

		final AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		final double valueExpected = 0.06922;
		final double valueTolerance = 0.0001;
		final double value = swapToTest.getValue(0.0, model);
		final boolean success = Math.abs(value - valueExpected) < valueTolerance;

		if(!success) {
			System.out.println("\t\tSwap valuation (" + value + ") devitates from expected value " + valueExpected + " by more than " + valueTolerance + ".");
		}
		return success;
	}

	private static boolean checkBasics(InterestRateCurveHedgePortfolioAssignment solution) {
		final ModelFactory modelFactory = new ModelFactoryImplementation(InterpolationMethod.LINEAR, ExtrapolationMethod.LINEAR, InterpolationEntity.LOG_OF_VALUE);
		final ProductFactory swapFactory = solution.getSwapFactory();
		final InterestRateCurveHedgePortfolio hedgePortfolio = solution.getInterestRateCurveHedgePortfolio(modelFactory, swapFactory);

		/*
		 * Build a discount (wrapped in a model).
		 */
		final String discountCurveName = "EURSTR";

		// The vector 1.0, 2.0, ..., 30.0
		final double[] maturities = IntStream.range(0, 30).mapToDouble(x -> 1.0 + x).toArray();
		// The vector 0.05, 0.05, ..., 0.05
		final double[] zeroRates = IntStream.range(0, 30).mapToDouble(x -> 0.05).toArray();

		final AnalyticModel model = modelFactory.getModel(maturities, zeroRates, discountCurveName);

		/*
		 * Build a set of hedge swaps - all par-rate swaps
		 */

		final int maturityStepForSwaps = 1;
		final double[] maturitiesForSwap = IntStream.range(1, 30/maturityStepForSwaps+1).mapToDouble(x -> (double)maturityStepForSwaps * x).toArray();

		final List<AnalyticProduct> portfolioInstruments = new ArrayList<>();
		final double periodLength = 0.5;
		final boolean isPayer = true;
		for(final double maturity : maturitiesForSwap) {
			final double parRate = hedgePortfolio.getParRate(periodLength, maturity, discountCurveName, model);
			final AnalyticProduct swap = swapFactory.getSwap(periodLength, maturity, parRate, isPayer, discountCurveName);
			portfolioInstruments.add(swap);
		}

		/*
		 * Create non-standard swap
		 */
		final AnalyticProduct productToHedge = swapFactory.getSwap(periodLength, 6.5, 0.05, isPayer, discountCurveName);

		final double parRate = hedgePortfolio.getParRate(0.5, 6.5, discountCurveName, modelFactory.getModel(maturities, zeroRates, discountCurveName));
		Assertions.assertTrue((0 <= parRate && parRate <= 0.1), "par swap rate pausibility: should be between 0.0 and 0.10 for our model");

		final double[] phi = hedgePortfolio.getReplicationPortfolio(portfolioInstruments, maturities, zeroRates, discountCurveName, productToHedge);
		assertReplicationWeights(phi, portfolioInstruments.size(), "basic functionality");

		return true;
	}

	public static AnalyticProduct getSwap(double maturity, double periodLength, double rateFix, String forwardCurveName, String discountCurveName) {

		final int numberOfPeriods = (int) Math.round(maturity/periodLength);

		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0, numberOfPeriods, periodLength);
		final Schedule legSchedule = new RegularSchedule(timeDiscretization);

		final AnalyticProduct swapLegFloat = new SwapLeg(legSchedule, forwardCurveName, 0.0, discountCurveName);
		final AnalyticProduct swapLegFix = new SwapLeg(legSchedule, null, rateFix, discountCurveName);

		final AnalyticProduct swap = new Swap(swapLegFloat, swapLegFix);

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
		final double[] zeroRatesShifted = Arrays.copyOf(zeroRates, zeroRates.length);
		zeroRatesShifted[bucket] += shift;

		// Get discount factors
		final double[] discountFactors = getDiscountFactorsFromForwardZeroRates(maturities, zeroRatesShifted);

		// Create discount curve
		final DiscountCurve discountCurve = DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(discountCurveName, referenceDate,
				maturities,
				discountFactors,
				null,
				interpolationMethod, extrapolationMethod, interpolationEntity);

		final ForwardCurve forwardCurve = new ForwardCurveFromDiscountCurve(forwardCurveName, discountCurve.getName(), referenceDate, null);

		final AnalyticModel model = new AnalyticModelFromCurvesAndVols(new Curve[] { discountCurve, forwardCurve });

		return model;
	}

	private static double[] getDiscountFactorsFromForwardZeroRates(double[] maturities, double[] zeroRatesShifted) {

		final double[] discountFactors = new double[maturities.length];

		double discountFactor = 1.0;
		double maturityPrevious = 0.0;
		for(int i=0; i<maturities.length; i++) {
			final double maturity = maturities[i];
			discountFactor *= Math.exp(- zeroRatesShifted[i] * (maturity-maturityPrevious));
			discountFactors[i] = discountFactor;
			maturityPrevious = maturity;
		}

		return discountFactors;
	}
}
