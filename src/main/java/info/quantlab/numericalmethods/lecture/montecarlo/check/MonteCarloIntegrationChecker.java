/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.montecarlo.check;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import info.quantlab.numericalmethods.lecture.computerarithmetics.QuadraticEquation;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.Integrand;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.IntegrationDomain;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.MonteCarloIntegratorFactory;
import info.quantlab.reflection.ObjectConstructor;
import net.finmath.exception.CalculationException;
import net.finmath.integration.MonteCarloIntegrator;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.IndependentIncrements;
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

public class MonteCarloIntegrationChecker {

	/**
	 * Check if the class solves the exercise.
	 *
	 * @param theClass The class to test;
	 * @param whatToCheck A string, currently "basic" or "accuracy".
	 * @return Boolean if the test is passed.
	 */
	public static boolean check(MonteCarloIntegratorFactory integratorFactory, String whatToCheck) {

		boolean success;

		switch(whatToCheck) {
		case "unit circle":
		default:
			success = testUnitCircle(integratorFactory);
			break;
		case "normal cdf":
			success = testUnitCircle(integratorFactory);
			break;
			
		}

		if(success) {
			System.out.println("\t Test of " + whatToCheck + " passed.");
		}
		else {
			System.out.println("\t Test of " + whatToCheck + " failed.");
		}
		return success;
	}

	private static boolean testUnitCircle(MonteCarloIntegratorFactory integratorFactory) {
		Integrand integrand = new Integrand() {
			@Override
			public double value(double[] arguments) {
				double x = arguments[0];
				double y = arguments[1];
				return x*x + y*y < 1.0 ? 1.0 : 0.0;
			}
		};
			
		IntegrationDomain domain = new IntegrationDomain() {

			@Override
			public double[] fromUnitCube(double[] parametersOnUnitCube) {
				return new double[] { 2.0 * parametersOnUnitCube[0] - 1.0,  2.0 * parametersOnUnitCube[1] - 1.0 };
			}

			@Override
			public int getDimention() {
				return 2;
			}

			@Override
			public double getDeterminantOfDifferential(double[] parametersOnUnitCurve) {
				return 4.0;
			}			
		};
		
		long seed = 3141;
		long numberOfSamplePoints = 1000000;

		double integral = integratorFactory.getIntegrator(seed, numberOfSamplePoints).integrate(integrand, domain);
		
		System.out.println(integral);

		return Math.abs(integral-Math.PI) < 1E-2;
	}
	
	private static boolean testNormalCDF(MonteCarloIntegratorFactory integratorFactory) {
		Integrand integrand = new Integrand() {
			@Override
			public double value(double[] arguments) {
				double x = arguments[0];
				double y = arguments[1];
				double z = arguments[2];
				return Math.exp(-0.5 * (x*x + y*y + z*z)) / Math.pow(2*Math.PI, 3.0/2.0);
			}
		};
			
		IntegrationDomain domain = new IntegrationDomain() {

			@Override
			public double[] fromUnitCube(double[] parametersOnUnitCube) {
				return new double[] {
						2.0 * parametersOnUnitCube[0] - 1.0,
						2.0 * parametersOnUnitCube[1] - 1.0,
						2.0 * parametersOnUnitCube[2] - 1.0
						};
			}

			@Override
			public int getDimention() {
				return 3;
			}

			@Override
			public double getDeterminantOfDifferential(double[] parametersOnUnitCurve) {
				return 8.0;
			}			
		};
		
		long seed = 3141;
		long numberOfSamplePoints = 1000000;
		double integral = integratorFactory.getIntegrator(seed, numberOfSamplePoints).integrate(integrand, domain);
		
		System.out.println(integral);
		System.out.println(integral);

		return Math.abs(integral-Math.PI) < 1E-2;
	}
}
