/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.appliedmathematicalfinance.assignments.interestrates.discountcurve.check;

import info.quantlab.appliedmathematicalfinance.assignments.interestrates.discountcurve.DiscountCurve;
import info.quantlab.appliedmathematicalfinance.assignments.interestrates.discountcurve.DiscountCurveFactory;

public class DiscountCurveChecker {

	public enum Check {
		
		BASIC("basic functionality"),
		LINEAR_INTERPOLATION("linear interpolation of discount factors"),
		LOG_LINEAR_INTERPOLATION("log-linear interpolation of discount factors");

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
	public static boolean check(DiscountCurveAssignment solution, Check whatToCheck) {
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
			case LINEAR_INTERPOLATION:
			{
				success = checkLinear(solution);
			}
			break;
			case LOG_LINEAR_INTERPOLATION:
			{
				success = checkLoglinear(solution);
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

	private static boolean checkBasics(DiscountCurveAssignment solution) {
		
		// Get the discount curve factory
		DiscountCurveFactory discountCurveFactory = solution.getDiscountCurveFactory();

		// Create a simple discount curve
		DiscountCurve discountCurve = discountCurveFactory.createDiscountCurve(new double[] { 0.0, 1.0, 2.0, 3.0, 5.0 }, new double[] { 1.0, 0.9, 0.8, 0.7, 0.5 }, "linear");
		
		double df = discountCurve.getDiscountFactor(1.0);
		
		double dfTarget = 0.9;

		boolean success = Math.abs(df-dfTarget) < 1E-12;
		
		if(!success) System.out.println("\tExpected discount factor at 1.0 to be " + dfTarget + " but got " + df);

		return success;
	}

	private static boolean checkLinear(DiscountCurveAssignment solution) {
		
		// Get the discount curve factory
		DiscountCurveFactory discountCurveFactory = solution.getDiscountCurveFactory();

		// Create a simple discount curve
		DiscountCurve discountCurve = discountCurveFactory.createDiscountCurve(new double[] { 0.0, 1.0, 2.0, 3.0, 5.0 }, new double[] { 1.0, 0.9, 0.8, 0.7, 0.5 }, "linear");
		
		double df = discountCurve.getDiscountFactor(1.5);

		double dfTarget = 0.85;

		boolean success = Math.abs(df-dfTarget) < 1E-12;

		if(!success) System.out.println("\tExpected discount factor at 1.5 to be " + dfTarget + " but got " + df);

		return success;
	}

	private static boolean checkLoglinear(DiscountCurveAssignment solution) {
		
		// Get the discount curve factory
		DiscountCurveFactory discountCurveFactory = solution.getDiscountCurveFactory();

		// Create a simple discount curve
		DiscountCurve discountCurve = discountCurveFactory.createDiscountCurve(new double[] { 0.0, 1.0, 2.0, 3.0, 5.0 }, new double[] { 1.0, 0.9, 0.8, 0.7, 0.5 }, "log_linear");
		
		double df = discountCurve.getDiscountFactor(1.5);

		double dfTarget = Math.exp((Math.log(0.9)+Math.log(0.8))/2.0);

		boolean success = Math.abs(df-dfTarget) < 1E-12;

		if(!success) System.out.println("\tExpected discount factor at 1.5 to be " + dfTarget + " but got " + df);

		return success;
	}
}
