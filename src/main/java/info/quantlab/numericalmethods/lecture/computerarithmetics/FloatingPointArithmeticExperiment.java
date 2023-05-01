/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 23.04.2020, 26.04.2022
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

/**
 * A simple class illustrating some aspects related to floating point arithmetic.
 *
 * @author Christian Fries
 */
public class FloatingPointArithmeticExperiment {

	public static void main(String[] args) {

		/*
		 * Explore Double
		 */

		System.out.println("\nSome experiments related to floating point arithmetic (IEEE 754).\n");
		System.out.println("_".repeat(79)+"\n");

		/*
		 * Double: Smallest positive number
		 */

		System.out.println("Smallest positive number 2^{-k}:\n");

		double tiny = 1.0;
		while(tiny/2.0 > 0 && tiny/2.0 < tiny) {
			tiny = tiny / 2.0;
		}

		System.out.println("tiny............ = " + tiny);

		System.out.println("2^{eMin-p}...... = " + Math.pow(2,-1022 - 52));

		System.out.println("tiny/2.......... = " + tiny/2);
		System.out.println("tiny/2*2........ = " + (tiny/2)*2);
		System.out.println("tiny*2/2........ = " + (tiny*2)/2);

		System.out.println("_".repeat(79)+"\n");

		/*
		 * Double: Smallest positive (i.e. non-zero) number x = eps for which 1+2x > 1
		 */

		System.out.println("Smallest positive number with 1+2x != 1 and 1+x = 1:\n");

		double eps = 1.0;
		while(1+eps > 1 && 1+eps < 1+2*eps) {
			eps = eps / 2.0;
		}

		System.out.println("eps ....... = " + eps);
		System.out.println("1+eps ..... = " + (1+eps));
		System.out.println("1+eps == 1 is " + ((1+eps)==1));
		System.out.println("1+2*eps ... = " + (1+2*eps));
		System.out.println("2^(-p)/2... = " + Math.pow(2, -52-1));	// For double mantissa has p = 52 bits
		
		System.out.println();

		System.out.println("1+1*eps ... = " + (1+1*eps));
		System.out.println("1+2*eps ... = " + (1+2*eps));
		System.out.println("1+3*eps ... = " + (1+3*eps));
		System.out.println("1+4*eps ... = " + (1+4*eps));
		System.out.println("1+5*eps ... = " + (1+5*eps));
		System.out.println("1+6*eps ... = " + (1+6*eps));

		System.out.println("_".repeat(79)+"\n");

		
		/*
		 * Experiments with Double.MAX_VALUE
		 */

		System.out.println("Small experiments with Double.MAX_VALUE (largest positive floating point number (before infinity).\n");

		double maxDouble	= Double.MAX_VALUE;
		double bigStep		= 0.49 * Math.pow(2, 1023-52);		// Largest scale (1 + c / 2^52) * 2^1023, i.e. steps are 1/2^52 * 2^1023
		double bigerStep	= 0.50 * Math.pow(2, 1023-52);

		System.out.println("maxDouble            = " + maxDouble);
		System.out.println("maxDouble+1000       = " + (maxDouble+1000));
		System.out.println("maxDouble+bigStep    = " + (maxDouble+bigStep));
		System.out.println("maxDouble+bigerStep  = " + (maxDouble+bigerStep));
		System.out.println("maxDouble+maxDouble  = " + (maxDouble+maxDouble));
		System.out.println();
		System.out.println("maxDouble+maxDouble-maxDouble   = " + (maxDouble+maxDouble-maxDouble));
		System.out.println("maxDouble+(maxDouble-maxDouble) = " + (maxDouble+(maxDouble-maxDouble)));

		System.out.println("_".repeat(79)+"\n");

		/*
		 * Small experiments with special values like Infinity and NaN
		 */

		System.out.println("Small experiments with special values like Infinity and NaN.\n");

		double zero = 0.0;
		double oneOverZero = 1.0/zero;

		System.out.println("1/0         = " + oneOverZero);

		// Checking +0

		double plusZero = 1.0;
		while(plusZero > 0) {
			plusZero = plusZero / 2.0;
		}
		System.out.println("+0          = " + plusZero);

		double minusZero = -1.0;
		while(minusZero < 0) {
			minusZero = minusZero / 2.0;
		}
		System.out.println("-0          = " + minusZero);

		System.out.println("+0 == -0   is " + (plusZero == minusZero));
		System.out.println("1/(+0)      = " + (1.0/plusZero));
		System.out.println("1/(-0)      = " + (1.0/minusZero));

		double plusInfinity = 1.0/plusZero;
		double minusInfinity = 1.0/minusZero;

		System.out.println("+Infinity  +  -Infinity = " + (plusInfinity+minusInfinity));

		double nan = Double.NaN;

		System.out.println("NaN         = " + nan);

		System.out.println("sqrt(-1)    = " + Math.sqrt(-1.0));

		System.out.println("1+NaN       = " + (1+nan));

		System.out.println("1+infinity  = " + (1 + plusInfinity));

		System.out.println("_".repeat(79)+"\n");

		/*
		 * Experiment on loss of significance - solve a quadratic equation x^2 + p*x + q = 0
		 */

		System.out.println("Experiment on loss of significance - solve a quadratic equation x^2 + p*x + q = 0.\n");

		double p = -10000000.0;
		double q = 1.0;

		double x1 = getSmallestRootOfQuadraticEquationVersion1(p,q);
		System.out.println("..................p = " +p);
		System.out.println("..................q = " +q);
		System.out.println("Solution.........x1 = " +x1);

		System.out.println("x^2 + px + q (with x1) = " + (x1*x1 + p*x1 +q) );

		double x2 = getSmallestRootOfQuadraticEquationVersion2(p,q);

		System.out.println("Solution.........x2 = " +x2);
		System.out.println("x^2 + px +q (with x1) = " + (x2*x2 + p*x2 +q) );

		System.out.println("_".repeat(79)+"\n");

		/*
		 * Experiment on loss of significance - summation
		 */

		System.out.println("Experiment on loss of significance - summation.\n");

		double value = 0.1;
		int numberOfValues = 10;		// change this to 10000000;

		double sumOfValuesClassical = getSumOfValuesClassical(value, numberOfValues);
		double averageClassical = sumOfValuesClassical / numberOfValues;

		System.out.println("Classical summation average = " + averageClassical);

		double sumOfValueKahan = getSumOfValuesKahan(value, numberOfValues);
		double averageKahan = sumOfValueKahan / numberOfValues;
		System.out.println("Kahan     summation average = " + averageKahan);

		System.out.println("_".repeat(79)+"\n");

	}


	/**
	 * Returns the smallest root of x^2 + px + q = 0
	 *
	 * @param p coefficient p
	 * @param q coefficient q
	 * @return The root
	 */
	private static double getSmallestRootOfQuadraticEquationVersion1(double p, double q) {
		return -p/2 - Math.sqrt(p*p/4 - q);
	}

	/**
	 * Returns the smallest root of x^2 + px + q = 0
	 *
	 * @param p coefficient p
	 * @param q coefficient q
	 * @return The root
	 */
	private static double getSmallestRootOfQuadraticEquationVersion2(double p, double q) {
		return q / (-p/2 + Math.sqrt(p*p/4 -q));
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

	private static double getSumOfValuesClassical(double value, int numberOfValues) {
		double sum = 0.0;
		for(int i=0; i<numberOfValues; i++) {
			sum = sum + value;
		}
		return sum;
	}
}
