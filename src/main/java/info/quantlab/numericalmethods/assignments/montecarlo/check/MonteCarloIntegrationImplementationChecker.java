/*
  * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.assignments.montecarlo.check;

import java.util.List;

import info.quantlab.numericalmethods.lecture.montecarlo.integration.Integrand;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.IntegrationDomain;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.IntegratorFactory;
import info.quantlab.numericalmethods.lecture.montecarlo.integration.MonteCarloIntegratorFactory;
import info.quantlab.numericalmethods.lecture.randomnumbers.MersenneTwister;
import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGeneratorFrom1D;
import net.finmath.functions.NormalDistribution;

public class MonteCarloIntegrationImplementationChecker {

	interface IntegratorTestCase {
		Integrand getIntegrand();
		double getToleranceScaling();
		IntegrationDomain getDomain();
		double getIntegralAnalytic();
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
	public static boolean check(MonteCarloIntegrationAssignment solution, String whatToCheck) {
		System.out.println("Running " + whatToCheck + " test on " + solution.getClass().getCanonicalName());

		boolean success = false;
		try {
			switch(whatToCheck) {
			case "monte carlo integrator":
			default:
			{
				success = checkMonteCarloIntegrator(solution);
			}
			break;
			case "monte carlo integrator 2D":
			{
				success = checkIntegralOfDoubleBinaryOperator(solution);
			}
			break;
			case "simpsons integrator":
			{
				success = checkSimpsonsIntegrator(solution);
			}
			break;
			}
		}
		catch(Exception e) {
			System.out.println("\tTest failed with exception: " + e.getMessage());
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
	 * Check the MonteCarloIntegratorFactory with different functions.
	 *
	 * @param solution Solution of MonteCarloIntegrationAssignment
	 */
	public static boolean checkMonteCarloIntegrator(MonteCarloIntegrationAssignment solution) {

		boolean success = true;

		MonteCarloIntegratorFactory integratorFactory = solution.getMonteCarloIntegratorFactory();

		for(IntegratorTestCase testCase : List.of(new IntegratorTestCaseUnitCircle(), new IntegratorTestCaseNormalCDF())) {
			System.out.println("Testing " + integratorFactory.getClass().getCanonicalName() + " with " + testCase);

			boolean successOfTestCase = checkMonteCarloIntegrator(integratorFactory, testCase);

			if(successOfTestCase) {
				System.out.println("\tTest of " + testCase + " passed.");
			}
			else {
				System.out.println("\tTest of " + testCase + " failed.");
			}

			success &= successOfTestCase;
		}

		return success;
	}

	/**
	 * Check the MonteCarloIntegratorFactory with different functions.
	 *
	 * @param solution Solution of MonteCarloIntegrationAssignment
	 */
	public static boolean checkSimpsonsIntegrator(MonteCarloIntegrationAssignment solution) {

		boolean success = true;

		IntegratorFactory integratorFactory = solution.getSimpsonsIntegratorFactory();

		for(IntegratorTestCase testCase : List.of(new IntegratorTestCaseUnitCircle(), new IntegratorTestCaseNormalCDF())) {
			System.out.println("Testing " + integratorFactory.getClass().getCanonicalName() + " with " + testCase);

			boolean successOfTestCase = checkSimpsonsIntegrator(integratorFactory, testCase);

			if(successOfTestCase) {
				System.out.println("\tTest of " + testCase + " passed.");
			}
			else {
				System.out.println("\tTest of " + testCase + " failed.");
			}

			success &= successOfTestCase;
		}

		return success;
	}

	/**
	 * Check the MonteCarloIntegratorFactory with different seeds.
	 *
	 * @param integratorFactory
	 * @param integratorTestCase
	 * @return
	 */
	public static boolean checkMonteCarloIntegrator(MonteCarloIntegratorFactory integratorFactory, IntegratorTestCase integratorTestCase) {
		/*
		 * Check three different seeds, at least one should pass
		 */
		boolean success = false;

		for(long seed : new long[] { 3141, 6563, 132 }) {
			long numberOfSamplePoints = 1000000;	// 1E-6

			double integral = integratorFactory.getIntegrator(
					new RandomNumberGeneratorFrom1D(new MersenneTwister(seed), integratorTestCase.getDomain().getDimension()),
					numberOfSamplePoints).integrate(integratorTestCase.getIntegrand(), integratorTestCase.getDomain());

			System.out.println(String.format("\tgot: %8.5g, expected: %8.5g, error: %8.5g", integral, integratorTestCase.getIntegralAnalytic(), (integral-integratorTestCase.getIntegralAnalytic())));

			success |= Math.abs(integral-integratorTestCase.getIntegralAnalytic()) < 1E-2 * integratorTestCase.getToleranceScaling();
		}

		return success;
	}

	/**
	 * Check the
	 */
	public static boolean checkIntegralOfDoubleBinaryOperator(MonteCarloIntegrationAssignment solution) {
		System.out.println("Testing getIntegral(...)");

		double integral = solution.getIntegral((x,y) -> Math.sin(x)*Math.sin(y), 0, Math.PI, 0, Math.PI);
		double integralAnalytic = 4.0;

		boolean success = Math.abs(integral-integralAnalytic) < 5E-2;

		return success;
	}

	/**
	 * Check the Simpsons IntegratorFactory with different seeds.
	 *
	 * @param integratorFactory
	 * @param integratorTestCase
	 * @return
	 */
	public static boolean checkSimpsonsIntegrator(IntegratorFactory integratorFactory, IntegratorTestCase integratorTestCase) {

		long numberOfSamplePoints = (long) Math.pow(101.0, integratorTestCase.getDomain().getDimension());	// 101^d

		double integral = integratorFactory.getIntegrator(
				numberOfSamplePoints).integrate(integratorTestCase.getIntegrand(), integratorTestCase.getDomain());

		System.out.println(String.format("\tgot: %8.5g, expected: %8.5g, error: %8.5g", integral, integratorTestCase.getIntegralAnalytic(), (integral-integratorTestCase.getIntegralAnalytic())));

		boolean success = Math.abs(integral-integratorTestCase.getIntegralAnalytic()) < 5E-3 * integratorTestCase.getToleranceScaling();

		return success;
	}

	public static class IntegratorTestCaseUnitCircle implements IntegratorTestCase {
		@Override
		public Integrand getIntegrand() {

			return new Integrand() {
				@Override
				public double value(double[] arguments) {
					double x = arguments[0];
					double y = arguments[1];
					return x*x + y*y < 1.0 ? 1.0 : 0.0;
				}
			};
		}

		@Override
		public IntegrationDomain getDomain() {
			return new IntegrationDomain() {

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
		}

		@Override
		public double getIntegralAnalytic() {
			return Math.PI;
		}

		@Override
		public double getToleranceScaling() {
			return 1.0;
		}

		@Override
		public String toString() {
			return "integrator test case unit circle";
		}
	}

	public static class IntegratorTestCaseNormalCDF implements IntegratorTestCase {
		@Override
		public Integrand getIntegrand() {
			return new Integrand() {
				@Override
				public double value(double[] arguments) {
					double x = arguments[0];
					double y = arguments[1];
					double z = arguments[2];
					return Math.exp(-0.5 * (x*x + y*y + z*z)) / Math.pow(2*Math.PI, 3.0/2.0);
				}
			};
		}

		@Override
		public IntegrationDomain getDomain() {
			return new IntegrationDomain() {

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
		}

		@Override
		public double getIntegralAnalytic() {
			return Math.pow(NormalDistribution.cumulativeDistribution(1)-NormalDistribution.cumulativeDistribution(-1), 3);
		}

		@Override
		public double getToleranceScaling() {
			return 1.0;
		}

		@Override
		public String toString() {
			return "integrator test case normal ICDF";
		}
	}
}


