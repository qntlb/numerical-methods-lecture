/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christian-fries.de.
 * 
 * Created on 23.04.2020
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

/**
 * A simple class illustrating some aspects related to
 * integer arithmetic and floating point arithmetic.
 * 
 * @author Christian Fries
 */
public class ComputerArithmeticExperiment {

	public static void main(String[] args) {

		System.out.println("\nSome experiments related to integer arithmetic.\n");
		System.out.println("_".repeat(79));

		/*
		 * Explore Integer
		 */

		System.out.println("Behaviour of Integer.MAX_VALUE:\n");

		int i = Integer.MAX_VALUE;

		int iPlusOne = i+1;

		System.out.println("i     = " + i);
		System.out.println("i+1   = " + iPlusOne);

		System.out.println("i > i+1 is " + (i > i+1));

		System.out.println("_".repeat(79));


		/*
		 * Explore Double
		 */

		System.out.println("\nSome experiments related to floating point arithmetic (IEEE 754).\n");
		System.out.println("_".repeat(79));

		/*
		 * Double: Smallest positive number
		 */

		System.out.println("Smallest positive number:\n");

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
		 * Double: Smallest positive number x = eps for which 1+2x != 1
		 */

		System.out.println("Smallest positive number with 1+2x != 1 :\n");

		double eps = 1.0;

		while(1 + eps > 1) {
			eps = eps / 2.0;
		}

		System.out.println("eps ....... = " + eps);
		System.out.println("1+eps ..... = " + (1+eps));
		System.out.println("1+eps == 1 is " + ((1+eps)==1));
		System.out.println("1+2*eps ... = " + (1+2*eps));

		System.out.println("_".repeat(79));
	}

}
