/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 21.04.2021, 05.05.2022
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

/**
 * A simple class illustrating some aspects related to floating point arithmetic.
 * Experiments related to summation.
 *
 * @author Christian Fries
 */
public class SummationExperiment {

	public static void main(String[] args) {

		/*
		 * Experiment on loss of significance - summation
		 */

		System.out.println("Experiment on loss of significance - summation.\n");

		double value = 0.1;
		int numberOfValues = 10000000; // Change this to 10 or to 10000000 (10 million).

		double sumOfValuesClassical = getSumOfValuesClassical(value, numberOfValues);
		double averageClassical = sumOfValuesClassical / numberOfValues;

		System.out.println("Classical summation average = " + averageClassical);

		double sumOfValueKahan = getSumOfValuesKahan(value, numberOfValues);
		double averageKahan = sumOfValueKahan / numberOfValues;
		System.out.println("Kahan     summation average = " + averageKahan);

		System.out.println("_".repeat(79)+"\n");

	}

	private static double getSumOfValuesClassical(double value, int numberOfValues) {
		double sum = 0.0;
		for(int i=0; i<numberOfValues; i++) {
			sum = sum + value;
		}
		return sum;
	}

	private static double getSumOfValuesKahan(double value, int numberOfValues) {
		double sum = 0.0;
		double error = 0.0;
		for(int i=0; i<numberOfValues; i++) {
			double newValue = value - error;
			double newSum = sum + newValue;
			error = (newSum - sum) - newValue;
			sum = newSum;
		}
		return sum;
	}

}
