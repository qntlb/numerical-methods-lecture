/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 17.04.2024
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

/**
 * A simple class illustrating some aspects related to floating point numbers.
 *
 * @author Christian Fries
 */
public class FloatingPointNumbersExperiment {

	public static void main(String[] args) {

		/*
		 * Explore Double
		 */

		System.out.println("\nSome experiments related to floating point numbers (IEEE 754).\n");
		System.out.println("_".repeat(79)+"\n");

		/*
		 * Double: ULP
		 */
		
		System.out.println("Unit in the Last Place (ULP) as reported by Math::ulp:\n");

		double ulp1 = Math.ulp(1.0);
		System.out.println("ulp(1).......... = " + ulp1);
		System.out.println("2^{-52}......... = " + Math.pow(2, -52));

		double ulp0 = Math.ulp(0.0);
		System.out.println("ulp(0).......... = " + ulp0);
		System.out.println("2^{eMin-q}...... = " + Math.pow(2,-1022 - 52));

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
		System.out.println("Double.MIN_VALUE = " + Double.MIN_VALUE);

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
		System.out.println("2^(-q)/2... = " + Math.pow(2, -52-1));	// For double mantissa has q = 52 bits

		System.out.println("_".repeat(79)+"\n");

		/*
		 * Small experiments with +0 / -0
		 */

		System.out.println("Comparing +0 and -0:\n");

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
	}
}
