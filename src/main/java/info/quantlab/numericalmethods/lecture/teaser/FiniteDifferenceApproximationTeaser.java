package info.quantlab.numericalmethods.lecture.teaser;

import java.util.function.Function;

/**
 * A small teaser experiment
 * showing that a naive application of a numerical method can go wrong.
 *
 * Here: the finite difference approximation of a partial derivative.
 *
 * @author Christian Fries
 */
public class FiniteDifferenceApproximationTeaser {

	public static void main(String[] args) {

		final double x = 0.0;
		final Function<Double,Double> function = Math::exp;	// Function maps Double to Double

		// Analytic derivative of exp at x
		final double derivativeAnalytic = Math.exp(x);

		// Finite difference approximation with a given shift
		final double shift = 1E-6;		// choose a suitable shift size. try 1E-15 to 1E-8
		final double derivativeApprox	= (function.apply(x + shift) - function.apply(x - shift)) / (2*shift);

		// The error
		final double error = derivativeApprox - derivativeAnalytic;

		System.out.println("derivativeApprox...... = " + derivativeApprox);
		System.out.println("derivativeAnalytic.... = " + derivativeAnalytic);
		System.out.println("error................. = " + error);
	}
}
