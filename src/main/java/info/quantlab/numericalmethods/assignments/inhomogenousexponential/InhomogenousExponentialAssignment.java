package info.quantlab.numericalmethods.assignments.inhomogenousexponential;

import java.util.function.DoubleSupplier;

import info.quantlab.numericalmethods.lecture.randomnumbers.RandomNumberGenerator1D;

public interface InhomogenousExponentialAssignment {

	DoubleSupplier createRandomNumberGeneratorInhomogenousExponential(RandomNumberGenerator1D uniformSequence, double[] times, double[] intensities);

}
