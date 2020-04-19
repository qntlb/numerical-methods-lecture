package info.quantlab.numericalmethods.lecture.computerarithmetics;

public interface QuadraticEquation {
	
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
