/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 23.04.2020, 26.04.2022
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

/**
 * A simple class illustrating some aspects related to floating point arithmetic.
 * Here: "loss of significance" for the solution of a quadratic equation.
 * 
 * See also {@link FloatingPointArithmeticExperiment}.
 *
 * @author Christian Fries
 */
public class QuadraticEquationExperiment {

	public static void main(String[] args) {

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
}
