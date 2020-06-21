/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.montecarlo.check;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import info.quantlab.reflection.ObjectConstructor;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.IndependentIncrements;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AsianOption;
import net.finmath.montecarlo.assetderivativevaluation.products.AssetMonteCarloProduct;
import net.finmath.montecarlo.model.AbstractProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel.Scheme;
import net.finmath.montecarlo.process.MonteCarloProcessFromProcessModel;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class AsianOptionWithBSControlVariateChecker {

	// Model properties
	private static final double	initialValue   = 1.0;
	private static final double	riskFreeRate   = 0.05;
	private static final double	volatility     = 0.30;

	// Process discretization properties
	private static final int		numberOfPaths		= 200000;
	private static final int		numberOfTimeSteps	= 20;
	private static final double		deltaT				= 0.5;

	private static final int		seed				= 31415;

	private static double accuracy = 1E-11;
	private static Random random = new Random(3141);

	/**
	 * Check if the class solves the exercise.
	 * 
	 * @param theClass The class to test;
	 * @param whatToCheck A string, currently "basic" or "accuracy".
	 * @return Boolean if the test is passed.
	 */
	public static boolean check(Class<?> theClass, String whatToCheck) {

		switch(whatToCheck) {
		case "control":
		default:
		{
			if(!checkBasicFunctionality(theClass)) {
				System.out.println("\t Before we test the variance reduction, the valuation has to be correct.");
				return false;
			}
			else {
				boolean success = checkControl(theClass);
				if(success) {
					System.out.println("\t Strong variance reduction test passed.");
				}
				else {
					System.out.println("\t The variance reduction is not good enough for the strong variance reduction test.");
				}
				return success;
			}
		}
		case "accuracy":
			if(!checkBasicFunctionality(theClass)) {
				System.out.println("\t Before we test the variance reduction, the valuation has to be correct.");
				return false;
			}
			else {
				boolean success = checkAccuracy(theClass);
				if(success) {
					System.out.println("\t Weak variance reduction test passed.");
				}
				else {
					System.out.println("\t The variance reduction is not good enough.");
				}
				return success;
			}
		case "basic":
			boolean success = checkBasicFunctionality(theClass);
			if(success) {
				System.out.println("\t Valuation test passed.");
			}
			else {
				System.out.println("\t Your class does not work or the value of the asian option appears to be wrong.");
			}
			return success;
		}
	}

	/**
	 * Check basic functionality
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkBasicFunctionality(Class<?> theClass) {

		RandomVariable value = getValueForTestCase(theClass, 0);

		return Math.abs(value.getAverage()- 0.3725) <= 0.02;
	}

	/**
	 * Check accuracy of the implementation.
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkAccuracy(Class<?> theClass) {

		RandomVariable value = getValueForTestCase(theClass, 0);

		return Math.abs(value.getStandardError()) <= 0.0009;
	}	

	private static boolean checkControl(Class<?> theClass) {

		RandomVariable value = getValueForTestCase(theClass, 0);

		return Math.abs(value.getStandardError()) <= 0.0004;
	}	

	private static RandomVariable getValueForTestCase(Class<?> theClass, int testCase) {
		double	maturity = 10.0;
		double	strike = 1.05;
		TimeDiscretization timesForAveraging = new TimeDiscretizationFromArray(5.0, 6.0, 7.0, 8.0, 9.0, 10.0);


		/*
		 * Construct object
		 */
		AssetMonteCarloProduct product = createProduct(theClass, maturity, strike, timesForAveraging);

		/*
		 * Create model
		 */
		final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = createModel();

		/*
		 * Value AsianOptionWithBSControlVariate
		 */

		RandomVariable valueAsian = null;
		try {
			valueAsian = product.getValue(0.0, monteCarloBlackScholesModel);
		} catch (CalculationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Print results
		 */

		Function<RandomVariable, String> printAvgErr = x -> 
		String.format("%10.7f \u00B1 %10.7f", x.getAverage(), x.getStandardError());

		System.out.println("value Asian.................: " + printAvgErr.apply(valueAsian));

		return valueAsian;
	}

	private static AssetModelMonteCarloSimulationModel createModel() {
		// Create a model
		final AbstractProcessModel model = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		// Create a time discretization
		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0 /* initial */, numberOfTimeSteps, deltaT);

		// Create a Brownian motion
		final IndependentIncrements brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 1 /* numberOfFactors */, numberOfPaths, seed);

		// Create a corresponding MC process
		final MonteCarloProcessFromProcessModel process = new EulerSchemeFromProcessModel(model, brownianMotion, Scheme.EULER);

		// Using the process (Euler scheme), create an MC simulation of a Black-Scholes model
		final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = new MonteCarloAssetModel(model, process);

		return monteCarloBlackScholesModel;
	}


	private static AssetMonteCarloProduct createProduct(Class<?> theClass, double maturity, double strike, TimeDiscretization timesForAveraging) {

		/*
		 * Try (double, double, TimeDiscretization)
		 */
		try {
			List<Class<?>> argumentTypes = List.of(double.class, double.class, TimeDiscretization.class);
			List<Object> arguments = List.of(maturity, strike, timesForAveraging);

			AssetMonteCarloProduct product = ObjectConstructor.<AssetMonteCarloProduct>create(theClass, AssetMonteCarloProduct.class, argumentTypes, arguments);
			return product;
		}
		catch(IllegalArgumentException e) {
		}

		/*
		 * Try (Double, Double, TimeDiscretization)
		 */
		try {
			List<Class<?>> argumentTypes = List.of(Double.class, Double.class, TimeDiscretization.class);
			List<Object> arguments = List.of(maturity, strike, timesForAveraging);

			AssetMonteCarloProduct product = ObjectConstructor.<AssetMonteCarloProduct>create(theClass, AssetMonteCarloProduct.class, argumentTypes, arguments);
			return product;
		}
		catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

}
