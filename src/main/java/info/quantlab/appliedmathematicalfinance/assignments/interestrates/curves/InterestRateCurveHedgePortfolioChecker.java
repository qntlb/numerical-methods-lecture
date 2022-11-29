/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.appliedmathematicalfinance.assignments.interestrates.curves;

import java.util.Random;

public class InterestRateCurveHedgePortfolioChecker {

	public enum Check {
		BASIC("basic functionality"),
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
				success = false;
			}
			break;
			case PAR_RATE:
			{
				success = false;
			}
			break;
			case SENSITIVITIES:
			{
				success = false;
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
}
