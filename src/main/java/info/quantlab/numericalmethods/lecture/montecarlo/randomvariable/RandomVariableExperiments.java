package info.quantlab.numericalmethods.lecture.montecarlo.randomvariable;

import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.montecarlo.RandomVariableFromFloatArray;
import net.finmath.stochastic.RandomVariable;

public class RandomVariableExperiments {

	public static void main(String[] args) {
		
		RandomVariable randomVariableDoublePrecision = new RandomVariableFromDoubleArray(0, new double[] { -1.0/3.0, -1.0/3.0,2.0/3.0 });
		printMoments(randomVariableDoublePrecision);

		RandomVariable randomVariableSinglePrecision = new RandomVariableFromFloatArray(0, new double[] { -1.0/3.0, -1.0/3.0, 2.0/3.0 });		
		printMoments(randomVariableSinglePrecision);
	}

	/**
	 * Prints out the moments E(X) and E(X^2)) for any object implementing <code>RandomVariable</code>.
	 * 
	 * @param randomVariable The random variable X.
	 */
	private static void printMoments(RandomVariable randomVariable) {
		System.out.println(randomVariable.getClass().getSimpleName());
		
		RandomVariable valueSquared = randomVariable.mult(randomVariable);
		
		RandomVariable expectation = randomVariable.expectation();
		RandomVariable expectationOfSquared = valueSquared.expectation();
		
		System.out.println("\tE(X)   = " + expectation.doubleValue());
		System.out.println("\tE(X^2) = " + expectationOfSquared.doubleValue());
		
		System.out.println();
	}
}
