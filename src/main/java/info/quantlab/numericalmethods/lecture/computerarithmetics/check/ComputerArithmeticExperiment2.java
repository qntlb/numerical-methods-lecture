/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christian-fries.de.
 * 
 * Created on 23.04.2020
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics.check;

/**
 * A simple class illustrating some aspects related to
 * integer arithmetic and floating point arithmetic.
 * 
 * @author Christian Fries
 */
public class ComputerArithmeticExperiment2 {

	public static void main(String[] args) {

		System.out.println("\nSome experiments related to integer.\n");

		/*
		 * Explore Integer
		 */

		int i = Integer.MAX_VALUE;

		int iPlusOne = i+1;

		System.out.println("i     = " + i);
		System.out.println("i+1   = " + iPlusOne);

		System.out.println("i > i+1 is " + (i > i+1));

		System.out.println("_".repeat(79));


		/*
		 * Explore Double
		 */

		System.out.println("\nSome experiments related to computer arithmetic (IEEE 754).\n");

		/*
		 * Double: Smallest positive number
		 */

		double tiny = 1.0;

		while(tiny/2.0 > 0) {
			tiny = tiny/2.0;
		}

		System.out.println("tiny       = " + tiny);
		System.out.println("tiny/2     = " + tiny/2);
		System.out.println("tiny/2*2   = " + (tiny/2)*2);
		System.out.println("tiny*2/2   = " + (tiny*2)/2);

		System.out.println("_".repeat(79));

		/*
		 * Double: Smallest positive number x = eps for which 1+x = x and 1+2x != x
		 */

		/*
		 * Test 1
		 */
		System.out.println("Test 1: Find the machine precision.");

		double epsilon = getMachinePrecision();

		System.out.println("Machine precision: epsilon = " + epsilon);

		System.out.println("_________________________________________________________");
		System.out.println("");

		System.out.println("eps ....... = " + epsilon);
		System.out.println("1+eps ..... = " + (1+epsilon));
		System.out.println("1+eps==1...is " + ((1+epsilon)==1));
		System.out.println("1+2*eps ... = " + (1+2*epsilon));
		System.out.println("0.1 + eps == 0.1 is " + (0.1+epsilon == 0.1));
		
		System.out.println("2^(-(52+1)) = " + Math.pow(2, -53));

		System.out.println("_".repeat(79));



		
		
		/*
		 * Test 2
		 */
		System.out.println("Test 2: Check behavior of Double.MAX_VALUE");
		
		double x = Double.MAX_VALUE;
		System.out.println("Double.MAX_VALUE                       = " + x);
		System.out.println("Double.MAX_VALUE + 1                   = " + (x+1));
		System.out.println("Double.MAX_VALUE + Double.MAX_VALUE    = " + (x+x-x));
		
		System.out.println("_________________________________________________________");
		System.out.println("");

		/*
		 * Test 3
		 */
		System.out.println("Test 3: Check behavior +0 and -0.");

		// Create +0
		double zero = 1;
		while(zero > 0) {
			zero = zero / 2;
		}
		System.out.println("     zero   = " + zero);
		
		// Create -0
		double minusZero = -1;
		while(minusZero < 0) {
			minusZero = minusZero / 2;
		}
		System.out.println("minusZero   = " + minusZero);

		// Example where +0 and -0 makes a difference
		System.out.println("1/zero      = " + 1/zero);
		System.out.println("1/minusZero = " + 1/minusZero);

		// Check +0 and -0
		System.out.println("Testing 0 == -0 gives " + (zero == minusZero));

		System.out.println("_________________________________________________________");
		System.out.println("");

		/*
		 * Test 4
		 */
		System.out.println("Test 4: Loss of significance in the solution of a quadratic equation.");

		double p = 100000000.0;
		double q = 1.0;

		double x1 = getSmallestSolutionOfQuadraticEquation1(p,q);

		System.out.println("Method 1:");
		System.out.println("x = " + x1);
		System.out.println("Test: x^2-2px+q = " + (x1*x1 - 2*p*x1 +q)  );

		double x2 = getSmallestSolutionOfQuadraticEquation2(p,q);

		System.out.println("Method 2:");
		System.out.println("x = " + x2);
		System.out.println("Test: x^2-2px+q = " + (x2*x2 - 2*p*x2 +q)  );

		System.out.println("_________________________________________________________");
		System.out.println("");

		/*
		 * Test 5
		 */
		System.out.println("Test 5: Compare classical summation and Kahan summation.");

		double value = 0.1;
		int numberOfSumations = 10000000;

		double sumClassical = getSumOfNumberClassical(value, numberOfSumations);
		double averageClassical = sumClassical / numberOfSumations;

		System.out.println("Average (classic) = " + averageClassical);
		System.out.println("Error (classic)   = " + (averageClassical-value)/value);

		
		
		double sumKahan = getSumOfNumberKahan(value, numberOfSumations);
		double averageKahan = sumKahan / numberOfSumations;

		System.out.println("Average (Kahan)   = " + averageKahan);
		System.out.println("Error (classic)   = " + (averageKahan-value)/value);
	}

	private static double getMachinePrecision() {
		double epsilon = 1.0;
		while(1.0+epsilon != 1.0) {
			epsilon /= 2.0;				// epsilon = epsilon / 2.0;
		}
		return epsilon;
	}

	private static double getSumOfNumberClassical(double value, int numberOfSumations) {
		double sum = 0.0;
		for(int i=0; i < numberOfSumations; i++) {
			sum = sum + value;
		}
		return sum;
	}

	private static double getSumOfNumberKahan(double value, int numberOfSumations) {
		double sum = 0.0;
		double error = 0.0;
		for(int i=0; i < numberOfSumations; i++) {
			double valueCorrected = value - error;
			double newSum = sum + valueCorrected;
			error = (newSum - sum) - valueCorrected;
			sum = newSum;
		}
		return sum;
	}

	private static double getSmallestSolutionOfQuadraticEquation1(double p, double q) {
		return p - Math.sqrt(p*p-q);
	}

	private static double getSmallestSolutionOfQuadraticEquation2(double p, double q) {
		return q / (p + Math.sqrt(p*p-q));
	}
}
