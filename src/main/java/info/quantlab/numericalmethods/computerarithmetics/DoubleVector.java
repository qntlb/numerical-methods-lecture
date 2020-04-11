package info.quantlab.numericalmethods.computerarithmetics;

public interface DoubleVector {

	/**
	 * Create a vector from a given array.
	 * 
	 * @return A class implementing DoubleVector
	 */
	DoubleVector of(double[] values);

	/**
	 * Calculate the sum of the elements of the vector.
	 * 
	 * @return The sum of the elements of the vector.
	 */
	double sum();
}
