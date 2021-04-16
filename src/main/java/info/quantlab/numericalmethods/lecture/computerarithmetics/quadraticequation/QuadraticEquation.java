/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics.quadraticequation;

public interface QuadraticEquation {

	/**
	 * Returns the array { a_0 , a_1 } with the coefficients of the quadratic from
	 * \(  x^2 + a_1 x + a_0 = 0 \)
	 * Note: the first element of the array is the constant coefficient, the second element is the linear coefficient.
	 *
	 * @return An array of length 2 with the constant and linear coefficient.
	 */
	double[] getCoefficients();

	/**
	 * Returns true if at least one real value root exists, otherwise false.
	 *
	 * @return True if at least one real value root exists, otherwise false.
	 */
	boolean hasRealRoot();

	/**
	 * If a real root exists, the method returns the smallest root of the quadratic equation.
	 * Otherwise it returns Double.NaN.
	 *
	 * @return The smallest root of the quadratic equation, if it exists, otherwise NaN.
	 */
	double getSmallestRoot();
}
