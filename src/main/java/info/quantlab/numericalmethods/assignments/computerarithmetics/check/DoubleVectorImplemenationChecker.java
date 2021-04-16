/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.assignments.computerarithmetics.check;

import java.util.Random;

import info.quantlab.numericalmethods.lecture.computerarithmetics.summation.DoubleVector;
import info.quantlab.reflection.ObjectConstructor;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;

public class DoubleVectorImplemenationChecker {

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
		boolean checkSum = checkSimpleArray(theClass) && checkRandom(theClass);
		boolean checkAccuracy = check3(theClass);
		if(checkSum & !checkAccuracy) {
			System.out.println("You almost solved the exercise. The sum is approximately correct, but not accurate enough.");
		}
		if(whatToCheck.equals("basic")) return checkSum;
		else return checkSum & checkAccuracy;
	}

	/**
	 * Simple array
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkSimpleArray(Class<?> theClass) {
		double[] testArgument = new double[] { 1, 2, 3 };

		DoubleVector vector = ObjectConstructor.<DoubleVector>create(theClass, DoubleVector.class, testArgument);

		if(vector.sum() != 6) {
			System.out.println("\tSimple test failed.");
			return false;
		}
		else {
			System.out.println("\tSimple test passed.");
		}

		return true;
	}

	/**
	 * Random array test
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkRandom(Class<?> theClass) {
		double[] testArgument = new double[1000];
		for(int i=0; i<testArgument.length; i++) {
			testArgument[i] = random.nextDouble();
		}
		double testSum = new RandomVariableFromDoubleArray(0.0, testArgument).getAverage()*testArgument.length;

		DoubleVector vector = ObjectConstructor.<DoubleVector>create(theClass, DoubleVector.class, testArgument);

		double error = testSum - vector.sum();
		if(Math.abs(error) > accuracy) {
			System.out.println("\tRandom array test failed. The error is " + error);
			return false;
		}
		else {
			System.out.println("\tRandom array test passed.");
		}

		return true;
	}

	/**
	 * Small and large numbers array
	 *
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean check3(Class<?> theClass) {
		/*
		 * Create an array of 10000 times Math.Pi and 1 times 10000 * Math.Pi.
		 * Then check if the sum is 20000 times Math.Pi.
		 */
		double[] testArgument = new double[10001];
		for(int i=0; i<testArgument.length; i++) {
			testArgument[i] = Math.PI;
		}
		testArgument[5000] = 10000*Math.PI;
		double testSum = 20000*Math.PI;

		DoubleVector vector = ObjectConstructor.<DoubleVector>create(theClass, DoubleVector.class, testArgument);

		double error = testSum - vector.sum();
		if(Math.abs(error) > accuracy) {
			System.out.println("\tAccuracy test failes. Accuracy is too low. The error is " + error);
			return false;
		}
		else {
			System.out.println("\tAccuracy test passed.");
		}

		return true;
	}
}
