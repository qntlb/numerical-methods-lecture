/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics.check;

import java.util.Arrays;
import java.util.Random;

import info.quantlab.numericalmethods.lecture.computerarithmetics.DoubleVector;
import info.quantlab.numericalmethods.lecture.computerarithmetics.QuadraticEquation;
import info.quantlab.reflection.ObjectConstructor;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;

public class QuadraticEquationImplemenationChecker {

	private static double accuracy = 1E-12;
	private static Random random = new Random(3141);

	/**
	 * Check if the class solves the exercise.
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean check(Class<?> theClass) {
		boolean succes = true;
		
		succes &= checkWithCoefficients(theClass, 100000.0, -1.0);
		succes &= checkWithCoefficients(theClass, -100000.0, -1.0);
		
		return succes;
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
		QuadraticEquation equation = ObjectConstructor.<QuadraticEquation>create(theClass, QuadraticEquation.class, testArgument);
		
		double x = equation.getSmallestRoot();
		
		double error = x*x - 2 * p * x + q;
		if(Math.abs(error) > accuracy) {
			System.out.println("Test failed with coefficients " + Arrays.toString(testArgument) + " failed. The error is " + error);
			return false;
		}

		return true;
	}
}
