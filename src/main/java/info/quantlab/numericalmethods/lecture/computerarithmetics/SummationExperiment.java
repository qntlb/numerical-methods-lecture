/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 21.04.2021, 05.05.2022
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

import java.util.Arrays;

/**
 * A simple class illustrating some aspects related to floating point arithmetic.
 * Experiments related to summation.
 *
 * See also {@link FloatingPointArithmeticExperiment}.
 * 
 * @author Christian Fries
 */
public class SummationExperiment {

	public static void main(String[] args) {

		experimentWithSameValues();
		
		experimentWithOneAndEpsilon();

	}

	private static void experimentWithSameValues() {
		/*
		 * Experiment on loss of significance - summation 1: always the same value
		 */

		System.out.println("Experiment on loss of significance - summation: average of same values.\n");

		double value = 0.1;
		int numberOfValues = 10000000; // Change this to 10 or to 10000000 (10 million).
		
		// Initialize the array we like to sum (no needed, but mimics the general case)
		double[] realizations = new double[numberOfValues];
		Arrays.fill(realizations, value);

		double sumOfValuesClassical = getSumOfValuesClassical(realizations);
		double averageClassical = sumOfValuesClassical / numberOfValues;

		System.out.println("Classical summation average = " + averageClassical);

		double sumOfValueKahan = getSumOfValuesKahan(realizations);
		double averageKahan = sumOfValueKahan / numberOfValues;
		System.out.println("Kahan     summation average = " + averageKahan);

		System.out.println("_".repeat(79)+"\n");
	}

	private static void experimentWithOneAndEpsilon() {
		/*
		 * Experiment on loss of significance - summation 2: one large value, many small		 */

		System.out.println("Experiment on loss of significance - summation: one large, many small.\n");

		// Array we like to sum: all epsilon, expect the first entry is 1.0
		double value = 0.5*Math.ulp(1.0);
		int numberOfValues = 10000000; // Change this to 10 or to 10000000 (10 million).
		double[] realizations = new double[numberOfValues];
		Arrays.fill(realizations, value);
		realizations[0] = 1.0;

		double sumOfValuesClassical = getSumOfValuesClassical(realizations);

		System.out.println("Classical summation = " + sumOfValuesClassical);

		double sumOfValueKahan = getSumOfValuesKahan(realizations);
		System.out.println("Kahan     summation = " + sumOfValueKahan);

		System.out.println("_".repeat(79)+"\n");
	}

	private static double getSumOfValuesClassical(double[] realizations) {
		double sum = 0.0;
		for(int i=0; i<realizations.length; i++) {
			double value = realizations[i];
			sum = sum + value;
		}
		return sum;
	}

	private static double getSumOfValuesKahan(double[] realizations) {
		double sum = 0.0;
		double error = 0.0;
		for(int i=0; i<realizations.length; i++) {
			double value = realizations[i];
			double newValue = value - error;
			double newSum = sum + newValue;
			error = (newSum - sum) - newValue;
			sum = newSum;
		}
		return sum;
	}

}
