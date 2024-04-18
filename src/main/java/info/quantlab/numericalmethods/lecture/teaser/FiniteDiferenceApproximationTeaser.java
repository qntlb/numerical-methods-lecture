package info.quantlab.numericalmethods.lecture.teaser;

import java.util.function.Function;

/**
 * A small teaser experiment showing that a naive application of a numerical method can go wrong.
 * 
 * Here: the finite difference approximation of a partial derivative.
 * 
 * @author Christian Fries
 */
public class FiniteDiferenceApproximationTeaser {

	public static void main(String[] args) {
		
		double x = 0.0;
		Function<Double,Double> function = Math::exp;		// Function maps Double to Double

		// Analytic derivative of exp at x
		double derivativeAnalytic = Math.exp(x);

		// Finite difference approximation with a given shift
		double shift = 1E-15;			// choose a suitable shift size. try 1E-15 to 1E-8
		double derivativeApprox	= (function.apply(x + shift) - function.apply(x)) / shift;

		// The error
		double error = derivativeApprox - derivativeAnalytic;

		System.out.println("derivativeApprox...... = " + derivativeApprox);
		System.out.println("derivativeAnalytic.... = " + derivativeAnalytic);
		System.out.println("error................. = " + error);
	}
}
