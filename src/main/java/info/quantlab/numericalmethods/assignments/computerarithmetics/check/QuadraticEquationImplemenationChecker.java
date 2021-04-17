/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.assignments.computerarithmetics.check;

import java.util.Arrays;
import info.quantlab.numericalmethods.lecture.computerarithmetics.quadraticequation.QuadraticEquation;
import info.quantlab.numericalmethods.lecture.computerarithmetics.quadraticequation.QuadraticEquationFactory;

public class QuadraticEquationImplemenationChecker {

	private static double accuracy = 3E-16;		// A bit larger than 2 times machine precision. You may achive this.

	/**
	 * Check if the class solves the exercise.
	 * @param whatToTest 
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean check(QuadraticEquationFactory quadraticEquationFactory, String whatToTest) {
		boolean succes = true;

		try {
			switch(whatToTest) {
			case "accuracy":
			default:
				/*
				 * Check some extreme cases
				 */
				double[][] testCases = {
						{1.0,  100000.0},
						{1.0, -100000.0},
						{1.0,  10000000.0},
						{1.0, -10000000.0},
						{100, 20.01  },
						{100, -20.01 },
						{0, 10.0  },
						{0, -10.0 },
				};
				for(double[] testCase : testCases) {
					succes &= checkWithCoefficients(quadraticEquationFactory, testCase[0] /* q */,  testCase[1] /* p */);
				}

				/*
				 * Check for smallest root
				 */
				succes &= checkForSmallestRoot(quadraticEquationFactory);
			case "basic":
				/*
				 * Check coefficents returned correctly
				 */
				succes &= checkgetCoefficients(quadraticEquationFactory, 2.0, -1.0);

				/*
				 * Check hasRealRoot
				 */
				succes &= checkHasRealRoot(quadraticEquationFactory);


				/*
				 * Check behaviour for non real roots.
				 */
				boolean isNonRealRootNaN;
				try {
					QuadraticEquation equationWithoutRoot = quadraticEquationFactory.createQuadraticEquation(2.0, -1.0);
					double solution = equationWithoutRoot.getSmallestRoot();

					isNonRealRootNaN = Double.isNaN(solution);
				}
				catch(Exception e) {
					isNonRealRootNaN = false;
				}
				if(!isNonRealRootNaN) {
					System.out.println("\tWe would expect getSmallestRoot() of equation without root to be NaN.");
				}
			}

		}
		catch(UnsupportedOperationException e) {
			System.out.println("You assigment does not implement the factory method that should create"
					+ " an object of type QuadraticEquation.");
			System.out.println(e.getMessage());

			succes = false;
		}
		catch(Exception e) {
			System.out.println("Failed to test your implementation.");
			System.out.println(e.getMessage());

			succes = false;
		}

		return succes;
	}

	private static boolean checkForSmallestRoot(QuadraticEquationFactory quadraticEquationFactory) {
		boolean success = true;
		
		// (x-1) * (x-2) = x^2 - 2 x + 2 => p = -2, q= 2
		System.out.println("\tTesting q = 2, p = -2.");
		double x1 = quadraticEquationFactory.createQuadraticEquation(2, -2).getSmallestRoot();
		success &= x1 < 1.5;

		// x * (x+2) = x^2 + 2 x => p = 2, q= 0
		System.out.println("\tTesting q = 0, p = 2.");
		double x2 = quadraticEquationFactory.createQuadraticEquation(0, -2).getSmallestRoot();
		success &= x2 < -0.5;

		if(!success) {
			System.out.println("\t\u274cThe method getSmallestRoot does not report the smallest (!) root.");
		}
		else {
			System.out.println("\t\u2705Test passed.");
		}
		return success;
	}

	private static boolean  checkgetCoefficients(QuadraticEquationFactory quadraticEquationFactory, double q, double p) {
		QuadraticEquation equation = quadraticEquationFactory.createQuadraticEquation(q, p);

		boolean success = false;
		try {
			double[] parameter = equation.getCoefficients();

			success = parameter[0] == 2.0 && parameter[1] == -1.0;
		}
		catch(Exception e) {
			System.out.println("\tTest of method getCoefficients reported an exception, did not expect that: " + e.getMessage());
		}

		if(!success) {
			System.out.println("\t\u274cTest of method getCoefficients failed. The method did not report the coefficients in the right way.");
		}
		else {
			System.out.println("\t\u2705Test passed.");
		}
		return success;
	}

	/**
	 * Check if the class solves the exercise with respect to the method <code>hasRealRoot</code>.
	 * Test x^2 + p x + q = 0.
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	private static boolean checkHasRealRoot(QuadraticEquationFactory quadraticEquationFactory) {
		QuadraticEquation equation1 = quadraticEquationFactory.createQuadraticEquation(2.0, -1.0);
		if(equation1.hasRealRoot()) {
			System.out.println("\tTest failed with coefficients " + Arrays.toString(equation1.getCoefficients()) + " failed. hasRealRoot reported true, should be false.");
			return false;
		}

		QuadraticEquation equation2 = quadraticEquationFactory.createQuadraticEquation(2.0, -1.0);
		if(equation2.hasRealRoot()) {
			System.out.println("\tTest failed with coefficients " + Arrays.toString(equation2.getCoefficients()) + " failed. hasRealRoot reported true, should be false.");
			return false;
		}

		QuadraticEquation equation3 = quadraticEquationFactory.createQuadraticEquation(1.0, 2.0);
		if(!equation3.hasRealRoot()) {
			System.out.println("\tTest failed with coefficients " + Arrays.toString(equation2.getCoefficients()) + " failed. hasRealRoot reported false, should be true.");
			return false;
		}

		return true;
	}

	/**
	 * Check if the class solves the exercise.
	 * Test x^2 + p x + q = 0.
	 *
	 * @param theClass The class to test;
	 * @param p The coefficient p.
	 * @param q The coefficient q.
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkWithCoefficients(QuadraticEquationFactory quadraticEquationFactory, double q, double p) {
		System.out.println("\tTesting with coefficients q = " + q + ", p = " + p);

		QuadraticEquation equation = quadraticEquationFactory.createQuadraticEquation(q, p);

		double x = equation.getSmallestRoot();
		System.out.println("\t\tSolution reported x = " + x);

		if(Double.isNaN(x)) {
			System.out.println("\t\tTest failed. Solution failed to calculate.");
			return false;
		}

		double error = (x*x + p * x + q) / (1+2*x*x+Math.abs(x*p));
		System.out.println("\t\tThe relative error of the solution is " + error);

		boolean success = Math.abs(error) < accuracy;
		if(!success) {
			System.out.println("\t\u274cTest failed.");
		}
		else {
			System.out.println("\t\u2705Test passed.");
		}

		return success;
	}
}
