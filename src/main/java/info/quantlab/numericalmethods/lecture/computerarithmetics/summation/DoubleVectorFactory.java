package info.quantlab.numericalmethods.lecture.computerarithmetics.summation;

public interface DoubleVectorFactory {

	/**
	 * Factory method creating an instance of an object
	 * implementing DoubleVector with the given array of values.
	 * 
	 * @return Object implementing DoubleVector using the given values.
	 */
	DoubleVector createDoubleVector(double[] values);
}
