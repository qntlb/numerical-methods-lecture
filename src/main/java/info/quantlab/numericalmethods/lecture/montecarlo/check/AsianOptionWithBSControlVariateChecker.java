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
		case "basic":
			return checkBasicFunctionality(theClass);
		case "accuracy":
		default:
			return checkAccuracy(theClass);
		}
	}

	/**
	 * Check basic functionality
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkBasicFunctionality(Class<?> theClass) {
		double	maturity = 2.0;
		double	strike = 1.05;
		TimeDiscretization timesForAveraging = new TimeDiscretizationFromArray(5.0, 6.0, 7.0, 8.0, 9.0, 10.0);


		/*
		 * Construct object
		 */
		AssetMonteCarloProduct product = createProduct(theClass, maturity, strike, timesForAveraging);

		/*
		 * Create model
		 */
		// Model properties
		final double	initialValue   = 1.0;
		final double	riskFreeRate   = 0.05;
		final double	volatility     = 0.30;

		// Process discretization properties
		final int		numberOfPaths		= 200000;
		final int		numberOfTimeSteps	= 20;
		final double	deltaT				= 0.5;

		final int		seed				= 31415;


		// Create a model
		final AbstractProcessModel model = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		// Create a time discretizeion
		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(0.0 /* initial */, numberOfTimeSteps, deltaT);

		// Create a Brownian motion
		final IndependentIncrements brownianMotion = new BrownianMotionFromMersenneRandomNumbers(timeDiscretization, 1 /* numberOfFactors */, numberOfPaths, seed);

		// Create a corresponding MC process
		final MonteCarloProcessFromProcessModel process = new EulerSchemeFromProcessModel(model, brownianMotion, Scheme.EULER);

		// Using the process (Euler scheme), create an MC simulation of a Black-Scholes model
		final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = new MonteCarloAssetModel(model, process);

		/*
		 * Value AsianOptionWithBSControlVariate
		 */

		AssetMonteCarloProduct asian = new AsianOption(maturity, strike, timesForAveraging);

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

		if(Math.abs(valueAsian.getAverage()- 0.556) > 0.02) {
			System.out.println("\tThe value of the asian option appears to be wrong.");
			return false;
		}
		else {
			System.out.println("\tSimple test passed.");
		}

		System.out.println("You implementation appears to work, however, the final grading will require an update of this test.");
		System.out.println("For that reason we consider this test currently as failed.");
		System.out.println("Just wait for the update of this test.");

		return false;
	}

	/**
	 * Check accuracy of the implementation.
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkAccuracy(Class<?> theClass) {

		System.out.println("The final grading will require an update of this test.");
		System.out.println("Just wait for the update of this test.");
		return false;
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
