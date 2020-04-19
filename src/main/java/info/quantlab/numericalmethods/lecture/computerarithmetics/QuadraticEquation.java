/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics;

public interface QuadraticEquation {
	
	/**
	 * Returns the array { a_0 , a_1 } with the coefficients of the quadratic from
	 * \(  x^2 + a_1 x + a_0 = 0 \)
	 * 
	 * @return An array of length 2 with the constant and linear coefficient.
	 */
	double[] getCoefficients();
	
	/**
	 * Returns true if at least one real value root exists.
	 * 
	 * @return True if at least one real value root exists.
	 */
	boolean hasRealRoot();

	/**
	 * Returns the smallest root of the quadratic equation, if it exists.
	 * 
	 * @return The smallest root of the quadratic equation, if it exists.
	 */
	double getSmallestRoot();
}
