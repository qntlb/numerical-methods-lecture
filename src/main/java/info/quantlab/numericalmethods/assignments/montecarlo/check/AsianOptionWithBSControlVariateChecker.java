/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.assignments.montecarlo.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.IndependentIncrements;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AssetMonteCarloProduct;
import net.finmath.montecarlo.model.AbstractProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel.Scheme;
import net.finmath.montecarlo.process.MonteCarloProcessFromProcessModel;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class AsianOptionWithBSControlVariateChecker {

	public enum Check {
		BASIC("basic variance reduction"),
		WEAK("basic variance reduction"),
		STRONG("improved variance reduction"),
		STRONGER("improved variance reduction"),
		STRONGEST("strong variance reduction");

		private final String name;

		Check(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	// Model properties
	private static final double	initialValue   = 1.0;
	private static final double	riskFreeRate   = 0.05;
	private static final double	volatility     = 0.30;

	// Process discretization properties
	private static final int		numberOfPaths		= 200000;
	private static final int		numberOfTimeSteps	= 20;
	private static final double		deltaT				= 0.5;

	private static final int		seed				= 31415;

	/**
	 * Perform the test of different sub-tasks (whatToCheck) on solution.
	 *
	 * Perform exception handling.
	 *
	 * @param solution Solution of MonteCarloIntegrationAssignment
	 * @param whatToCheck Name of the subtask.
	 * @return true if successful, otherwise false.
	 */
	public static boolean check(AsianOptionWithBSControlVariateAssignment solution, Check whatToCheck) {
		System.out.println("Running " + whatToCheck + " test on " + solution.getClass().getCanonicalName());

		boolean success = false;
		try {
			switch(whatToCheck) {
			case BASIC:
			default:
			{
				success = checkBasicFunctionality(solution);
			}
			break;
			case WEAK:
			{
				success = checkAccuracy(solution);
			}
			break;
			case STRONG:
			{
				success = checkControlStrong(solution);
			}
			break;
			case STRONGER:
			{
				success = checkControlStronger(solution);
			}
			break;
			case STRONGEST:
			{
				success = checkControlStrongest(solution);
			}
			break;
			}
		}
		catch(Exception e) {
			System.out.println("\tTest '" + whatToCheck.getName() + "' failed with exception: " + e.getMessage());
			System.out.println("\nHere is a stack trace:");
			e.printStackTrace(System.out);
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

	/**
	 * Check basic functionality
	 *
	 * @param solution The solution.
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkBasicFunctionality(AsianOptionWithBSControlVariateAssignment solution) {

		boolean success = true;

		double value1 = getValueForTestCase(solution, 0).get("value");
		success &= Math.abs(value1 - 0.3725) <= 0.02;

		double value2 = getValueForTestCase(solution, 1).get("value");
		success &= Math.abs(value2- 0.1244) <= 0.02;

		return success;
	}

	/**
	 * Check accuracy of the implementation.
	 *
	 * @param solution The solution.
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkAccuracy(AsianOptionWithBSControlVariateAssignment solution) {

		if(!checkBasicFunctionality(solution)) {
			System.out.println("  The product did not pass the basic functionality test.");
			return false;
		}

		boolean success = true;

		double error1 = getValueForTestCase(solution, 0).get("standardDeviation");
		success &= error1 <= 0.0009;

		double error2 = getValueForTestCase(solution, 1).get("standardDeviation");
		success &= error2 <= 0.0009;

		return success;
	}

	private static boolean checkControlStrong(AsianOptionWithBSControlVariateAssignment solution) {

		if(!checkBasicFunctionality(solution)) {
			System.out.println("  The product did not pass the basic functionality test.");
			return false;
		}

		boolean success = true;

		double error1 = getValueForTestCase(solution, 0).get("standardDeviation");
		success &= error1 <= 0.0004;

		double error2 = getValueForTestCase(solution, 1).get("standardDeviation");
		success &= error2 <= 0.0004;

		return success;
	}

	private static boolean checkControlStronger(AsianOptionWithBSControlVariateAssignment solution) {

		if(!checkBasicFunctionality(solution)) {
			System.out.println("  The product did not pass the basic functionality test.");
			return false;
		}

		boolean success = true;

		double error1 = getValueForTestCase(solution, 0).get("standardDeviation");
		success &= error1 <= 0.0001;

		double error2 = getValueForTestCase(solution, 1).get("standardDeviation");
		success &= error2 <= 0.0001;

		return success;
	}

	private static boolean checkControlStrongest(AsianOptionWithBSControlVariateAssignment solution) {

		if(!checkBasicFunctionality(solution)) {
			System.out.println("  The product did not pass the basic functionality test.");
			return false;
		}

		boolean success = true;

		double error1 = getValueForTestCase(solution, 0).get("standardDeviation");
		success &= error1 <= 0.00006;

		double error2 = getValueForTestCase(solution, 1).get("standardDeviation");
		success &= error2 <= 0.00006;

		return success;
	}

	private static Map<String, Double> getValueForTestCase(AsianOptionWithBSControlVariateAssignment solution, int testCase) {
		double	maturity = 10.0;
		double	strike = 1.05;
		TimeDiscretization timesForAveraging = new TimeDiscretizationFromArray(5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
		double callOrPutSign = testCase == 0 ? 1.0 : -1.0;

		/*
		 * Construct object
		 */
		AssetMonteCarloProduct product = createProduct(solution, maturity, strike, timesForAveraging, callOrPutSign);

		List<Double> valuesAsianAsList = new ArrayList<>();
		Random seeds = new Random(seed);
		for(int seed : seeds.ints().limit(20).toArray()) { 
			/*
			 * Create model
			 */
			final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = createModel(seed);

			/*
			 * Value AsianOptionWithBSControlVariate
			 */

			try {
				RandomVariable valueAsian = product.getValue(0.0, monteCarloBlackScholesModel);
				valuesAsianAsList.add(valueAsian.getAverage());
			} catch (CalculationException e) {
				throw new RuntimeException(e);
			}
		}

		RandomVariable valuesAsian = new RandomVariableFromDoubleArray(0.0, valuesAsianAsList.stream().mapToDouble(x -> x.doubleValue()).toArray());
		
		/*
		 * Print results
		 */
		Function<RandomVariable, String> printAvgAndErr = x ->
		String.format("%10.7f (\u03c3=%-9.7f)", x.getAverage(), x.getStandardDeviation());

		String callPut = callOrPutSign > 0 ? "call" : "put.";
		System.out.println("value Asian " + callPut + "............: " + printAvgAndErr.apply(valuesAsian));

		return Map.of("value", valuesAsian.getAverage(), "standardDeviation", valuesAsian.getStandardDeviation());
	}

	private static AssetModelMonteCarloSimulationModel createModel(int seed) {
		// Create a model
		final AbstractProcessModel model = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		// Create a time discretization
		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0 /* initial */, numberOfTimeSteps, deltaT);

		// Create a Brownian motion
		final IndependentIncrements brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 1 /* numberOfFactors */, numberOfPaths, seed);

		// Create a corresponding MC process
		final MonteCarloProcessFromProcessModel process = new EulerSchemeFromProcessModel(model, brownianMotion, Scheme.EULER);

		// Using the process (Euler scheme), create an MC simulation of a Black-Scholes model
		final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = new MonteCarloAssetModel(process);

		return monteCarloBlackScholesModel;
	}


	private static AssetMonteCarloProduct createProduct(AsianOptionWithBSControlVariateAssignment solution, double maturity, double strike, TimeDiscretization timesForAveraging, double callOrPutSign) {

		/*
		 * Try (double, double, TimeDiscretization)
		 */
		AssetMonteCarloProduct product = solution.getAsianOption(maturity, strike, timesForAveraging, callOrPutSign);

		return product;
	}
}
