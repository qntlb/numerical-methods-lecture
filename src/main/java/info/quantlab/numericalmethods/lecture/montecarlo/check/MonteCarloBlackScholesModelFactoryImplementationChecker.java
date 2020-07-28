/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.montecarlo.check;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import info.quantlab.numericalmethods.lecture.montecarlo.MonteCarloBlackScholesModelFactory;
import info.quantlab.reflection.ObjectConstructor;
import net.finmath.exception.CalculationException;
import net.finmath.functions.AnalyticFormulas;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.products.DigitalOption;
import net.finmath.montecarlo.assetderivativevaluation.products.DigitalOptionDeltaLikelihood;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;
import net.finmath.time.TimeDiscretizationFromArray;

public class MonteCarloBlackScholesModelFactoryImplementationChecker {

	private static Function<RandomVariable, String> printAvgErr = x -> String.format("%10.7f \u00B1 %10.7f", x.getAverage(), x.getStandardError());

	// Model properties
	private static final double	initialValue   = 1.0;
	private static final double	riskFreeRate   = 0.05;
	private static final double	volatility     = 0.30;

	// Process discretization properties
	private static final int		numberOfPaths		= 200000;

	private static final double initalTime = 0;
	private static final int		numberOfTimeSteps	= 20;
	private static final double		deltaT				= 0.5;

	private static final int		seed				= 31415;

	// Product properties
	private static final double	maturity = 2.0;
	private static final double	strike = 1.05;

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
		case "strong":
		default:
		{
			if(!checkBasicFunctionality(theClass)) {
				System.out.println("\t Before we test the variance reduction, the valuation has to be correct.");
				return false;
			}
			else {
				boolean success = checkStrong(theClass);
				if(success) {
					System.out.println("\t Strong variance reduction test passed.");
				}
				else {
					System.out.println("\t The variance reduction is not good enough for the strong variance reduction test.");
				}
				return success;
			}
		}
		case "weak":
			if(!checkBasicFunctionality(theClass)) {
				System.out.println("\t Before we test the variance reduction, the valuation has to be correct.");
				return false;
			}
			else {
				boolean success = checkWeak(theClass);
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
		try {
			/*
			 * Create model using the factory "theClass"
			 */
			final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = createModel(theClass);

			/*
			 * Create products
			 */
			AbstractAssetMonteCarloProduct europeanOption = new EuropeanOption(maturity, strike);
			AbstractAssetMonteCarloProduct digitalOption = new DigitalOption(maturity, strike);

			/*
			 * Value a European call option
			 */
			double valueAnalytic = AnalyticFormulas.blackScholesOptionValue(initialValue, riskFreeRate, volatility, maturity, strike);
			RandomVariable valueMonteCarlo = europeanOption.getValue(initalTime, monteCarloBlackScholesModel);

			System.out.println("\nValuation (European Call Option):\n");
			System.out.format("\t%20s: %10.3g\n", "analytic", valueAnalytic);
			System.out.format("\t%20s: %s\n", "monte carlo", printAvgErr.apply(valueMonteCarlo));

			/*
			 * Value a digital option
			 */
			double valueDigitalAnalytic = AnalyticFormulas.blackScholesDigitalOptionValue(initialValue, riskFreeRate, volatility, maturity, strike);
			RandomVariable valueDigitalMonteCarlo = digitalOption.getValue(initalTime, monteCarloBlackScholesModel);

			System.out.println("\nValuation (European Digital Option):\n");
			System.out.format("\t%20s: %10.3g\n", "analytic", valueDigitalAnalytic);
			System.out.format("\t%20s: %s\n", "monte carlo", printAvgErr.apply(valueDigitalMonteCarlo));

			return	(Math.abs(valueMonteCarlo.getAverage()- valueAnalytic) <= 0.002)
					&&
					(Math.abs(valueDigitalMonteCarlo.getAverage()- valueDigitalAnalytic) <= 0.002);
		}
		catch(Exception e) {
			System.out.println("\nTest failed with exception:");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Check accuracy of the implementation.
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkWeak(Class<?> theClass) {
		try {
			/*
			 * Create model using the factory "theClass"
			 */
			final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = createModel(theClass);

			/*
			 * Create products
			 */
			AbstractAssetMonteCarloProduct europeanOption = new EuropeanOption(maturity, strike);
			AbstractAssetMonteCarloProduct digitalOption = new DigitalOption(maturity, strike);

			/*
			 * Calculate delta of a European call option
			 */
			double delteEuropeanAnalytic = AnalyticFormulas.blackScholesOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
			RandomVariable deltaEuropeanMonteCarlo = getDeltaByFiniteDifference(monteCarloBlackScholesModel, europeanOption, 1E-5);

			System.out.println("\nDelta (European Call Option):\n");
			System.out.format("\t%20s: %10.3g\n", "analytic", delteEuropeanAnalytic);
			System.out.format("\t%20s: %s\n", "monte carlo", printAvgErr.apply(deltaEuropeanMonteCarlo));

			/*
			 * Calculate delta of a digital option
			 */
			double delteDigitalAnalytic = AnalyticFormulas.blackScholesDigitalOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
			RandomVariable deltaDigitalMonteCarlo = getDeltaByFiniteDifference(monteCarloBlackScholesModel, digitalOption, 1E-5);

			System.out.println("\nDelta (European Digital Option):\n");
			System.out.format("\t%20s: %10.3g\n", "analytic", delteDigitalAnalytic);
			System.out.format("\t%20s: %s\n", "monte carlo", printAvgErr.apply(deltaDigitalMonteCarlo));

			return	(Math.abs(deltaEuropeanMonteCarlo.getAverage()- delteEuropeanAnalytic) <= 0.002)
					&&
					(Math.abs(deltaDigitalMonteCarlo.getAverage()- delteDigitalAnalytic) <= 0.06);
		}
		catch(Exception e) {
			System.out.println("\nTest failed with exception:");
			e.printStackTrace();
			return false;
		}
	}

	private static boolean checkStrong(Class<?> theClass) {
		try {
			/*
			 * Create model using the factory "theClass"
			 */
			final AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = createModel(theClass);

			/*
			 * Create products
			 */
			AbstractAssetMonteCarloProduct europeanOption = new EuropeanOption(maturity, strike);
			AbstractAssetMonteCarloProduct digitalOption = new DigitalOption(maturity, strike);

			/*
			 * Calculate delta of a European call option
			 */
			double delteEuropeanAnalytic = AnalyticFormulas.blackScholesOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
			RandomVariable deltaEuropeanMonteCarlo = getDeltaByFiniteDifference(monteCarloBlackScholesModel, europeanOption, 1E-5);

			System.out.println("\nDelta (European Call Option):\n");
			System.out.format("\t%20s: %10.3g\n", "analytic", delteEuropeanAnalytic);
			System.out.format("\t%20s: %s\n", "monte carlo", printAvgErr.apply(deltaEuropeanMonteCarlo));

			/*
			 * Calculate delta of a digital option
			 */
			double delteDigitalAnalytic = AnalyticFormulas.blackScholesOptionDelta(initialValue, riskFreeRate, volatility, maturity, strike);
			RandomVariable deltaDigitalMonteCarlo = getDeltaByFiniteDifference(monteCarloBlackScholesModel, digitalOption, 1E-5);
			RandomVariable deltaDigitalLikelihood = new DigitalOptionDeltaLikelihood(maturity, strike).getValue(initalTime, monteCarloBlackScholesModel);

			System.out.println("\nDelta (European Digital Option):\n");
			System.out.format("\t%20s: %10.3g\n", "analytic", delteDigitalAnalytic);
			System.out.format("\t%20s: %s\n", "monte carlo", printAvgErr.apply(deltaDigitalMonteCarlo));
			System.out.format("\t%20s: %s\n", "likelihood ratio", printAvgErr.apply(deltaDigitalLikelihood));

			return	(Math.abs(deltaDigitalMonteCarlo.getAverage()- delteDigitalAnalytic) <= 0.002)
					&&
					(Math.abs(deltaDigitalMonteCarlo.getStandardError()- deltaDigitalLikelihood.getAverage()) <= 0.01);
		}
		catch(Exception e) {
			System.out.println("\nTest failed with exception:");
			e.printStackTrace();
			return false;
		}

	}

	private static RandomVariable getDeltaByFiniteDifference(AssetModelMonteCarloSimulationModel model, AbstractAssetMonteCarloProduct product, double shiftRelative) {
		try {
			double shift = initialValue*shiftRelative;

			AssetModelMonteCarloSimulationModel modelUpShift = model.getCloneWithModifiedData(Map.of("initialValue", initialValue+shift));
			RandomVariable valueUpShift = product.getValue(initalTime, modelUpShift);

			AssetModelMonteCarloSimulationModel modelDnShift = model.getCloneWithModifiedData(Map.of("initialValue", initialValue-shift));
			RandomVariable valueDnShift = product.getValue(initalTime, modelDnShift);

			return valueUpShift.sub(valueDnShift).div(2*shiftRelative);
		} catch (CalculationException e) {
			return new Scalar(Double.NaN);
		}
	}

	private static AssetModelMonteCarloSimulationModel createModel(Class<?> theClass) {

		MonteCarloBlackScholesModelFactory modelFactory = ObjectConstructor.<MonteCarloBlackScholesModelFactory>create(theClass, MonteCarloBlackScholesModelFactory.class);

		AssetModelMonteCarloSimulationModel monteCarloBlackScholesModel = modelFactory.getModel(initialValue, riskFreeRate, volatility, new TimeDiscretizationFromArray(initalTime, numberOfTimeSteps, deltaT), numberOfPaths);
		return monteCarloBlackScholesModel;
	}

}
