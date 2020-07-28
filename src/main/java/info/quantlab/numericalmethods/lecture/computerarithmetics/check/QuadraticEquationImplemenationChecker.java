/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics.check;

import java.util.Arrays;
import java.util.Random;

import info.quantlab.numericalmethods.lecture.computerarithmetics.QuadraticEquation;
import info.quantlab.reflection.ObjectConstructor;

public class QuadraticEquationImplemenationChecker {

	private static double accuracy = 1E-12;
	private static Random random = new Random();

	/**
	 * Check if the class solves the exercise.
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean check(Class<?> theClass) {
		boolean succes = true;

		/*
		 * Check two extrem cases
		 */
		succes &= checkWithCoefficients(theClass,  100000.0, -1.0);
		succes &= checkWithCoefficients(theClass, -100000.0, -1.0);

		/*
		 * Check a random case
		 */
		double pAbs = 10000*(1.0-random.nextDouble());	// 0 not included
		double sign = random.nextBoolean() ? 1.0 : -1.0;
		double p = sign * pAbs;
		double q = p*p*random.nextDouble()-1.0;		// q < p*p
		succes &= checkWithCoefficients(theClass, p, q);

		/*
		 * Check hasRealRoot
		 */
		succes &= checkHasRealRoot(theClass);

		return succes;
	}

	/**
	 * Check if the class solves the exercise with respect to the method <code>hasRealRoot</code>.
	 * Test x^2 - 2 p x + q = 0.
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	private static boolean checkHasRealRoot(Class<?> theClass) {
		double[] testArgument = new double[] { 2.0 , 0.5 };
		QuadraticEquation equation = ObjectConstructor.<QuadraticEquation>create(theClass, QuadraticEquation.class, testArgument);
		if(equation.hasRealRoot()) {
			System.out.println("\tTest failed with coefficients " + Arrays.toString(testArgument) + " failed. hasRealRoot reported true, should be false.");
			return false;
		}

		return true;
	}

	/**
	 * Check if the class solves the exercise.
	 * Test x^2 - 2 p x + q = 0.
	 *
	 * @param theClass The class to test;
	 * @param p The coefficient p.
	 * @param q The coefficient q.
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkWithCoefficients(Class<?> theClass, double p, double q) {
		double[] testArgument = new double[] { q, -2*p };
		System.out.println("\tTesting with coefficients " + Arrays.toString(testArgument));

		QuadraticEquation equation = ObjectConstructor.<QuadraticEquation>create(theClass, QuadraticEquation.class, testArgument);

		double x = equation.getSmallestRoot();
		System.out.println("\t\tSolution reported x = " + x);

		if(Double.isNaN(x)) {
			System.out.println("\t\tTest failed. Solution failed to calculate.");
			return false;
		}

		double error = (x*x - 2 * p * x + q) / (1.0+Math.pow(x,2));
		if(Math.abs(error) > accuracy) {
			System.out.println("\t\tTest failed. The error is " + error);
			return false;
		}
		else {
			System.out.println("\t\tTest passed. The error is " + error);
		}

		return true;
	}
}
