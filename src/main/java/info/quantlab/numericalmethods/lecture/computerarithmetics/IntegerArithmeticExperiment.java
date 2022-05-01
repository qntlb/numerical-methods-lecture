/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 23.04.2020, 26.04.2022
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

/**
 * A simple class illustrating some aspects related to integer arithmetic
 *
 * @author Christian Fries
 */
public class IntegerArithmeticExperiment {

	public static void main(String[] args) {

		/*
		 * Explore Integer
		 */

		System.out.println("\nSome experiments related to integer arithmetic.\n");
		System.out.println("_".repeat(79)+"\n");

		/*
		 * Behaviour of MAX_VALUE
		 */

		System.out.println("Behaviour of Integer.MAX_VALUE:\n");

		int i = Integer.MAX_VALUE;

		int iPlusOne = i+1;

		System.out.println("i     = " + i);
		System.out.println("i+1   = " + iPlusOne);

		System.out.println("i > i+1 is " + (i > i+1));

		System.out.println("_".repeat(79)+"\n");

		/*
		 * Experiment on overflow and remainder (%)
		 *
		 * For the remainder we have ((a % c) + (b % c)) % c = (a+b)%c,
		 * but this is violated after an overflow.
		 */

		System.out.println("The overflow will also lead that some rules for the modulus (%) are violated:\n");

		// The sum of integerBig and intergerSmall will result in an overflow
		int integerBig = Integer.MAX_VALUE-9;
		int integerSmall = 20;
		int modulus = 13;
		int modOfBig = integerBig % modulus;
		int modOfSmall = integerSmall % modulus;


		int sumOfModulusMod = (modOfBig+modOfSmall) % modulus;
		int modulusOfSum = (integerBig+integerSmall) % modulus;

		System.out.println("Large integer a = " + integerBig);
		System.out.println("Modulus c       = " + modulus);
		System.out.println("a % c           = " + modOfBig);

		System.out.println("Small integer b = " + integerSmall);
		System.out.println("b % c           = " + modOfSmall);
		
		System.out.println("((a % c) + (b % c)) % c = " + sumOfModulusMod);
		System.out.println("(a + b) % c             = " + modulusOfSum);
	}
}
