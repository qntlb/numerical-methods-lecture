package info.quantlab.numericalmethods.lecture.computerarithmetics;

import java.text.DecimalFormat;
import java.util.function.DoubleUnaryOperator;

import net.finmath.plots.Plot2D;

public class FiniteDifferenceExperiments {

	public static void main(String[] args) {
		
		double x = 0.0;
		
		printForwardFiniteDifferenceApproximationOfExp(x, 0.01 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.001 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 0.0001 /* shift */);

		printForwardFiniteDifferenceApproximationOfExp(x, 1E-16 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-15 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1E-12 /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, Math.pow(2, -52) /* shift */);
		printForwardFiniteDifferenceApproximationOfExp(x, 1.5*Math.pow(2, -52) /* shift */);
		
		plotForwardFiniteDifferenceApproximationErrorOfExp(x);
	}

	private static void plotForwardFiniteDifferenceApproximationErrorOfExp(double x) {
		
		DoubleUnaryOperator finiteDifferenceApproxError = scale -> {
			
			double shift = Math.pow(10, -scale);
			
			double valueUpShift = Math.exp(x + shift);
			double value = Math.exp(x);
			double finiteDifferenceApproximation = (valueUpShift-value)/shift;
			
			double derivativeAnalytic = Math.exp(x);
			
			double error = finiteDifferenceApproximation - derivativeAnalytic;
			
			return error;
		};
		
		Plot2D plot = new Plot2D(14, 16.5, 200, finiteDifferenceApproxError);
		plot.setYAxisNumberFormat(new DecimalFormat("0E00"))
		.setTitle("(One Sided Finite Difference) Derivative of exp(x) at x = " + x)
		.setXAxisLabel("scale (h = 10^{-scale}")
		.setYAxisLabel("error")
		.show();
	}

	private static void printForwardFiniteDifferenceApproximationOfExp(double x, double shift) {

		double valueUpShift = Math.exp(x + shift);
		double value = Math.exp(x);
		double finiteDifferenceApproximation = (valueUpShift-value)/shift;
		
		System.out.println("h = " + shift + "  \t  finiteDifferenceApprox = " + finiteDifferenceApproximation);
	}

}
