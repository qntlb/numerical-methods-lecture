package info.quantlab.numericalmethods.lecture.montecarlo.randomvariable;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import net.finmath.montecarlo.RandomVariableFromDoubleArray;
import net.finmath.montecarlo.RandomVariableFromFloatArray;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.Scalar;

public class RandomVariableExperiments {

	public static void main(String[] args) {

		RandomVariable randomVariableDoublePrecision = new RandomVariableFromDoubleArray(0, new double[] { -1.0/3.0, -1.0/3.0, 0.0/3.0, 2.0/3.0 });
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

		System.out.println(Arrays.toString(randomVariable.getRealizations()));
		RandomVariable a = randomVariable.choose(Scalar.of(1.0), Scalar.of(-1.0));
		RandomVariable b = randomVariable.apply(Math::signum);
		RandomVariable c = randomVariable.apply(x -> x >= 0 ? 1.0 : -1.0);
		System.out.println(Arrays.toString(a.getRealizations()));
		System.out.println(Arrays.toString(b.getRealizations()));
		System.out.println(Arrays.toString(c.getRealizations()));
		
		class Signum implements DoubleUnaryOperator {
			@Override
			public double applyAsDouble(double operand) {
				if(operand > 0) return 1.0;
				if(operand < 0) return -1.0;
				return 0.0;
			}
			
		}
		RandomVariable d = randomVariable.apply(new Signum());
		System.out.println(Arrays.toString(d.getRealizations()));

		// Calculate E(X)
		RandomVariable expectation = randomVariable.expectation();
		System.out.println("\tE(X)   = " + expectation.doubleValue());

		// Calculate E(X^2)
		RandomVariable valueSquared = randomVariable.mult(randomVariable);
		RandomVariable expectationOfSquared = valueSquared.expectation();
		System.out.println("\tE(X^2) = " + expectationOfSquared.doubleValue());

		System.out.println();
	}
}
