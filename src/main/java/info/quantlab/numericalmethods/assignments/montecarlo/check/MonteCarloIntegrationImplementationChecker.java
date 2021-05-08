/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.assignments.montecarlo.check;

import info.quantlab.numericalmethods.lecture.montecarlo.integration.Integrand;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.IntegrationDomain;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.MonteCarloIntegratorFactory;
import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGeneratorFrom1D;
import net.finmath.functions.NormalDistribution;

public class MonteCarloIntegrationImplementationChecker {

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
			success = testNormalCDF(integratorFactory);
			break;
		}

		if(success) {
			System.out.println("\tTest of " + whatToCheck + " passed.");
		}
		else {
			System.out.println("\tTest of " + whatToCheck + " failed.");
		}
		return success;
	}

	public static boolean check(MonteCarloIntegrationAssignment solution, String whatToCheck) {
		System.out.println("Testing getIntegral(...)");

		boolean success;
		try {
			double integral = solution.getIntegral((x,y) -> Math.sin(x)*Math.sin(y), 0, Math.PI, 0, Math.PI);
			double integralAnalytic = 4.0;

			success = Math.abs(integral-integralAnalytic) < 5E-2;
		}
		catch(Exception e) {
			System.out.println(" Failed with exception " + e.getMessage());
			success = false;
		}

		if(success) {
			System.out.println("\tTest of " + whatToCheck + " passed.");
		}
		else {
			System.out.println("\tTest of " + whatToCheck + " failed.");
			System.out.println("\tIn case it is a numerical error: we expect an accuracy of 0.05.");
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
			public int getDimension() {
				return 2;
			}

			@Override
			public double getDeterminantOfDifferential(double[] parametersOnUnitCurve) {
				return 4.0;
			}			
		};

		double integralAnalytic = Math.PI;

		/*
		 * Check three different seeds, at least one should pass
		 */
		boolean success = false;

		for(long seed : new long[] { 3141, 6563, 132 }) {
			long numberOfSamplePoints = 1000000;

			double integral = integratorFactory.getIntegrator(
					new RandomNumberGeneratorFrom1D(new MersenneTwister(seed), domain.getDimension()),
					numberOfSamplePoints).integrate(integrand, domain);

			System.out.println("\tgot: " + integral + ", expected: " + integralAnalytic);

			success |= Math.abs(integral-integralAnalytic) < 1E-2;
		}

		return success;
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
			public int getDimension() {
				return 3;
			}

			@Override
			public double getDeterminantOfDifferential(double[] parametersOnUnitCurve) {
				return 8.0;
			}			
		};

		double integralAnalytic = Math.pow(NormalDistribution.cumulativeDistribution(1)-NormalDistribution.cumulativeDistribution(-1), 3);

		/*
		 * Check three different seeds, at least one should pass
		 */
		boolean success = false;

		for(long seed : new long[] { 3141, 6563, 132 }) {
			long numberOfSamplePoints = 1000000;

			double integral = integratorFactory.getIntegrator(
					new RandomNumberGeneratorFrom1D(new MersenneTwister(seed), domain.getDimension()),
					numberOfSamplePoints)
					.integrate(integrand, domain);

			System.out.println("\tgot: " + integral + ", expected: " + integralAnalytic);

			success |= Math.abs(integral-integralAnalytic) < 1E-2;
		}

		return success;
	}
}
