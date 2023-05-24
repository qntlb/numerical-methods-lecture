/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020, 01.05.2022
 */
package info.quantlab.numericalmethods.lecture.computerarithmetics.summation;

/**
 * A class wrapping a vector of floating point doubles[] that allows to perform a summation.
 *
 * @author Christian Fries
 */
public interface DoubleVector {

	/**
	 * Returns the i-th element of the vector (where i ranges from 0 (included) to size() (excluded).
	 *
	 * @return The i-th element of the vector.
	 */
	double get(int i);

	/**
	 * Calculates the sum of the elements of the vector, using error correction.
	 *
	 * @return The sum of the elements of the vector.
	 */
	double sum();

	/**
	 * Returns the number of elements of this vector.
	 *
	 * @return The number of elements of this vector.
	 */
	long size();

	/**
	 * Calculates the averages of the elements of the vector.
	 * If size() is not zero, returns sum()/size(), otherwise Double.NAN.
	 *
	 * @return If size() is not zero, returns sum()/size(), otherwise Double.NAN.
	 */
	default double average() {
		if(size() == 0) return Double.NaN;

		return sum() / size();
	}
}
