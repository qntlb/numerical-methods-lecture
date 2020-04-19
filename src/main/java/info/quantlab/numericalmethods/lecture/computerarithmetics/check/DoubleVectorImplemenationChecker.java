/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics.check;

import java.util.Random;

import info.quantlab.numericalmethods.lecture.computerarithmetics.DoubleVector;
import info.quantlab.reflection.ObjectConstructor;
import net.finmath.montecarlo.RandomVariableFromDoubleArray;

public class DoubleVectorImplemenationChecker {

	private static double accuracy = 1E-7;
	private static Random random = new Random(3141);

	/**
	 * Check if the class solves the exercise.
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean check(Class<?> theClass) {
		return check1(theClass) && check2(theClass) && check3(theClass);
	}

	/**
	 * Simple array
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean check1(Class<?> theClass) {
		double[] testArgument = new double[] { 1, 2, 3 };

		DoubleVector vector = ObjectConstructor.<DoubleVector>create(theClass, DoubleVector.class, testArgument);

		if(vector.sum() != 6) {
			System.out.println("Simple test failed.");
			return false;
		}

		return true;
	}

	/**
	 * Random array test
	 * 
	 * @param theClass The class to test;
	 * @return Boolean if the test is passed.
	 */
	public static boolean check2(Class<?> theClass) {
		double[] testArgument = new double[1000];
		for(int i=0; i<testArgument.length; i++) {
			testArgument[i] = random.nextDouble();
		}
		double testSum = new RandomVariableFromDoubleArray(0.0, testArgument).getAverage()*testArgument.length;

		DoubleVector vector = ObjectConstructor.<DoubleVector>create(theClass, DoubleVector.class, testArgument);
		
		double error = testSum - vector.sum();
		if(Math.abs(error) > accuracy) {
			System.out.println("Random test failed. The error is " + error);
			return false;
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
		double[] testArgument = new double[10001];
		for(int i=0; i<testArgument.length; i++) {
			testArgument[i] = Math.PI;
		}
		testArgument[5000] = 10000*Math.PI;
		double testSum = 20000*Math.PI;

		DoubleVector vector = ObjectConstructor.<DoubleVector>create(theClass, DoubleVector.class, testArgument);
		
		double error = testSum - vector.sum();
		if(Math.abs(error) > accuracy) {
			System.out.println("Small and large number test failed. The error is " + error);
			return false;
		}

		return true;
	}
	
}
