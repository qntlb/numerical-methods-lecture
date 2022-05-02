/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020m 01.05.2022
 */
package info.quantlab.numericalmethods.assignments.computerarithmetics.check;

import java.util.Random;

import info.quantlab.numericalmethods.lecture.computerarithmetics.summation.DoubleVector;
import info.quantlab.numericalmethods.lecture.computerarithmetics.summation.DoubleVectorFactory;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;

public class DoubleVectorImplemenationChecker {

	private static double accuracy = 1E-11;
	private static Random random = new Random(3141);

	/**
	 * Check if the class solves the exercise.
	 *
	 * @param solution The class to test;
	 * @param whatToCheck A string, currently "basic" or "accuracy".
	 * @return Boolean if the test is passed.
	 */
	public static boolean check(DoubleVectorFactory solution, String whatToCheck) {
		System.out.println("Running " + whatToCheck + " test on " + solution.getClass().getCanonicalName());

		boolean success = false;
		try {
			switch(whatToCheck) {
			case "basic":
			default:
			{
				boolean checkSum = checkSimpleArray(solution) && checkRandom(solution);
				success = checkSum;
			}
			break;
			case "accuracy":
			{
				boolean checkSum = checkSimpleArray(solution) && checkRandom(solution);
				boolean checkAccuracy = checkAccuracy(solution);
				if(checkSum & !checkAccuracy) {
					System.out.println("You almost solved the exercise. The sum is approximately correct, but not accurate enough.");
				}
				success = checkSum & checkAccuracy;
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
	 * Simple array
	 *
	 * @param solution The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkSimpleArray(DoubleVectorFactory solution) {
		double[] testArgument = new double[] { 1, 2, 3 };

		DoubleVector vector = solution.createDoubleVector(testArgument);

		if(vector.get(0) != testArgument[0] || vector.get(1) != testArgument[1]) {
			System.out.println("\t\u274cSimple test failed: get appears to be wrong.");
			return false;
		}

		if(vector.size() != testArgument.length) {
			System.out.println("\t\u274cSimple test failed: size ist not correct.");
			return false;
		}

		if(vector.sum() != 6) {
			System.out.println("\t\u274cSimple test failed: sum ist not correct.");
			return false;
		}

		System.out.println("\t\u2705Simple test passed.");
		return true;
	}

	/**
	 * Random array test
	 *
	 * @param solution The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkRandom(DoubleVectorFactory solution) {
		double[] testArgument = new double[1000];
		for(int i=0; i<testArgument.length; i++) {
			testArgument[i] = random.nextDouble();
		}
		double testSum = new RandomVariableFromDoubleArray(0.0, testArgument).getAverage()*testArgument.length;

		DoubleVector vector = solution.createDoubleVector(testArgument);

		double error = testSum - vector.sum();
		if(Math.abs(error) > accuracy) {
			System.out.println("\t\u274cRandom array test failed. The error is " + error);
			return false;
		}
		else {
			System.out.println("\t\u2705Random array test passed.");
		}

		return true;
	}

	/**
	 * Small and large numbers array
	 *
	 * @param solution The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean checkAccuracy(DoubleVectorFactory solution) {
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

		DoubleVector vector = solution.createDoubleVector(testArgument);

		double error = testSum - vector.sum();
		if(Math.abs(error) > accuracy) {
			System.out.println("\t\u274cAccuracy test failed. Accuracy is too low. The error is " + error);
			System.out.println("\tHint: Check the numerical methods lecture on how to improve the accuracy of a summation.");
			return false;
		}
		else {
			System.out.println("\t\u2705Accuracy test passed.");
		}

		return true;
	}
}
